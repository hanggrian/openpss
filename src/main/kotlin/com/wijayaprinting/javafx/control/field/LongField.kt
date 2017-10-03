package com.wijayaprinting.javafx.control.field

import com.wijayaprinting.javafx.utils.digitsOnly
import com.wijayaprinting.javafx.utils.isDigits
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