@file:Suppress("NOTHING_TO_INLINE")

package com.hendraanggrian.openpss.control

import javafx.scene.control.ListView
import ktfx.coroutines.listener
import ktfx.layouts.LayoutMarker
import ktfx.layouts.NodeManager

class UnselectableListView<T> : ListView<T>() {

    init {
        selectionModel.run {
            selectedItemProperty().listener { _, _, value ->
                if (value != null) {
                    clearSelection()
                }
            }
        }
    }
}

fun <T> unselectableListView(
    init: ((@LayoutMarker UnselectableListView<T>).() -> Unit)? = null
): UnselectableListView<T> = UnselectableListView<T>().also { init?.invoke(it) }

inline fun <T> NodeManager.selectionListView(
    noinline init: ((@LayoutMarker UnselectableListView<T>).() -> Unit)? = null
): UnselectableListView<T> = com.hendraanggrian.openpss.control.unselectableListView(init).add()