package com.hendraanggrian.openpss.i18n

/** Mark enum values to be translatable. */
interface Resourced {

    val resourceId: String

    fun toString(resources: Resources): String = resources.getString(resourceId)
}