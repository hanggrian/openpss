package com.wijayaprinting.javafx.scene.control

import com.wijayaprinting.javafx.scene.utils.digitsOnly
import com.wijayaprinting.javafx.scene.utils.isDigits
import javafx.beans.property.SimpleIntegerProperty
import javafx.scene.control.TextField
import kotfx.bindings.intBindingOf

/**
 * @author Hendra Anggrian (hendraanggrian@gmail.com)
 */
open class IntField : TextField() {

    val valueProperty = SimpleIntegerProperty().apply {
        bind(intBindingOf(textProperty()) {
            if (isDigits) text.toInt()
            else 0
        })
    }
    var value: Int
        get() = valueProperty.get()
        set(value) = valueProperty.set(value)

    init {
        digitsOnly()
    }
}