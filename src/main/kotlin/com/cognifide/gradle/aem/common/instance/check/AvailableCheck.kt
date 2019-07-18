package com.cognifide.gradle.aem.common.instance.check

import com.cognifide.gradle.aem.common.instance.LocalInstance
import com.cognifide.gradle.aem.common.instance.local.Status
import java.util.concurrent.TimeUnit

class AvailableCheck(group: CheckGroup) : DefaultCheck(group) {

    /**
     * Status of remote instances cannot be checked easily. Because of that, check will work just a little bit longer.
     */
    var utilisationTime = TimeUnit.SECONDS.toMillis(15)

    override fun check() {
        // todo different logic here
        /*val bundleState = state(sync.osgiFramework.determineBundleState())
        if (bundleState.unknown) {
            statusLogger.error(
                    "Bundles stable (${bundleState.stablePercent})",
                    "Bundles stable (${bundleState.stablePercent}). HTTP server still responding on $instance"
            )
            return
        }

        if (instance is LocalInstance) {
            val status = state(instance.checkStatus())
            if (!STATUS_EXPECTED.contains(status)) {
                statusLogger.error(
                        "Awaiting not running",
                        "Unexpected instance status '$status'. Waiting for status '$STATUS_EXPECTED' of $instance"
                )
            }
        } else {
            if (utilisationTime !in 0..progress.stateTime) {
                statusLogger.error(
                        "Awaiting utilized",
                        "HTTP server not responding. Waiting for utilization (port releasing) of $instance"
                )
            }
        }*/
    }

    companion object {
        val STATUS_EXPECTED = listOf(Status.RUNNING)
    }
}