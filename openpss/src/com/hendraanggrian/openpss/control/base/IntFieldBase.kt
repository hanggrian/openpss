package com.hendraanggrian.openpss.control.base

import javafx.beans.property.IntegerProperty
import javafx.scene.control.TextField
import ktfx.coroutines.listener
import ktfx.listeners.bindBidirectional

interface IntFieldBase : BaseControl {

    val actual: TextField

    fun valueProperty(): IntegerProperty

    override fun initialize() {
        actual.textProperty().bindBidirectional(valueProperty()) {
            fromString { it.toIntOrNull() ?: 0 }
        }
        actual.textProperty().addListener { _, oldValue, value ->
            actual.text = if (value.isEmpty()) "0" else value.toIntOrNull()?.toString() ?: oldValue
            actual.end()
        }
        actual.focusedProperty().listener { _, _, focused ->
            if (focused && actual.text.isNotEmpty()) actual.selectAll()
        }
    }
}