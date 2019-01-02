package com.hendraanggrian.openpss

import androidx.annotation.CallSuper

/**
 * @param E editor
 */
interface Setting<E> {

    companion object {
        const val KEY_LANGUAGE = "language"
        const val KEY_SERVER_HOST = "server_host"
        const val KEY_SERVER_PORT = "server_port"
        const val KEY_EMPLOYEE = "employee"
    }

    operator fun contains(key: String): Boolean

    fun getString(key: String): String

    fun getInt(key: String): Int = getString(key).toInt()

    fun getEditor(): E

    @CallSuper
    fun setDefault(editor: E) {
        if (KEY_LANGUAGE !in this) {
            editor[KEY_LANGUAGE] = Language.EN_US.fullCode
        }
        if (KEY_SERVER_HOST !in this) {
            editor[KEY_SERVER_HOST] = "localhost"
        }
        if (KEY_SERVER_PORT !in this) {
            editor[KEY_SERVER_PORT] = "8080"
        }
        if (KEY_EMPLOYEE !in this) {
            editor[KEY_EMPLOYEE] = ""
        }
    }

    operator fun E.set(key: String, value: String)

    fun E.save()

    fun edit(edit: E.() -> Unit) = getEditor().apply { edit() }.save()

    fun editDefault() = edit { setDefault(this) }

    val language: Language get() = Language.ofFullCode(getString(KEY_LANGUAGE))
}