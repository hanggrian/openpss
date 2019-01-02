package com.hendraanggrian.openpss

/**
 * @param E editor
 */
interface Setting<E> {

    fun getString(key: String): String?

    fun getEditor(): E

    fun E.save()

    fun edit(edit: E.() -> Unit) = getEditor().apply { edit() }.save()
}