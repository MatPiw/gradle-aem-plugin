package com.cognifide.gradle.aem.jar

import com.cognifide.gradle.aem.AemConfig
import com.cognifide.gradle.aem.AemPlugin
import com.cognifide.gradle.aem.AemTask
import org.dm.gradle.plugins.bundle.BundleExtension
import org.gradle.api.DefaultTask
import org.gradle.api.plugins.JavaPlugin
import org.gradle.api.plugins.osgi.OsgiManifest
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.InputFiles
import org.gradle.api.tasks.Internal
import org.gradle.api.tasks.TaskAction
import org.gradle.jvm.tasks.Jar
import java.io.File

/**
 * Update manifest being used by 'jar' task of Java Plugin.
 *
 * Both plugins 'osgi' and 'org.dm.bundle' are supported.
 * Dependency embedding does not work when official 'osgi' plugin is used.
 *
 * @see <https://issues.gradle.org/browse/GRADLE-1107>
 * @see <https://github.com/TomDmitriev/gradle-bundle-plugin>
 */
open class UpdateManifestTask : DefaultTask(), AemTask {

    companion object {
        val NAME = "aemUpdateManifest"

        val BUNDLE_CLASSPATH_INSTRUCTION = "Bundle-ClassPath"

        val OSGI_PLUGIN_ID = "osgi"

        val BUNDLE_PLUGIN_ID = "org.dm.bundle"
    }

    init {
        group = AemPlugin.TASK_GROUP
        description = "Update OSGi manifest instructions"
    }

    @Input
    final override val config = AemConfig.extend(project)

    @Internal
    val jar = project.tasks.getByName(JavaPlugin.JAR_TASK_NAME) as Jar

    val embeddableJars: List<File>
        @InputFiles
        get() {
            return jar.project.configurations.getByName(AemPlugin.CONFIG_EMBED).files.sortedBy { it.name }
        }

    @TaskAction
    fun updateManifest() {
        includeEmbedJars()
    }

    private fun includeEmbedJars() {
        if (embeddableJars.isEmpty()) {
            return
        }

        if (osgiPluginApplied()) {
            project.logger.warn("As of Gradle 3.5, jar embedding does not work when '$OSGI_PLUGIN_ID' plugin is used."
                    + " Consider using '$BUNDLE_PLUGIN_ID' instead.")
        }

        project.logger.info("Embedding jar files: ${embeddableJars.map { it.name }}")

        if (embeddableJars.isNotEmpty()) {
            addInstruction(BUNDLE_CLASSPATH_INSTRUCTION, {
                val list = mutableListOf(".")
                embeddableJars.onEach { jar -> list.add(jar.name) }
                list.joinToString(",")
            })
            jar.from(embeddableJars)
        }
    }

    private fun addInstruction(name: String, valueProvider: () -> String) {
        if (osgiPluginApplied()) {
            addInstruction(jar.manifest as OsgiManifest, name, valueProvider())
        } else if (bundlePluginApplied()) {
            addInstruction(project.extensions.getByType(BundleExtension::class.java), name, valueProvider())
        } else {
            project.logger.warn("Cannot apply specific OSGi instruction to JAR manifest, because neither "
                    + "'$OSGI_PLUGIN_ID' nor '$BUNDLE_PLUGIN_ID' are applied to project '${project.name}'.")
        }
    }

    private fun addInstruction(manifest: OsgiManifest, name: String, value: String) {
        if (!manifest.instructions.containsKey(name)) {
            if (!value.isNullOrBlank()) {
                manifest.instruction(name, value)
            }
        }
    }

    private fun osgiPluginApplied() = project.plugins.hasPlugin(OSGI_PLUGIN_ID)

    private fun bundlePluginApplied() = project.plugins.hasPlugin(BUNDLE_PLUGIN_ID)

    @Suppress("unchecked_cast")
    private fun addInstruction(config: BundleExtension, name: String, value: String) {
        val instructions = config.instructions as Map<String, Any>
        if (!instructions.contains(name)) {
            if (!value.isNullOrBlank()) {
                config.instruction(name, value)
            }
        }
    }

}