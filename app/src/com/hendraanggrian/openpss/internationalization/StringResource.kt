package com.hendraanggrian.openpss.internationalization

interface StringResource {

    val resourceId: String

    fun toString(resourced: Resourced): String = resourced.getString(resourceId)
}