@file:Suppress("NOTHING_TO_INLINE")

package com.hendraanggrian.openpss.util

import javafx.scene.control.ListView

inline fun <T> ListView<T>.forceRefresh() = items.let {
    items = null
    items = it
}