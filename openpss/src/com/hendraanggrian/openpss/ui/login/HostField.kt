package com.hendraanggrian.openpss.ui.login

import com.jfoenix.controls.JFXTextField
import javafx.beans.property.BooleanProperty
import javafx.beans.property.SimpleBooleanProperty
import ktfx.beans.binding.buildBooleanBinding
import ktfx.coroutines.listener
import org.apache.commons.validator.routines.InetAddressValidator

/** Field that display IP address. */
class HostField : JFXTextField() {

    private val validProperty = SimpleBooleanProperty()
    fun validProperty(): BooleanProperty = validProperty

    init {
        validProperty.bind(
            buildBooleanBinding(textProperty()) {
                when (text) {
                    "localhost" -> true
                    else -> InetAddressValidator.getInstance().isValidInet4Address(text)
                }
            }
        )
        focusedProperty().listener { _, _, value -> if (value && text.isNotEmpty()) selectAll() }
    }
}
