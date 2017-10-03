package com.wijayaprinting.javafx.scene.control

import com.wijayaprinting.javafx.scene.utils.isDecimal
import javafx.beans.property.SimpleBooleanProperty
import javafx.beans.property.SimpleFloatProperty
import javafx.scene.control.TextField
import kotfx.bindings.booleanBindingOf
import kotfx.bindings.floatBindingOf

/**
 * @author Hendra Anggrian (hendraanggrian@gmail.com)
 */
class FloatField : TextField() {

    val valueProperty: SimpleFloatProperty = SimpleFloatProperty().apply {
        bind(floatBindingOf(textProperty()) {
            if (isDecimal) text.toFloat()
            else 0f
        })
    }
    val value: Float get() = valueProperty.value

    val validProperty: SimpleBooleanProperty = SimpleBooleanProperty().apply {
        bind(booleanBindingOf(textProperty()) { isDecimal })
    }
    val isValid: Boolean = validProperty.value
}