package com.hendraanggrian.openpss.content

import java.util.ResourceBundle

/** Easier access to [ResourceBundle] across components. */
interface Resources {

    val resources: ResourceBundle

    val language: Language
        get() = Language.ofCode(
            resources.baseBundleName.substringAfter('_')
        )

    fun getString(id: String): String = resources.getString(id)

    fun getString(id: String, vararg args: Any): String = getString(id).format(*args)

    /** Mark enum values to be translatable. */
    interface Enum {

        val resourceId: String

        fun toString(resources: Resources): String = resources.getString(resourceId)
    }
}