package com.wijayaprinting.javafx.scene.control

import com.wijayaprinting.javafx.scene.utils.isDecimal
import javafx.beans.property.SimpleBooleanProperty
import javafx.beans.property.SimpleDoubleProperty
import javafx.scene.control.TextField
import kotfx.bindings.booleanBindingOf
import kotfx.bindings.doubleBindingOf

/**
 * @author Hendra Anggrian (hendraanggrian@gmail.com)
 */
open class DoubleField : TextField() {

    val valueProperty = SimpleDoubleProperty().apply {
        bind(doubleBindingOf(textProperty()) {
            if (isDecimal) text.toDouble()
            else 0.0
        })
    }
    var value: Double
        get() = valueProperty.get()
        set(value) = valueProperty.set(value)

    val validProperty = SimpleBooleanProperty().apply {
        bind(booleanBindingOf(textProperty()) { isDecimal })
    }
    val isValid: Boolean = validProperty.value
}