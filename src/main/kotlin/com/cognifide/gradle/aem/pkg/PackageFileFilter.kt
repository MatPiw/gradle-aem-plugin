package com.cognifide.gradle.aem.pkg

import aQute.bnd.osgi.Jar
import com.cognifide.gradle.aem.common.AemExtension
import com.cognifide.gradle.aem.common.Patterns
import com.cognifide.gradle.aem.common.file.FileContentReader
import com.cognifide.gradle.aem.instance.Bundle
import java.io.File
import java.io.Serializable
import org.gradle.api.Project
import org.gradle.api.file.CopySpec
import org.gradle.api.tasks.Input

class PackageFileFilter(project: Project) : Serializable {

    private val aem = AemExtension.of(project)

    @Input
    var excluding: Boolean = true

    /**
     * Exclude files being a part of CRX package.
     */
    @Input
    var excludeFiles: List<String> = EXCLUDE_FILES_DEFAULT

    @Input
    var expanding: Boolean = true

    /**
     * Wildcard file name filter expression that is used to filter in which Vault files properties can be injected.
     */
    @Input
    var expandFiles: List<String> = EXPAND_FILES_DEFAULT

    /**
     * Define here custom properties that can be used in CRX package files like 'META-INF/vault/properties.xml'.
     * Could override predefined properties provided by plugin itself.
     */
    @Input
    var expandProperties: Map<String, Any> = mapOf()

    fun expandProperty(name: String, value: String) { expandProperties += mapOf(name to value) }

    /**
     * Filter that ensures that only OSGi bundles will be put into CRX package under install path.
     */
    @Input
    var bundleChecking: Boolean = true

    fun filter(spec: CopySpec, expandProperties: Map<String, Any> = mapOf()) {
        if (excluding) {
            spec.exclude(excludeFiles)
        }

        spec.eachFile { fileDetail ->
            val path = "/${fileDetail.relativePath.pathString.removePrefix("/")}"

            if (expanding && Patterns.wildcard(path, expandFiles)) {
                FileContentReader.filter(fileDetail) {
                    aem.props.expandPackage(it, expandProperties + this.expandProperties, path)
                }
            }

            if (bundleChecking && Patterns.wildcard(path, "**/install/*.jar")) {
                val bundle = fileDetail.file
                if (!isBundle(bundle)) {
                    aem.logger.warn("Jar being a part of composed CRX package is not a valid OSGi bundle: $bundle")
                    fileDetail.exclude()
                }
            }
        }
    }

    @Suppress("TooGenericExceptionCaught")
    private fun isBundle(bundle: File): Boolean {
        return try {
            val manifest = Jar(bundle).manifest.mainAttributes
            !manifest.getValue(Bundle.ATTRIBUTE_SYMBOLIC_NAME).isNullOrBlank()
        } catch (e: Exception) {
            false
        }
    }

    companion object {
        val EXPAND_FILES_DEFAULT = listOf(
                "**/${Package.META_PATH}/*.xml",
                "**/${Package.META_PATH}/*.MF",
                "**/${Package.META_PATH}/*.cnd"
        )

        val EXCLUDE_FILES_DEFAULT = listOf(
                "**/.gradle",
                "**/.git",
                "**/.git/**",
                "**/.gitattributes",
                "**/.gitignore",
                "**/.gitmodules",
                "**/.vlt",
                "**/.vlt*.tmp",
                "**/node_modules/**",
                "jcr_root/.vlt-sync-config.properties"
        )

        val FILE_PROPERTIES = mapOf(
                "compose" to mapOf(
                        "vaultFilters" to mapOf<String, String>(),
                        "vaultNodeTypesLibs" to listOf<String>(),
                        "vaultNodeTypesLines" to listOf<String>(),
                        "vaultProperties" to mapOf(
                                "acHandling" to "merge_preserve",
                                "requiresRoot" to false
                        )
                )
        )
    }
}