package com.hendraanggrian.openpss

import java.util.ResourceBundle

/** Easier access to [ResourceBundle] across components. */
interface StringResources {

    val resourceBundle: ResourceBundle

    val language: Language get() = Language.ofCode(resourceBundle.locale.language)

    fun getString(id: String): String = resourceBundle.getString(id)

    fun getString(id: String, vararg args: Any): String = getString(id).format(*args)

    /** Mark enum value to be translatable. */
    interface Enum {

        val resourceId: String

        fun toString(resources: StringResources): String = resources.getString(resourceId)
    }
}
