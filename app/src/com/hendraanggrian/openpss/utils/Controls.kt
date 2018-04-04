@file:Suppress("NOTHING_TO_INLINE")

package com.hendraanggrian.openpss.utils

import javafx.scene.control.ChoiceBox
import javafx.scene.control.ListView

inline fun <T> ListView<T>.forceRefresh() = items.let {
    items = null
    items = it
}

@Suppress("UNCHECKED_CAST")
inline fun <T> ChoiceBox<*>.get(): T = value as T