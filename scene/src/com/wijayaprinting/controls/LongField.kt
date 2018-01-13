@file:Suppress("NOTHING_TO_INLINE", "UNUSED")

package com.wijayaprinting.controls

import javafx.beans.property.SimpleLongProperty
import javafx.scene.control.TextField
import kotfx.ChildRoot
import kotfx.ItemRoot
import kotfx.KotfxDsl
import kotfx.stringConverter

open class LongField : TextField() {

    val valueProperty = SimpleLongProperty()

    init {
        textProperty().bindBidirectional(valueProperty, stringConverter<Number>({ it.toLongOrNull() ?: 0 }))
        textProperty().addListener { _, oldValue, newValue -> text = if (newValue.isEmpty()) "0" else newValue.toLongOrNull()?.toString() ?: oldValue }
    }

    var value: Long
        get() = valueProperty.get()
        set(value) = valueProperty.set(value)
}

@JvmOverloads inline fun longField(noinline init: ((@KotfxDsl LongField).() -> Unit)? = null): LongField = LongField().apply { init?.invoke(this) }
@JvmOverloads inline fun ChildRoot.longField(noinline init: ((@KotfxDsl LongField).() -> Unit)? = null): LongField = LongField().apply { init?.invoke(this) }.add()
@JvmOverloads inline fun ItemRoot.longField(noinline init: ((@KotfxDsl LongField).() -> Unit)? = null): LongField = LongField().apply { init?.invoke(this) }.add()