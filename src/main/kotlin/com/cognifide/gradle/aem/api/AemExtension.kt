package com.cognifide.gradle.aem.api

import com.cognifide.gradle.aem.instance.Instance
import com.cognifide.gradle.aem.instance.InstanceSync
import groovy.lang.Closure
import org.gradle.api.Project
import org.gradle.util.ConfigureUtil

/**
 * Main place for providing build script DSL capabilities in case of AEM.
 */
open class AemExtension(@Transient private val project: Project) {

    val config = AemConfig(project)

    val bundle = AemBundle(project)

    val notifier = AemNotifier.of(project)

    val tasks = AemTaskFactory(project)

    val instances: List<Instance>
        get() = Instance.filter(project)

    fun instances(filter: String): List<Instance> {
        return Instance.filter(project, filter)
    }

    fun sync(synchronizer: (InstanceSync) -> Unit) {
        instances.parallelStream().forEach { it.sync(synchronizer) }
    }

    fun config(configurer: AemConfig.() -> Unit) {
        config.apply(configurer)
    }

    fun config(closure: Closure<*>) {
        config { ConfigureUtil.configure(closure, this) }
    }

    fun bundle(configurer: AemBundle.() -> Unit) {
        bundle.apply(configurer)
    }

    fun bundle(closure: Closure<*>) {
        bundle { ConfigureUtil.configure(closure, this) }
    }

    fun notifier(configurer: AemNotifier.() -> Unit) {
        notifier.apply(configurer)
    }

    fun notifier(closure: Closure<*>) {
        notifier { ConfigureUtil.configure(closure, this) }
    }

    fun tasks(configurer: AemTaskFactory.() -> Unit) {
        tasks.apply(configurer)
    }

    fun tasks(closure: Closure<*>) {
        tasks { ConfigureUtil.configure(closure, this) }
    }

    companion object {
        const val NAME = "aem"
    }

}