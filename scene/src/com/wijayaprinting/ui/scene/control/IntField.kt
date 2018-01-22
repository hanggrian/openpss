@file:Suppress("NOTHING_TO_INLINE", "UNUSED")

package com.wijayaprinting.ui.scene.control

import javafx.beans.property.SimpleIntegerProperty
import javafx.scene.control.TextField
import kotfx.ChildRoot
import kotfx.ItemRoot
import kotfx.KotfxDsl
import kotfx.stringConverter

open class IntField : TextField() {

    val valueProperty = SimpleIntegerProperty()

    init {
        textProperty().bindBidirectional(valueProperty, stringConverter<Number>({ it.toIntOrNull() ?: 0 }))
        textProperty().addListener { _, oldValue, newValue -> text = if (newValue.isEmpty()) "0" else newValue.toIntOrNull()?.toString() ?: oldValue }
    }

    var value: Int
        get() = valueProperty.get()
        set(value) = valueProperty.set(value)
}

@JvmOverloads inline fun intField(noinline init: ((@KotfxDsl IntField).() -> Unit)? = null): IntField = IntField().apply { init?.invoke(this) }
@JvmOverloads inline fun ChildRoot.intField(noinline init: ((@KotfxDsl IntField).() -> Unit)? = null): IntField = IntField().apply { init?.invoke(this) }.add()
@JvmOverloads inline fun ItemRoot.intField(noinline init: ((@KotfxDsl IntField).() -> Unit)? = null): IntField = IntField().apply { init?.invoke(this) }.add()