package com.wijayaprinting.manager.scene.control

import com.wijayaprinting.manager.scene.utils.isDecimal
import javafx.beans.property.SimpleBooleanProperty
import javafx.beans.property.SimpleFloatProperty
import javafx.scene.control.TextField
import kotfx.bindings.booleanBindingOf
import kotfx.properties.bind
import kotfx.stringConverterOf

open class FloatField : TextField() {

    val valueProperty = SimpleFloatProperty()
    val validProperty = SimpleBooleanProperty()

    init {
        textProperty().bindBidirectional(valueProperty, stringConverterOf<Number>({ if (!isDecimal) 0f else it.toFloat() }))
        validProperty bind booleanBindingOf(textProperty()) { isDecimal }
    }

    var value: Float
        get() = valueProperty.get()
        set(value) = valueProperty.set(value)

    val isValid: Boolean = validProperty.value
}