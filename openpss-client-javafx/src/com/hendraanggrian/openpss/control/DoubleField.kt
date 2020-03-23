package com.hendraanggrian.openpss.control

import com.jfoenix.controls.JFXTextField
import javafx.beans.property.BooleanProperty
import javafx.beans.property.DoubleProperty
import javafx.beans.property.SimpleBooleanProperty
import javafx.beans.property.SimpleDoubleProperty
import ktfx.coroutines.listener
import ktfx.getValue
import ktfx.setValue
import ktfx.text.buildStringConverter
import ktfx.toBooleanBinding

class DoubleField : JFXTextField() {

    private val valueProperty = SimpleDoubleProperty()
    fun valueProperty(): DoubleProperty = valueProperty
    var value: Double by valueProperty

    private val validProperty = SimpleBooleanProperty()
    fun validProperty(): BooleanProperty = validProperty
    val isValid: Boolean by validProperty

    init {
        textProperty().bindBidirectional(valueProperty(), buildStringConverter {
            fromString { it.toDoubleOrNull() ?: 0.0 }
        })
        validProperty().bind(textProperty().toBooleanBinding {
            runCatching {
                java.lang.Double.parseDouble(it)
                true
            }.getOrDefault(false)
        })
        focusedProperty().listener { _, _, focused ->
            if (focused && text.isNotEmpty()) {
                selectAll()
            }
        }
    }
}
