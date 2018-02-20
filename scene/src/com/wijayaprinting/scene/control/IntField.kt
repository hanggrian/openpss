@file:Suppress("NOTHING_TO_INLINE", "UNUSED")

package com.wijayaprinting.scene.control

import javafx.beans.property.IntegerProperty
import javafx.beans.property.SimpleIntegerProperty
import javafx.scene.control.TextField
import kotfx.annotations.LayoutDsl
import kotfx.layout.ChildManager
import kotfx.layout.ItemManager
import kotfx.stringConverterOf

open class IntField : TextField() {

    val valueProperty: IntegerProperty = SimpleIntegerProperty()

    init {
        textProperty().bindBidirectional(valueProperty, stringConverterOf<Number> { it.toIntOrNull() ?: 0 })
        textProperty().addListener { _, oldValue, newValue ->
            text = if (newValue.isEmpty()) "0" else newValue.toIntOrNull()?.toString() ?: oldValue
        }
    }

    var value: Int
        get() = valueProperty.get()
        set(value) = valueProperty.set(value)
}

inline fun intField(noinline init: ((@LayoutDsl IntField).() -> Unit)? = null): IntField = IntField().apply { init?.invoke(this) }
inline fun ChildManager.intField(noinline init: ((@LayoutDsl IntField).() -> Unit)? = null): IntField = IntField().apply { init?.invoke(this) }.add()
inline fun ItemManager.intField(noinline init: ((@LayoutDsl IntField).() -> Unit)? = null): IntField = IntField().apply { init?.invoke(this) }.add()