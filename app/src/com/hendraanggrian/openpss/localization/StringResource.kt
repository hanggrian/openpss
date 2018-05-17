package com.hendraanggrian.openpss.localization

interface StringResource {

    val resourceId: String

    fun toString(resourced: Resourced): String = resourced.getString(resourceId)
}