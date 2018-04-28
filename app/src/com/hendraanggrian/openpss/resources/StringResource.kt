package com.hendraanggrian.openpss.resources

interface StringResource {

    val resourceId: String

    fun toString(resourced: Resourced): String = resourced.getString(resourceId)
}