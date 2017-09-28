package com.cognifide.gradle.aem.deploy

import com.cognifide.gradle.aem.AemTask
import com.cognifide.gradle.aem.instance.Instance
import org.gradle.api.tasks.TaskAction

open class DistributeTask : SyncTask() {

    companion object {
        val NAME = "aemDistribute"
    }

    init {
        group = AemTask.GROUP
        description = "Distributes CRX package to instance(s). Upload, install then activate only for instances with group '${Instance.FILTER_AUTHOR}'."
    }

    @TaskAction
    fun distribute() {
        synchronizeInstances({ it.distributePackage() }, Instance.filter(project, Instance.FILTER_AUTHOR))
    }

}