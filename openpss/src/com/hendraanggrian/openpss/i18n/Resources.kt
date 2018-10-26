package com.hendraanggrian.openpss.i18n

import java.util.ResourceBundle

/** Easier access to [ResourceBundle] across components. */
interface Resources {

    val resources: ResourceBundle

    val language: Language get() = Language.ofCode(resources.baseBundleName.substringAfter('_'))

    fun getString(id: String): String = resources.getString(id)

    fun getString(id: String, vararg args: String): String = getString(id).format(*args)
}