@file:Suppress("NOTHING_TO_INLINE")

package com.hendraanggrian.openpss.utils

import com.hendraanggrian.openpss.R
import javafx.scene.Scene
import javafx.scene.control.Dialog

inline fun Scene.style() {
    stylesheets += getResource(R.style.openpss).toExternalForm()
}

inline fun Dialog<*>.style() {
    dialogPane.stylesheets += getResource(R.style.openpss).toExternalForm()
}