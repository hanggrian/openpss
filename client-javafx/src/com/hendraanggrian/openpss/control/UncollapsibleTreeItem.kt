package com.hendraanggrian.openpss.control

import javafx.scene.control.TreeItem
import ktfx.coroutines.listener

class UncollapsibleTreeItem<T>(value: T) : TreeItem<T>(value) {

    init {
        isExpanded = true
        expandedProperty().listener { _, _, expanded -> if (!expanded) isExpanded = true }
    }
}
