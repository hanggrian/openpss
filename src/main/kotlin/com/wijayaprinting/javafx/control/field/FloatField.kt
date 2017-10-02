package com.wijayaprinting.javafx.control.field

import com.wijayaprinting.javafx.utils.isDecimal
import javafx.beans.property.SimpleBooleanProperty
import javafx.beans.property.SimpleFloatProperty
import kotfx.bindings.booleanBindingOf
import kotfx.bindings.floatBindingOf

/**
 * @author Hendra Anggrian (hendraanggrian@gmail.com)
 */
class FloatField : TextField {

    constructor() : super()
    constructor(promptText: String) : super(promptText)
    constructor(promptText: String, text: String) : super(promptText, text)

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