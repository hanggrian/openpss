package com.hendraanggrian.openpss.i18n

interface StringResource {

    val resourceId: String

    fun toString(resourced: Resourced): String = resourced.getString(resourceId)
}