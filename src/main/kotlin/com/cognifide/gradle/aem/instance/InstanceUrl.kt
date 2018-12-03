package com.cognifide.gradle.aem.instance

import com.cognifide.gradle.aem.common.AemException
import java.net.MalformedURLException
import java.net.URL
import java.net.URLEncoder

class InstanceUrl(raw: String) {

    val config = URL(raw)

    val user: String
        get() = userPart(0) ?: Instance.USER_DEFAULT

    val password: String
        get() = userPart(1) ?: Instance.PASSWORD_DEFAULT

    val httpUrl: String
        get() = if (config.port != -1) {
            "${config.protocol}://${config.host}:${config.port}"
        } else {
            "${config.protocol}://${config.host}"
        }

    val httpBasicAuthUrl: String
        get() = httpBasicAuthUrl(user, password)

    fun httpBasicAuthUrl(user: String, password: String): String {
        val userInfo = "${encode(user)}:${encode(password)}"

        return if (config.port != -1) {
            "${config.protocol}://$userInfo@${config.host}:${config.port}"
        } else {
            "${config.protocol}://$userInfo@${config.host}"
        }
    }

    val httpPort: Int
        get() = if (config.port != -1) {
            config.port
        } else {
            if (config.protocol == "https") {
                HTTPS_PORT
            } else {
                HTTP_PORT
            }
        }

    val typeName: String
        get() = InstanceType.nameByUrl(httpUrl)

    val type: InstanceType
        get() = InstanceType.byUrl(httpUrl)

    val debugPort: Int
        get() = if (config.port != -1) {
            "1$httpPort".toInt()
        } else {
            if (config.protocol == "https") {
                HTTPS_DEBUG_PORT
            } else {
                HTTP_DEBUG_PORT
            }
        }

    private fun userPart(index: Int): String? {
        return config.userInfo?.split(":")
                ?.takeIf { it.size == 2 }
                ?.get(index)
    }

    companion object {

        const val HTTPS_PORT = 443

        const val HTTPS_DEBUG_PORT = 50443

        const val HTTP_PORT = 80

        const val HTTP_DEBUG_PORT = 50080

        fun encode(text: String): String {
            return URLEncoder.encode(text, Charsets.UTF_8.name()) ?: text
        }

        fun parse(raw: String): InstanceUrl {
            return try {
                InstanceUrl(raw)
            } catch (e: MalformedURLException) {
                throw AemException("Cannot parse instance URL: '$raw'", e)
            }
        }
    }
}