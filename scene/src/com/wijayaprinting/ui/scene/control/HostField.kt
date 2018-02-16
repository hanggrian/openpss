@file:Suppress("NOTHING_TO_INLINE", "UNUSED")

package com.wijayaprinting.ui.scene.control

import javafx.beans.property.BooleanProperty
import javafx.beans.property.SimpleBooleanProperty
import javafx.scene.control.TextField
import kotfx.annotations.SceneDsl
import kotfx.bindings.booleanBindingOf
import kotfx.scene.ChildManager
import kotfx.scene.ItemManager
import org.apache.commons.validator.routines.InetAddressValidator.getInstance

/** Field that display IP address. */
open class HostField : TextField() {

    val validProperty: BooleanProperty = SimpleBooleanProperty()

    init {
        validProperty.bind(booleanBindingOf(textProperty()) { getInstance().isValidInet4Address(text) })
    }

    val isValid: Boolean get() = validProperty.get()
}

@JvmOverloads inline fun hostField(noinline init: ((@SceneDsl HostField).() -> Unit)? = null): HostField = HostField().apply { init?.invoke(this) }
@JvmOverloads inline fun ChildManager.hostField(noinline init: ((@SceneDsl HostField).() -> Unit)? = null): HostField = HostField().apply { init?.invoke(this) }.add()
@JvmOverloads inline fun ItemManager.hostField(noinline init: ((@SceneDsl HostField).() -> Unit)? = null): HostField = HostField().apply { init?.invoke(this) }.add()
