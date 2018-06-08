@file:Suppress("NOTHING_TO_INLINE")

package com.hendraanggrian.openpss.control

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
open class HostField : TextField() {

    private val validProperty = SimpleBooleanProperty()
    fun validProperty(): BooleanProperty = validProperty
    val isValid: Boolean by validProperty

    init {
        validProperty.bind(booleanBindingOf(textProperty()) {
            when (text) {
                "localhost" -> true
                else -> getInstance().isValidInet4Address(text)
            }
        })
        focusedProperty().listener { _, _, value -> if (value && text.isNotEmpty()) selectAll() }
    }
}

/** Creates a [HostField]. */
fun hostField(
    init: ((@LayoutDsl HostField).() -> Unit)? = null
): HostField = HostField().also {
    init?.invoke(it)
}

/** Creates a [HostField] and add it to this [LayoutManager]. */
inline fun LayoutManager<Node>.hostField(
    noinline init: ((@LayoutDsl HostField).() -> Unit)? = null
): HostField = com.hendraanggrian.openpss.control.hostField(init)()