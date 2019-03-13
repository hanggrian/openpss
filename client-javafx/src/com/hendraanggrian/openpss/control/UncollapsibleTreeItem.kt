package com.hendraanggrian.openpss.control

import javafx.scene.control.TreeItem
import ktfx.coroutines.listener
import ktfx.layouts.LayoutMarker

class UncollapsibleTreeItem<T>(value: T) : TreeItem<T>(value) {

    init {
        isExpanded = true
        expandedProperty().listener { _, _, expanded -> if (!expanded) isExpanded = true }
    }
}

fun <T> uncollapsibleTreeItem(
    value: T,
    init: ((@LayoutMarker UncollapsibleTreeItem<T>).() -> Unit)? = null
): UncollapsibleTreeItem<T> = UncollapsibleTreeItem(value).also { init?.invoke(it) }