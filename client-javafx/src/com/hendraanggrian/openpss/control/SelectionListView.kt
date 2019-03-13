@file:Suppress("NOTHING_TO_INLINE")

package com.hendraanggrian.openpss.control

import com.jfoenix.controls.JFXListView
import ktfx.controls.isNotSelected
import ktfx.coroutines.listener
import ktfx.layouts.LayoutMarker
import ktfx.layouts.NodeManager

class SelectionListView<T> : JFXListView<T>() {

    companion object {
        const val ROW_HEIGHT = 40
    }

    init {
        selectionModel.run {
            items.listener {
                minHeight = items.size * ROW_HEIGHT + 2.0
                if (items.isNotEmpty()) selectFirst()
            }
            selectedItemProperty().listener { _, oldItem, _ -> if (isNotSelected()) select(oldItem) }
        }
    }
}

fun <T> selectionListView(
    init: ((@LayoutMarker SelectionListView<T>).() -> Unit)? = null
): SelectionListView<T> = SelectionListView<T>().also { init?.invoke(it) }

inline fun <T> NodeManager.selectionListView(
    noinline init: ((@LayoutMarker SelectionListView<T>).() -> Unit)? = null
): SelectionListView<T> = com.hendraanggrian.openpss.control.selectionListView(init).add()