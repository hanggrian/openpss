package com.hanggrian.openpss.control

import com.jfoenix.controls.JFXTextField
import javafx.beans.property.IntegerProperty
import javafx.beans.property.SimpleIntegerProperty
import ktfx.coroutines.listener
import ktfx.getValue
import ktfx.setValue
import ktfx.text.buildStringConverter

class IntField : JFXTextField() {
    val valueProperty: IntegerProperty = SimpleIntegerProperty()
    var value: Int by valueProperty

    init {
        textProperty().bindBidirectional(
            valueProperty,
            buildStringConverter {
                fromString { it.toIntOrNull() ?: 0 }
            },
        )
        textProperty().addListener { _, oldValue, value ->
            text =
                when {
                    value.isEmpty() -> "0"
                    else -> value.toIntOrNull()?.toString() ?: oldValue
                }
            end()
        }
        focusedProperty().listener { _, _, focused ->
            if (focused && text.isNotEmpty()) {
                selectAll()
            }
        }
    }
}
