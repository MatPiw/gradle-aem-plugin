package com.cognifide.gradle.aem.instance

import com.cognifide.gradle.aem.AemConfig
import com.cognifide.gradle.aem.AemException
import com.cognifide.gradle.aem.internal.Formats
import com.cognifide.gradle.aem.internal.PropertyParser
import com.fasterxml.jackson.annotation.JsonIgnore
import org.gradle.api.Project
import java.io.Serializable
import java.net.URL
import kotlin.reflect.KClass

interface Instance : Serializable {

    companion object {

        val FILTER_ANY = PropertyParser.FILTER_DEFAULT

        val FILTER_LOCAL = "local-*"

        val FILTER_AUTHOR = "*-author"

        val ENVIRONMENT_CMD = "cmd"

        val URL_AUTHOR_DEFAULT = "http://localhost:4502"

        val URL_PUBLISH_DEFAULT = "http://localhost:4503"

        val USER_DEFAULT = "admin"

        val PASSWORD_DEFAULT = "admin"

        val LIST_PROP = "aem.deploy.instance.list"

        val NAME_PROP = "aem.deploy.instance.name"

        fun parse(str: String): List<Instance> {
            return str.split(";").map { line ->
                val parts = line.split(",")

                when (parts.size) {
                    4 -> {
                        val (url, type, user, password) = parts
                        RemoteInstance(url, user, password, ENVIRONMENT_CMD, type)
                    }
                    3 -> {
                        val (url, user, password) = parts
                        RemoteInstance(url, user, password, ENVIRONMENT_CMD, InstanceType.byUrl(url).name)
                    }
                    else -> {
                        throw AemException("Cannot parse instance string: '$str'")
                    }
                }
            }
        }

        fun defaults(): List<Instance> {
            return listOf(
                    LocalInstance(URL_AUTHOR_DEFAULT),
                    LocalInstance(URL_PUBLISH_DEFAULT)
            )
        }

        fun filter(project: Project): List<Instance> {
            return filter(project, AemConfig.of(project).deployInstanceFilter)
        }

        fun filter(project: Project, instanceFilter: String): List<Instance> {
            val config = AemConfig.of(project)
            val instanceValues = project.properties[LIST_PROP] as String?
            if (!instanceValues.isNullOrBlank()) {
                return parse(instanceValues!!)
            }

            val instances = if (config.instances.values.isEmpty()) {
                return defaults()
            } else {
                config.instances
            }

            return instances.values.filter { instance ->
                PropertyParser(project).filter(instance.name, instanceFilter)
            }
        }

        @Suppress("unchecked_cast")
        fun <T : Instance> filter(project: Project, type: KClass<T>): List<T> {
            return filter(project).fold(mutableListOf(), { result, instance ->
                if (type.isInstance(instance)) result += (instance as T); result
            })
        }

        fun locals(project: Project): List<LocalInstance> {
            return filter(project, LocalInstance::class)
        }

        fun remotes(project: Project): List<RemoteInstance> {
            return filter(project, RemoteInstance::class)
        }

        fun portOfUrl(url: String): Int {
            return URL(url).port
        }

    }

    val httpUrl: String

    val httpPort: Int
        get() = portOfUrl(httpUrl)

    val user: String

    val password: String

    @get:JsonIgnore
    val hiddenPassword: String
        get() = "*".repeat(password.length)

    val environment: String

    val typeName: String

    val type: InstanceType
        get() = InstanceType.byName(typeName)

    val credentials: String
        get() = "$user:$password"

    val name: String
        get() = "$environment-$typeName"

    fun validate() {
        if (!Formats.URL_VALIDATOR.isValid(httpUrl)) {
            throw AemException("Malformed URL address detected in $this")
        }

        if (user.isBlank()) {
            throw AemException("User cannot be blank in $this")
        }

        if (password.isBlank()) {
            throw AemException("Password cannot be blank in $this")
        }

        if (environment.isBlank()) {
            throw AemException("Environment cannot be blank in $this")
        }

        if (typeName.isBlank()) {
            throw AemException("Type cannot be blank in $this")
        }
    }

}