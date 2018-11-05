package com.hendraanggrian.openpss.control.base

import javafx.beans.property.BooleanProperty
import javafx.beans.property.DoubleProperty
import javafx.scene.control.TextField
import ktfx.beans.binding.buildBooleanBinding
import ktfx.coroutines.listener
import ktfx.listeners.bindBidirectional

interface DoubleFieldBase : BaseControl {

    val actual: TextField

    fun valueProperty(): DoubleProperty

    fun validProperty(): BooleanProperty

    override fun initialize() {
        actual.textProperty().bindBidirectional(valueProperty()) {
            fromString { it.toDoubleOrNull() ?: 0.0 }
        }
        validProperty().bind(buildBooleanBinding(actual.textProperty()) {
            try {
                java.lang.Double.parseDouble(actual.text)
                true
            } catch (e: NumberFormatException) {
                false
            }
        })
        actual.focusedProperty().listener { _, _, focused ->
            if (focused && actual.text.isNotEmpty()) actual.selectAll()
        }
    }
}