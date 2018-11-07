package com.hendraanggrian.openpss.control

import javafx.beans.property.DoubleProperty
import javafx.beans.property.ObjectProperty
import javafx.beans.property.SimpleDoubleProperty
import javafx.beans.property.SimpleObjectProperty
import javafx.scene.Node
import javafx.scene.control.Pagination
import javafx.util.Callback
import ktfx.beans.binding.buildBinding
import ktfx.beans.value.getValue
import ktfx.beans.value.setValue

class PaginatedPane : Pagination() {

    private val magicProperty = SimpleDoubleProperty()
    fun magicProperty(): DoubleProperty = magicProperty
    var magic: Double by magicProperty

    private val contentFactoryProperty = SimpleObjectProperty<Callback<Pair<Int, Int>, Node>>()
    fun contentFactoryProperty(): ObjectProperty<Callback<Pair<Int, Int>, Node>> = contentFactoryProperty
    var contentFactory: Callback<Pair<Int, Int>, Node>? by contentFactoryProperty

    init {
        pageFactoryProperty().bind(buildBinding(magicProperty, contentFactoryProperty) {
            Callback<Int, Node> { page ->
                contentFactory?.call(page to (height / magic).toInt())
            }
        })
    }

    fun selectLast() {
        if (currentPageIndex != pageCount - 1) {
            currentPageIndex = pageCount - 1
        }
    }
}