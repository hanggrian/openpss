@file:Suppress("NOTHING_TO_INLINE", "UNUSED")

package com.hendraanggrian.openpss.scene.control

import javafx.beans.property.BooleanProperty
import javafx.beans.property.SimpleBooleanProperty
import javafx.scene.Node
import javafx.scene.control.TextField
import ktfx.beans.binding.booleanBindingOf
import ktfx.coroutines.listener
import ktfx.layouts.LayoutDsl
import ktfx.layouts.LayoutManager
import org.apache.commons.validator.routines.InetAddressValidator.getInstance

/** Field that display IP address. */
open class HostField : TextField() {

    val validProperty: BooleanProperty = SimpleBooleanProperty()

    init {
        validProperty.bind(booleanBindingOf(textProperty()) { getInstance().isValidInet4Address(text) })
        focusedProperty().listener { _, _, value -> if (value && text.isNotEmpty()) selectAll() }
    }

    val isValid: Boolean get() = validProperty.get()
}

inline fun hostField(
    noinline init: ((@LayoutDsl HostField).() -> Unit)? = null
): HostField = HostField().apply { init?.invoke(this) }

inline fun LayoutManager<Node>.hostField(
    noinline init: ((@LayoutDsl HostField).() -> Unit)? = null
): HostField = com.hendraanggrian.openpss.scene.control.hostField(init).add()