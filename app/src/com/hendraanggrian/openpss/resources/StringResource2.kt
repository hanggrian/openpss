package com.hendraanggrian.openpss.resources

interface StringResource2 {

    val resourceId: String

    fun toString(resourced: Resourced): String = resourced.getString(resourceId)
}