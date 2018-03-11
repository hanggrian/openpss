@file:Suppress("NOTHING_TO_INLINE", "UNUSED")

package com.hendraanggrian.openpss.scene.control

import javafx.beans.property.BooleanProperty
import javafx.beans.property.SimpleBooleanProperty
import javafx.scene.control.TextField
import kfx.beans.binding.booleanBindingOf
import kfx.coroutines.listener
import kfx.layouts.ChildManager
import kfx.layouts.ItemManager
import kfx.layouts.LayoutDsl
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

inline fun hostField(noinline init: ((@LayoutDsl HostField).() -> Unit)? = null): HostField = HostField().apply { init?.invoke(this) }
inline fun ChildManager.hostField(noinline init: ((@LayoutDsl HostField).() -> Unit)? = null): HostField = HostField().apply { init?.invoke(this) }.add()
inline fun ItemManager.hostField(noinline init: ((@LayoutDsl HostField).() -> Unit)? = null): HostField = HostField().apply { init?.invoke(this) }.add()
