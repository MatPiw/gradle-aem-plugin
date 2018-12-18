package com.cognifide.gradle.aem.tooling

import com.cognifide.gradle.aem.common.AemPlugin
import com.cognifide.gradle.aem.common.TaskFacade
import com.cognifide.gradle.aem.config.ConfigPlugin
import com.cognifide.gradle.aem.tooling.tasks.*
import com.cognifide.gradle.aem.tooling.tasks.Debug
import org.gradle.api.Project
import org.gradle.language.base.plugins.LifecycleBasePlugin

/**
 * Provides configuration used by both package and instance plugins.
 */
class ToolingPlugin : AemPlugin() {

    override fun Project.configure() {
        setupDependentPlugins()
        setupTasks()
    }

    private fun Project.setupDependentPlugins() {
        plugins.apply(ConfigPlugin::class.java)
    }

    private fun Project.setupTasks() {
        with(TaskFacade(this)) {
            register(Debug.NAME, Debug::class.java) {
                it.dependsOn(LifecycleBasePlugin.BUILD_TASK_NAME)
            }
            register(Rcp.NAME, Rcp::class.java)
            register(Vlt.NAME, Vlt::class.java) {
                it.mustRunAfter(LifecycleBasePlugin.CLEAN_TASK_NAME)
            }
            register(Sync.NAME, Sync::class.java) {
                it.mustRunAfter(LifecycleBasePlugin.CLEAN_TASK_NAME)
            }
        }
    }

    companion object {
        const val ID = "com.cognifide.aem.tooling"
    }
}