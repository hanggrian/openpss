@file:Suppress("NOTHING_TO_INLINE", "UNUSED")

package com.hendraanggrian.openpss.scene.control

import javafx.beans.property.BooleanProperty
import javafx.beans.property.SimpleBooleanProperty
import javafx.scene.Node
import javafx.scene.control.TextField
import ktfx.beans.binding.booleanBindingOf
import ktfx.beans.value.getValue
import ktfx.coroutines.listener
import ktfx.layouts.LayoutDsl
import ktfx.layouts.LayoutManager
import org.apache.commons.validator.routines.InetAddressValidator.getInstance

/** Field that display IP address. */
open class HostField() : TextField() {

    val validProperty: BooleanProperty = SimpleBooleanProperty()
    val isValid: Boolean by validProperty

    init {
        validProperty.bind(booleanBindingOf(textProperty()) { getInstance().isValidInet4Address(text) })
        focusedProperty().listener { _, _, value -> if (value && text.isNotEmpty()) selectAll() }
    }
}

inline fun hostField(): HostField = hostField { }

inline fun hostField(
    init: (@LayoutDsl HostField).() -> Unit
): HostField = HostField().apply(init)

inline fun LayoutManager<Node>.hostField(): HostField = hostField { }

inline fun LayoutManager<Node>.hostField(
    init: (@LayoutDsl HostField).() -> Unit
): HostField = com.hendraanggrian.openpss.scene.control.hostField(init).add()