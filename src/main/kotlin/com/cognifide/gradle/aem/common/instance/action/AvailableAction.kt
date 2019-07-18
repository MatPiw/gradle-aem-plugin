package com.cognifide.gradle.aem.common.instance.action

import com.cognifide.gradle.aem.AemExtension
import com.cognifide.gradle.aem.common.instance.check.AvailableCheck
import com.cognifide.gradle.aem.common.instance.check.CheckRunner
import com.cognifide.gradle.aem.common.instance.names
import java.util.concurrent.TimeUnit

/**
 * Check if given instance is reachable.
 */
class AvailableAction(aem: AemExtension) : AnyInstanceAction(aem) {

    private var availableOptions: AvailableCheck.() -> Unit = {
        utilisationTime = aem.props.long("instance.available.utilizationTime")
                ?: TimeUnit.SECONDS.toMillis(10)
    }

    fun available(options: AvailableCheck.() -> Unit) {
        availableOptions = options
    }

    private val runner = CheckRunner(aem).apply {
        delay = aem.props.long("instance.available.delay") ?: TimeUnit.SECONDS.toMillis(1)
        verbose = aem.props.boolean("instance.available.verbose") ?: true

        checks {
            listOf(available(availableOptions))
        }
    }

    fun runner(options: CheckRunner.() -> Unit) {
        runner.apply(options)
    }

    override fun perform() {
        if (!enabled) {
            return
        }

        if (instances.isEmpty()) {
            aem.logger.info("No instances to check.")
            return
        }

        aem.logger.info("Checking instance(s): ${instances.names}")

        runner.check(instances)
    }
}