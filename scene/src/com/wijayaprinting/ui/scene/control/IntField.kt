@file:Suppress("NOTHING_TO_INLINE", "UNUSED")

package com.wijayaprinting.ui.scene.control

import javafx.scene.control.TextField
import kotfx.annotations.SceneDsl
import kotfx.properties.MutableIntProperty
import kotfx.properties.SimpleIntProperty
import kotfx.scene.ChildRoot
import kotfx.scene.ItemRoot
import kotfx.stringConverterOf

open class IntField : TextField() {

    val valueProperty: MutableIntProperty = SimpleIntProperty()

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

@JvmOverloads inline fun intField(noinline init: ((@SceneDsl IntField).() -> Unit)? = null): IntField = IntField().apply { init?.invoke(this) }
@JvmOverloads inline fun ChildRoot.intField(noinline init: ((@SceneDsl IntField).() -> Unit)? = null): IntField = IntField().apply { init?.invoke(this) }.add()
@JvmOverloads inline fun ItemRoot.intField(noinline init: ((@SceneDsl IntField).() -> Unit)? = null): IntField = IntField().apply { init?.invoke(this) }.add()