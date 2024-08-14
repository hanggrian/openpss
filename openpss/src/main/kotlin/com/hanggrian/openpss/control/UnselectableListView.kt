package com.hanggrian.openpss.control

import javafx.scene.control.ListView
import ktfx.coroutines.listener

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
