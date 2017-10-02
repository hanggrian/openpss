package com.wijayaprinting.javafx.control.field

import com.wijayaprinting.javafx.utils.isDecimal
import javafx.beans.property.SimpleBooleanProperty
import javafx.beans.property.SimpleDoubleProperty
import kotfx.bindings.booleanBindingOf
import kotfx.bindings.doubleBindingOf

/**
 * @author Hendra Anggrian (hendraanggrian@gmail.com)
 */
class DoubleField : TextField {

    constructor() : super()
    constructor(promptText: String) : super(promptText)
    constructor(promptText: String, text: String) : super(promptText, text)

    val valueProperty: SimpleDoubleProperty = SimpleDoubleProperty().apply {
        bind(doubleBindingOf(textProperty()) {
            if (isDecimal) text.toDouble()
            else 0.0
        })
    }
    val value: Double get() = valueProperty.value

    val validProperty: SimpleBooleanProperty = SimpleBooleanProperty().apply {
        bind(booleanBindingOf(textProperty()) { isDecimal })
    }
    val isValid: Boolean = validProperty.value
}