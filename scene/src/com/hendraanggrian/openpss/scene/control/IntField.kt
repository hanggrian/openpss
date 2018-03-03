@file:Suppress("NOTHING_TO_INLINE", "UNUSED")

package com.hendraanggrian.openpss.scene.control

import javafx.beans.property.IntegerProperty
import javafx.beans.property.SimpleIntegerProperty
import javafx.scene.control.TextField
import kotfx.coroutines.listener
import kotfx.layouts.ChildManager
import kotfx.layouts.ItemManager
import kotfx.layouts.LayoutDsl
import kotfx.listeners.bindBidirectional

open class IntField : TextField() {

    val valueProperty: IntegerProperty = SimpleIntegerProperty()

    init {
        textProperty().bindBidirectional(valueProperty) {
            fromString { it.toIntOrNull() ?: 0 }
        }
        textProperty().listener { _, oldValue, value ->
            text = if (value.isEmpty()) "0" else value.toIntOrNull()?.toString() ?: oldValue
            end()
        }
        focusedProperty().listener { _, _, focused -> if (focused && text.isNotEmpty()) selectAll() }
    }

    var value: Int
        get() = valueProperty.get()
        set(value) = valueProperty.set(value)
}

inline fun intField(noinline init: ((@LayoutDsl IntField).() -> Unit)? = null): IntField = IntField().apply { init?.invoke(this) }
inline fun ChildManager.intField(noinline init: ((@LayoutDsl IntField).() -> Unit)? = null): IntField = IntField().apply { init?.invoke(this) }.add()
inline fun ItemManager.intField(noinline init: ((@LayoutDsl IntField).() -> Unit)? = null): IntField = IntField().apply { init?.invoke(this) }.add()