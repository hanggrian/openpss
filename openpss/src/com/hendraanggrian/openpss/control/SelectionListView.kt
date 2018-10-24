package com.hendraanggrian.openpss.control

import com.jfoenix.controls.JFXListView
import ktfx.coroutines.listener
import ktfx.scene.control.isNotSelected

class SelectionListView<T> : JFXListView<T>() {

    init {
        selectionModel.run {
            items.listener { if (items.isNotEmpty()) selectFirst() }
            selectedItemProperty().listener { _, oldItem, _ -> if (isNotSelected()) select(oldItem) }
        }
    }
}