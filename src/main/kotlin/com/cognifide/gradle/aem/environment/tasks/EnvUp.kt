package com.cognifide.gradle.aem.environment.tasks

import com.cognifide.gradle.aem.environment.ServiceAwait
import com.cognifide.gradle.aem.environment.docker.DockerTask
import org.gradle.api.tasks.Internal
import org.gradle.api.tasks.TaskAction

open class EnvUp : DockerTask() {

    init {
        description = "Starts additional services for local environment " +
                "- based on provided docker compose file."
    }

    @Internal
    private val serviceAwait = ServiceAwait(aem)

    private val upDelay = aem.retry { afterSecond(options.upDelay) }

    @TaskAction
    fun up() {
        stack.deploy()
        serviceAwait.await(upDelay)
    }

    companion object {
        const val NAME = "aemEnvUp"
    }
}
