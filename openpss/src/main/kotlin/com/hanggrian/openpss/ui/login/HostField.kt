package com.hanggrian.openpss.ui.login

import com.jfoenix.controls.JFXTextField
import javafx.beans.property.BooleanProperty
import javafx.beans.property.SimpleBooleanProperty
import ktfx.bindings.booleanBindingBy
import ktfx.coroutines.listener
import org.apache.commons.validator.routines.InetAddressValidator

/** Field that display IP address. */
class HostField : JFXTextField() {
    val validProperty: BooleanProperty = SimpleBooleanProperty()

    init {
        validProperty.bind(
            textProperty().booleanBindingBy {
                when (it) {
                    "localhost" -> true
                    else -> InetAddressValidator.getInstance().isValidInet4Address(text)
                }
            },
        )
        focusedProperty().listener { _, _, value -> if (value && text.isNotEmpty()) selectAll() }
    }
}
