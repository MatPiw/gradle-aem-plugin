package com.cognifide.gradle.aem.instance

import com.cognifide.gradle.aem.base.BasePlugin
import com.cognifide.gradle.aem.pkg.ComposeTask
import com.cognifide.gradle.aem.pkg.PackagePlugin
import com.cognifide.gradle.aem.pkg.deploy.DeployTask
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.language.base.plugins.LifecycleBasePlugin

/**
 * Separate plugin which provides tasks for managing local instances.
 * Most often should be applied only to one project in build.
 * Applying it multiple times to same configuration could case confusing errors like AEM started multiple times.
 */
class InstancePlugin : Plugin<Project> {

    companion object {
        val ID = "com.cognifide.aem.instance"

        val FILES_PATH = "local-instance"
    }

    override fun apply(project: Project) {
        setupDependentPlugins(project)
        setupTasks(project)
    }

    private fun setupDependentPlugins(project: Project) {
        project.plugins.apply(BasePlugin::class.java)
    }

    private fun setupTasks(project: Project) {
        val clean = project.tasks.getByName(LifecycleBasePlugin.CLEAN_TASK_NAME)

        val create = project.tasks.create(CreateTask.NAME, CreateTask::class.java)
        val destroy = project.tasks.create(DestroyTask.NAME, DestroyTask::class.java)
        val up = project.tasks.create(UpTask.NAME, UpTask::class.java)
        val down = project.tasks.create(DownTask.NAME, DownTask::class.java)
        val reload = project.tasks.create(ReloadTask.NAME, ReloadTask::class.java)
        val satisfy = project.tasks.create(SatisfyTask.NAME, SatisfyTask::class.java)
        val await = project.tasks.create(AwaitTask.NAME, AwaitTask::class.java)
        val tail = project.tasks.create(TailTask.NAME, TailTask::class.java)
        val collect = project.tasks.create(CollectTask.NAME, CollectTask::class.java)

        create.mustRunAfter(clean)
        up.mustRunAfter(clean)
        up.dependsOn(create)
        destroy.mustRunAfter(down)
        satisfy.mustRunAfter(create, up)
        collect.mustRunAfter(satisfy)

        project.plugins.withId(PackagePlugin.ID, {
            val setup = project.tasks.create(SetupTask.NAME, SetupTask::class.java)
            val deploy = project.tasks.getByName(DeployTask.NAME)

            setup.dependsOn(create, up, satisfy, deploy, await)

            deploy.mustRunAfter(create, up, satisfy)
            reload.mustRunAfter(deploy, satisfy)
            await.mustRunAfter(deploy)
            tail.mustRunAfter(await)
            tail.mustRunAfter(deploy)
        })

        project.gradle.afterProject { subproject ->
            if (subproject.plugins.hasPlugin(PackagePlugin.ID)) {
                collect.dependsOn(subproject.tasks.getByName(ComposeTask.NAME))
            }
        }
    }

}