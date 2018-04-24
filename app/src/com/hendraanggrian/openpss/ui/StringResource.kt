package com.hendraanggrian.openpss.ui

open class StringResource(private val s: String) {

    constructor(resourced: Resourced, id: String) : this(resourced.getString(id))

    override fun toString(): String = s
}