package com.wijayaprinting.scene.control

import javafx.scene.Node
import javafx.scene.control.ListCell

abstract class GraphicListCell<T> : ListCell<T>() {

    abstract fun getGraphic(item: T): Node

    override fun updateItem(item: T, empty: Boolean) {
        super.updateItem(item, empty)
        text = null
        graphic = null
        if (item != null && !empty) graphic = getGraphic(item)
    }
}