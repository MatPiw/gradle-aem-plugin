package com.cognifide.gradle.aem.instance.tail.io

import com.cognifide.gradle.aem.common.http.RequestException
import com.cognifide.gradle.aem.common.instance.Instance
import com.cognifide.gradle.aem.instance.tail.InstanceTailer
import com.cognifide.gradle.aem.instance.tail.LogSource
import java.io.BufferedReader

class UrlSource(
    private val tailer: InstanceTailer,
    private val instance: Instance
) : LogSource {

    private val logger = tailer.aem.logger

    private var wasStable = true

    override fun <T> readChunk(parser: (BufferedReader) -> List<T>) =
        handleInstanceUnavailability {
            instance.sync.http {
                get(tailer.errorLogEndpoint(instance)) {
                    asStream(it).bufferedReader().use(parser)
                }
            }
        }

    private fun <T> handleInstanceUnavailability(parser: () -> List<T>) =
        try {
            val chunk = parser()
            if (!wasStable) {
                logger.info("Tailing resumed for $instance")
                wasStable = true
            }
            chunk
        } catch (ex: RequestException) {
            if (wasStable) {
                logger.warn("Tailing paused for $instance due to '${ex.message}'. Waiting for resuming.")
            }
            wasStable = false
            emptyList<T>()
        }
}