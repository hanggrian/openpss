package com.wijayaprinting.javafx.scene.control

import com.wijayaprinting.javafx.scene.utils.digitsOnly
import com.wijayaprinting.javafx.scene.utils.isDigits
import javafx.beans.property.SimpleIntegerProperty
import javafx.scene.control.TextField
import kotfx.stringConverterOf

open class IntField : TextField() {

    val valueProperty = SimpleIntegerProperty()

    init {
        textProperty().bindBidirectional(valueProperty, stringConverterOf<Number> { if (!isDigits) 0 else it.toInt() })
        digitsOnly()
    }

    var value: Int
        get() = valueProperty.get()
        set(value) = valueProperty.set(value)
}