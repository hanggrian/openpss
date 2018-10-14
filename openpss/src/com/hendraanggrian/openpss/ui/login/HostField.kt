package com.hendraanggrian.openpss.ui.login

import javafx.beans.property.BooleanProperty
import javafx.beans.property.SimpleBooleanProperty
import javafx.scene.control.TextField
import ktfx.beans.binding.booleanBindingOf
import ktfx.coroutines.listener
import org.apache.commons.validator.routines.InetAddressValidator

/** Field that display IP address. */
class HostField : TextField() {

    private val validProperty = SimpleBooleanProperty()
    fun validProperty(): BooleanProperty = validProperty

    init {
        validProperty.bind(booleanBindingOf(textProperty()) {
            when (text) {
                "localhost" -> true
                else -> InetAddressValidator.getInstance().isValidInet4Address(text)
            }
        })
        focusedProperty().listener { _, _, value -> if (value && text.isNotEmpty()) selectAll() }
    }
}