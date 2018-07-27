package com.hendraanggrian.openpss.control

import com.hendraanggrian.openpss.scene.R
import javafx.beans.property.DoubleProperty
import javafx.beans.property.ObjectProperty
import javafx.beans.property.SimpleDoubleProperty
import javafx.beans.property.SimpleObjectProperty
import javafx.scene.Node
import javafx.scene.control.Pagination
import javafx.util.Callback
import javafxx.beans.binding.bindingOf
import javafxx.beans.value.getValue
import javafxx.beans.value.setValue

class PaginatedPane : Pagination() {

    private val countProperty = SimpleDoubleProperty()
    fun countProperty(): DoubleProperty = countProperty
    var count: Double by countProperty

    private val contentFactoryProperty = SimpleObjectProperty<Callback<Pair<Int, Int>, Node>>()
    fun contentFactoryProperty(): ObjectProperty<Callback<Pair<Int, Int>, Node>> = contentFactoryProperty
    var contentFactory: Callback<Pair<Int, Int>, Node>? by contentFactoryProperty

    init {
        stylesheets += javaClass.getResource(R.style.paginatedpane).toExternalForm()
        pageFactoryProperty().bind(bindingOf(countProperty, contentFactoryProperty) {
            Callback<Int, Node> { page ->
                contentFactory?.call(page to (height / count).toInt())
            }
        })
    }
}