package com.hanggrian.openpss.control

import javafx.beans.property.DoubleProperty
import javafx.beans.property.ObjectProperty
import javafx.beans.property.SimpleDoubleProperty
import javafx.beans.property.SimpleObjectProperty
import javafx.scene.Node
import javafx.scene.control.Pagination
import javafx.util.Callback
import ktfx.bindings.bindingOf
import ktfx.getValue
import ktfx.setValue

class PaginatedPane : Pagination() {
    val magicProperty: DoubleProperty = SimpleDoubleProperty()
    var magic: Double by magicProperty

    val contentFactoryProperty: ObjectProperty<Callback<Pair<Int, Int>, Node>> =
        SimpleObjectProperty()
    var contentFactory: Callback<Pair<Int, Int>, Node>? by contentFactoryProperty

    init {
        pageFactoryProperty().bind(
            bindingOf(magicProperty, contentFactoryProperty) {
                Callback { page ->
                    contentFactory?.call(page to (height / magic).toInt())
                }
            },
        )
    }

    val lastPageIndex: Int get() = pageCount - 1

    fun selectLast() {
        if (currentPageIndex != lastPageIndex && pageCount != INDETERMINATE) {
            currentPageIndex = lastPageIndex
        }
    }
}
