package com.wijayaprinting.manager.scene.control

import com.wijayaprinting.manager.scene.utils.isDecimal
import javafx.beans.property.SimpleBooleanProperty
import javafx.beans.property.SimpleDoubleProperty
import javafx.scene.control.TextField
import kotfx.bind
import kotfx.bindings.booleanBindingOf
import kotfx.stringConverterOf

open class DoubleField : TextField() {

    val valueProperty = SimpleDoubleProperty()
    val validProperty = SimpleBooleanProperty()

    init {
        textProperty().bindBidirectional(valueProperty, stringConverterOf<Number> { if (!isDecimal) 0.0 else it.toDouble() })
        validProperty bind booleanBindingOf(textProperty()) { isDecimal }
    }

    var value: Double
        get() = valueProperty.get()
        set(value) = valueProperty.set(value)

    val isValid: Boolean = validProperty.value
}