package com.hendraanggrian.openpss.ui

import java.util.ResourceBundle

/** Easier access to [ResourceBundle] across components. */
interface Resourced {

    val resources: ResourceBundle

    fun getString(id: String): String = resources.getString(id)
}