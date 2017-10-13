package com.wijayaprinting.javafx.scene.control

import javafx.beans.property.SimpleBooleanProperty
import javafx.scene.control.TextField
import kotfx.bindings.booleanBindingOf
import org.apache.commons.validator.routines.InetAddressValidator

open class IPField : TextField() {

    val validProperty = SimpleBooleanProperty()

    init {
        validProperty.bind(booleanBindingOf(textProperty()) { InetAddressValidator.getInstance().isValidInet4Address(text) })
    }

    val isValid: Boolean get() = validProperty.value
}