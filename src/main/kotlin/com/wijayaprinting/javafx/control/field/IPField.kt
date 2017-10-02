package com.wijayaprinting.javafx.control.field

import javafx.beans.property.SimpleBooleanProperty
import kotfx.bindings.booleanBindingOf
import org.apache.commons.validator.routines.InetAddressValidator

/**
 * @author Hendra Anggrian (hendraanggrian@gmail.com)
 */
class IPField : TextField {

    constructor() : super()
    constructor(promptText: String) : super(promptText)
    constructor(promptText: String, text: String) : super(promptText, text)

    val validProperty: SimpleBooleanProperty = SimpleBooleanProperty().apply { bind(booleanBindingOf(textProperty()) { InetAddressValidator.getInstance().isValidInet4Address(text) }) }
    val isValid: Boolean get() = validProperty.value
}