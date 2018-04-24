package com.hendraanggrian.openpss.resources

interface StringResource2 {

    val textId: String

    fun toString(resourced: Resourced): String = resourced.getString(textId)
}