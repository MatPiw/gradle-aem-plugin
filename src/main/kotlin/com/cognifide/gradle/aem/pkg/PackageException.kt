package com.cognifide.gradle.aem.pkg

import com.cognifide.gradle.aem.common.AemException

class PackageException : AemException {

    constructor(message: String, cause: Throwable) : super(message, cause)

    constructor(message: String) : super(message)
}