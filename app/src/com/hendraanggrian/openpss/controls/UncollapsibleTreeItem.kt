package com.hendraanggrian.openpss.controls

import javafx.scene.control.TreeItem

class UncollapsibleTreeItem<T>(value: T) : TreeItem<T>(value) {

    init {
        isExpanded = true
        expandedProperty().addListener { _, _, expanded -> if (!expanded) isExpanded = true }
    }
}