@file:Suppress("NOTHING_TO_INLINE", "UNUSED")

package com.hendraanggrian.openpss.scene.control

import javafx.beans.property.IntegerProperty
import javafx.beans.property.SimpleIntegerProperty
import javafx.scene.Node
import javafx.scene.control.TextField
import ktfx.coroutines.listener
import ktfx.layouts.LayoutDsl
import ktfx.layouts.LayoutManager
import ktfx.listeners.bindBidirectional

open class IntField : TextField() {

    val valueProperty: IntegerProperty = SimpleIntegerProperty()

    init {
        textProperty().bindBidirectional(valueProperty) {
            fromString { it.toIntOrNull() ?: 0 }
        }
        textProperty().addListener { _, oldValue, value ->
            text = if (value.isEmpty()) "0" else value.toIntOrNull()?.toString() ?: oldValue
            end()
        }
        focusedProperty().listener { _, _, focused -> if (focused && text.isNotEmpty()) selectAll() }
    }

    var value: Int
        get() = valueProperty.get()
        set(value) = valueProperty.set(value)
}

inline fun intField(
    noinline init: ((@LayoutDsl IntField).() -> Unit)? = null
): IntField = IntField().apply { init?.invoke(this) }

inline fun LayoutManager<Node>.intField(
    noinline init: ((@LayoutDsl IntField).() -> Unit)? = null
): IntField = com.hendraanggrian.openpss.scene.control.intField(init).add()