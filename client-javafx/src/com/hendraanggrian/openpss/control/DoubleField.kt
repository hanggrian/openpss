@file:Suppress("NOTHING_TO_INLINE")

package com.hendraanggrian.openpss.control

import com.jfoenix.controls.JFXTextField
import javafx.beans.property.BooleanProperty
import javafx.beans.property.DoubleProperty
import javafx.beans.property.SimpleBooleanProperty
import javafx.beans.property.SimpleDoubleProperty
import ktfx.bindings.buildBooleanBinding
import ktfx.coroutines.listener
import ktfx.getValue
import ktfx.listeners.bindBidirectional
import ktfx.setValue

class DoubleField : JFXTextField() {

    private val valueProperty = SimpleDoubleProperty()
    fun valueProperty(): DoubleProperty = valueProperty
    var value: Double by valueProperty

    private val validProperty = SimpleBooleanProperty()
    fun validProperty(): BooleanProperty = validProperty
    val isValid: Boolean by validProperty

    init {
        textProperty().bindBidirectional(valueProperty()) {
            fromString { it.toDoubleOrNull() ?: 0.0 }
        }
        validProperty().bind(buildBooleanBinding(textProperty()) {
            try {
                java.lang.Double.parseDouble(text)
                true
            } catch (e: NumberFormatException) {
                false
            }
        })
        focusedProperty().listener { _, _, focused ->
            if (focused && text.isNotEmpty()) {
                selectAll()
            }
        }
    }
}