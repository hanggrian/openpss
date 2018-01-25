@file:Suppress("NOTHING_TO_INLINE", "UNUSED")

package com.wijayaprinting.ui.scene.control

import javafx.beans.property.LongProperty
import javafx.beans.property.SimpleLongProperty
import javafx.scene.control.TextField
import kotfx.ChildRoot
import kotfx.ItemRoot
import kotfx.LayoutDsl
import kotfx.stringConverterOf

open class LongField : TextField() {

    val valueProperty: LongProperty = SimpleLongProperty()

    init {
        textProperty().bindBidirectional(valueProperty, stringConverterOf<Number>({ it.toLongOrNull() ?: 0 }))
        textProperty().addListener { _, oldValue, newValue ->
            text = if (newValue.isEmpty()) "0" else newValue.toLongOrNull()?.toString() ?: oldValue
        }
    }

    var value: Long
        get() = valueProperty.get()
        set(value) = valueProperty.set(value)
}

@JvmOverloads inline fun longField(noinline init: ((@LayoutDsl LongField).() -> Unit)? = null): LongField = LongField().apply { init?.invoke(this) }
@JvmOverloads inline fun ChildRoot.longField(noinline init: ((@LayoutDsl LongField).() -> Unit)? = null): LongField = LongField().apply { init?.invoke(this) }.add()
@JvmOverloads inline fun ItemRoot.longField(noinline init: ((@LayoutDsl LongField).() -> Unit)? = null): LongField = LongField().apply { init?.invoke(this) }.add()