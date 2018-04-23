package com.hendraanggrian.openpss.core.fx

import java.util.ResourceBundle

/** Easier access to [ResourceBundle] across components. */
interface Resourced {

    val resources: ResourceBundle

    fun getString(id: String): String = resources.getString(id)
}