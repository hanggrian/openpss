package com.wijayaprinting.javafx.scene.control

import com.wijayaprinting.javafx.scene.utils.digitsOnly
import com.wijayaprinting.javafx.scene.utils.isDigits
import javafx.beans.property.SimpleLongProperty
import javafx.scene.control.TextField
import kotfx.bindings.longBindingOf

/**
 * @author Hendra Anggrian (hendraanggrian@gmail.com)
 */
class LongField : TextField() {

    val valueProperty: SimpleLongProperty = SimpleLongProperty().apply {
        bind(longBindingOf(textProperty()) {
            if (isDigits) text.toLong()
            else 0
        })
    }
    val value: Long get() = valueProperty.value

    init {
        digitsOnly()
    }
}