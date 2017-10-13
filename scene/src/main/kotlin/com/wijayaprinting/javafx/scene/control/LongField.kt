package com.wijayaprinting.javafx.scene.control

import com.wijayaprinting.javafx.scene.utils.digitsOnly
import com.wijayaprinting.javafx.scene.utils.isDigits
import javafx.beans.property.SimpleLongProperty
import javafx.scene.control.TextField
import kotfx.stringConverterOf

open class LongField : TextField() {

    val valueProperty = SimpleLongProperty()

    init {
        textProperty().bindBidirectional(valueProperty, stringConverterOf<Number> { if (!isDigits) 0 else it.toLong() })
        digitsOnly()
    }

    var value: Long
        get() = valueProperty.get()
        set(value) = valueProperty.set(value)
}