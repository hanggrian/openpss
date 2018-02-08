@file:Suppress("NOTHING_TO_INLINE", "UNUSED")

package com.wijayaprinting.ui.scene.control

import javafx.beans.property.SimpleLongProperty
import javafx.scene.control.TextField
import kotfx.annotations.SceneDsl
import kotfx.properties.MutableLongProperty
import kotfx.scene.ChildRoot
import kotfx.scene.ItemRoot
import kotfx.stringConverterOf

open class LongField : TextField() {

    val valueProperty: MutableLongProperty = SimpleLongProperty()

    init {
        textProperty().bindBidirectional(valueProperty, stringConverterOf<Number> { it.toLongOrNull() ?: 0 })
        textProperty().addListener { _, oldValue, newValue ->
            text = if (newValue.isEmpty()) "0" else newValue.toLongOrNull()?.toString() ?: oldValue
        }
    }

    var value: Long
        get() = valueProperty.get()
        set(value) = valueProperty.set(value)
}

@JvmOverloads inline fun longField(noinline init: ((@SceneDsl LongField).() -> Unit)? = null): LongField = LongField().apply { init?.invoke(this) }
@JvmOverloads inline fun ChildRoot.longField(noinline init: ((@SceneDsl LongField).() -> Unit)? = null): LongField = LongField().apply { init?.invoke(this) }.add()
@JvmOverloads inline fun ItemRoot.longField(noinline init: ((@SceneDsl LongField).() -> Unit)? = null): LongField = LongField().apply { init?.invoke(this) }.add()