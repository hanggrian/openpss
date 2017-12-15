package com.wijayaprinting.manager.scene.control

import com.wijayaprinting.manager.scene.utils.digitsOnly
import com.wijayaprinting.manager.scene.utils.isDigits
import javafx.beans.property.SimpleIntegerProperty
import javafx.scene.control.TextField
import kotfx.stringConverterOf

open class IntField : TextField() {

    val valueProperty = SimpleIntegerProperty()

    init {
        textProperty().bindBidirectional(valueProperty, stringConverterOf<Number>({ if (!isDigits) 0 else it.toInt() }))
        digitsOnly()
    }

    var value: Int
        get() = valueProperty.get()
        set(value) = valueProperty.set(value)
}