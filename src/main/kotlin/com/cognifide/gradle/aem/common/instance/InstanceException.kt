package com.cognifide.gradle.aem.common.instance

import com.cognifide.gradle.aem.AemException

open class InstanceException : AemException {

    constructor(message: String, cause: Throwable) : super(message, cause)

    constructor(message: String) : super(message)
}
