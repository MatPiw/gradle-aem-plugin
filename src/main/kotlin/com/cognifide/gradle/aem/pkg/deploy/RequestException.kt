package com.cognifide.gradle.aem.pkg.deploy

class RequestException : DeployException {

    constructor(message: String, cause: Throwable) : super(message, cause)

    constructor(message: String) : super(message)

}