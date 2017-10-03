package com.wijayaprinting.javafx.scene.control

import com.wijayaprinting.javafx.scene.utils.digitsOnly
import com.wijayaprinting.javafx.scene.utils.isDigits
import javafx.beans.property.SimpleIntegerProperty
import javafx.scene.control.TextField
import kotfx.bindings.intBindingOf

/**
 * @author Hendra Anggrian (hendraanggrian@gmail.com)
 */
class IntField : TextField() {

    val valueProperty: SimpleIntegerProperty = SimpleIntegerProperty().apply {
        bind(intBindingOf(textProperty()) {
            if (isDigits) text.toInt()
            else 0
        })
    }
    val value: Int get() = valueProperty.value

    init {
        digitsOnly()
    }
}