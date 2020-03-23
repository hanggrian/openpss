package com.hendraanggrian.openpss

import com.hendraanggrian.prefs.EditablePrefs
import com.hendraanggrian.prefs.ReadablePrefs
import com.hendraanggrian.prefs.WritablePrefs

fun EditablePrefs<*>.setDefault() {
    edit {
        if (Setting.KEY_LANGUAGE !in this@setDefault) this[Setting.KEY_LANGUAGE] = Language.ENGLISH.fullCode
        if (Setting.KEY_SERVER_HOST !in this@setDefault) this[Setting.KEY_SERVER_HOST] = "localhost"
        if (Setting.KEY_SERVER_PORT !in this@setDefault) this[Setting.KEY_SERVER_PORT] = "8080"
        if (Setting.KEY_EMPLOYEE !in this@setDefault) this[Setting.KEY_EMPLOYEE] = ""
    }
}

fun WritablePrefs.setDefault() {
    if (Setting.KEY_LANGUAGE !in this) this[Setting.KEY_LANGUAGE] = Language.ENGLISH.fullCode
    if (Setting.KEY_SERVER_HOST !in this) this[Setting.KEY_SERVER_HOST] = "localhost"
    if (Setting.KEY_SERVER_PORT !in this) this[Setting.KEY_SERVER_PORT] = "8080"
    if (Setting.KEY_EMPLOYEE !in this) this[Setting.KEY_EMPLOYEE] = ""
}

val ReadablePrefs.language: Language get() = Language.ofFullCode(get(Setting.KEY_LANGUAGE)!!)

object Setting {
    const val KEY_LANGUAGE = "language"
    const val KEY_SERVER_HOST = "server_host"
    const val KEY_SERVER_PORT = "server_port"
    const val KEY_EMPLOYEE = "employee"
}
