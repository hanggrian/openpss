package com.hanggrian.openpss.control

import com.jfoenix.controls.JFXListView
import ktfx.controls.isNotSelected
import ktfx.coroutines.listener

class SelectionListView<T> : JFXListView<T>() {
    init {
        selectionModel.run {
            items.listener {
                minHeight = items.size * ROW_HEIGHT + 2.0
                if (items.isNotEmpty()) {
                    selectFirst()
                }
            }
            selectedItemProperty().listener { _, oldItem, _ ->
                if (isNotSelected()) {
                    select(oldItem)
                }
            }
        }
    }

    companion object {
        const val ROW_HEIGHT = 40
    }
}
