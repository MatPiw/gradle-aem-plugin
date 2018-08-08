package com.cognifide.gradle.aem.base

import com.cognifide.gradle.aem.api.AemExtension
import com.cognifide.gradle.aem.api.AemPlugin
import com.cognifide.gradle.aem.base.copy.CopyTask
import com.cognifide.gradle.aem.base.debug.DebugTask
import com.cognifide.gradle.aem.base.vlt.*
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.plugins.BasePlugin
import org.gradle.language.base.plugins.LifecycleBasePlugin

/**
 * Provides configuration used by both package and instance plugins.
 */
class BasePlugin : Plugin<Project> {

    override fun apply(project: Project) {
        with(project, {
            setupGreet()
            setupDependentPlugins()
            setupExtensions()
            setupTasks()
        })
    }

    private fun Project.setupGreet() {
        AemPlugin.once { logger.info("Using: ${AemPlugin.NAME_WITH_VERSION}") }
    }

    private fun Project.setupDependentPlugins() {
        plugins.apply(BasePlugin::class.java)
    }

    private fun Project.setupExtensions() {
        extensions.create(AemExtension.NAME, AemExtension::class.java, this)
    }

    private fun Project.setupTasks() {
        tasks.create(DebugTask.NAME, DebugTask::class.java)
        tasks.create(RcpTask.NAME, RcpTask::class.java)
        tasks.create(CopyTask.NAME, CopyTask::class.java)

        val clean = tasks.create(CleanTask.NAME, CleanTask::class.java)
        val vlt = tasks.create(VltTask.NAME, VltTask::class.java)
        val checkout = tasks.create(CheckoutTask.NAME, CheckoutTask::class.java)
        val sync = tasks.create(SyncTask.NAME, SyncTask::class.java)

        val baseClean = tasks.getByName(LifecycleBasePlugin.CLEAN_TASK_NAME)

        clean.mustRunAfter(baseClean, checkout)
        vlt.mustRunAfter(baseClean)
        checkout.mustRunAfter(baseClean)
        sync.dependsOn(checkout, clean).mustRunAfter(baseClean)
    }

    companion object {
        const val PKG = "com.cognifide.gradle.aem"

        const val ID = "com.cognifide.aem.base"
    }

}