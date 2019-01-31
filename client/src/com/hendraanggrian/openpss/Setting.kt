package com.hendraanggrian.openpss

import com.hendraanggrian.defaults.Defaults
import com.hendraanggrian.defaults.ReadableDefaults
import com.hendraanggrian.defaults.WritableDefaults

fun Defaults<*>.setDefault() {
    invoke {
        if (Setting.KEY_LANGUAGE !in this) {
            it[Setting.KEY_LANGUAGE] = Language.EN_US.fullCode
        }
        if (Setting.KEY_SERVER_HOST !in this) {
            it[Setting.KEY_SERVER_HOST] = "localhost"
        }
        if (Setting.KEY_SERVER_PORT !in this) {
            it[Setting.KEY_SERVER_PORT] = "8080"
        }
        if (Setting.KEY_EMPLOYEE !in this) {
            it[Setting.KEY_EMPLOYEE] = ""
        }
    }
}

fun WritableDefaults.setDefault() {
    if (Setting.KEY_LANGUAGE !in this) {
        this[Setting.KEY_LANGUAGE] = Language.EN_US.fullCode
    }
    if (Setting.KEY_SERVER_HOST !in this) {
        this[Setting.KEY_SERVER_HOST] = "localhost"
    }
    if (Setting.KEY_SERVER_PORT !in this) {
        this[Setting.KEY_SERVER_PORT] = "8080"
    }
    if (Setting.KEY_EMPLOYEE !in this) {
        this[Setting.KEY_EMPLOYEE] = ""
    }
}

val ReadableDefaults.language: Language get() = Language.ofFullCode(get(Setting.KEY_LANGUAGE)!!)

object Setting {
    const val KEY_LANGUAGE = "language"
    const val KEY_SERVER_HOST = "server_host"
    const val KEY_SERVER_PORT = "server_port"
    const val KEY_EMPLOYEE = "employee"
}