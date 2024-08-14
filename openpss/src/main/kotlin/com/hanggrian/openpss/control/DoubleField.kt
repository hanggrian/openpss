package com.hanggrian.openpss.control

import com.jfoenix.controls.JFXTextField
import javafx.beans.property.BooleanProperty
import javafx.beans.property.DoubleProperty
import javafx.beans.property.SimpleBooleanProperty
import javafx.beans.property.SimpleDoubleProperty
import ktfx.bindings.booleanBindingOf
import ktfx.coroutines.listener
import ktfx.getValue
import ktfx.setValue
import ktfx.text.buildStringConverter

class DoubleField : JFXTextField() {
    val valueProperty: DoubleProperty = SimpleDoubleProperty()
    var value: Double by valueProperty

    val validProperty: BooleanProperty = SimpleBooleanProperty()
    val isValid: Boolean by validProperty

    init {
        textProperty().bindBidirectional(
            valueProperty,
            buildStringConverter {
                fromString { it.toDoubleOrNull() ?: 0.0 }
            },
        )
        validProperty.bind(
            booleanBindingOf(textProperty()) {
                try {
                    java.lang.Double.parseDouble(text)
                    true
                } catch (e: NumberFormatException) {
                    false
                }
            },
        )
        focusedProperty().listener { _, _, focused ->
            if (focused && text.isNotEmpty()) {
                selectAll()
            }
        }
    }
}
