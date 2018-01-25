package com.wijayaprinting.ui.scene.control

import javafx.scene.Node
import javafx.scene.control.ListCell

abstract class GraphicListCell<T> : ListCell<T>() {

    abstract operator fun get(item: T): Node

    override fun updateItem(item: T, empty: Boolean) {
        super.updateItem(item, empty)
        text = null
        graphic = null
        if (item != null && !empty) graphic = this[item]
    }
}