@file:Suppress("NOTHING_TO_INLINE", "UNUSED")

package com.wijayaprinting.scene.control

import javafx.beans.property.LongProperty
import javafx.beans.property.SimpleLongProperty
import javafx.scene.control.TextField
import kotfx.annotations.SceneDsl
import kotfx.scene.ChildManager
import kotfx.scene.ItemManager
import kotfx.stringConverterOf

open class LongField : TextField() {

    val valueProperty: LongProperty = SimpleLongProperty()

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

inline fun longField(noinline init: ((@SceneDsl LongField).() -> Unit)? = null): LongField = LongField().apply { init?.invoke(this) }
inline fun ChildManager.longField(noinline init: ((@SceneDsl LongField).() -> Unit)? = null): LongField = LongField().apply { init?.invoke(this) }.add()
inline fun ItemManager.longField(noinline init: ((@SceneDsl LongField).() -> Unit)? = null): LongField = LongField().apply { init?.invoke(this) }.add()