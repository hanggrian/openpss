package com.hendraanggrian.openpss.control

import javafx.scene.control.TableView
import ktfx.coroutines.listener

class UnselectableTableView<S> : TableView<S>() {

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