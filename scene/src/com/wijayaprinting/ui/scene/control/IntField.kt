@file:Suppress("NOTHING_TO_INLINE", "UNUSED")

package com.wijayaprinting.ui.scene.control

import javafx.beans.property.IntegerProperty
import javafx.beans.property.SimpleIntegerProperty
import javafx.scene.control.TextField
import kotfx.annotations.SceneDsl
import kotfx.scene.ChildManager
import kotfx.scene.ItemManager
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

@JvmOverloads inline fun intField(noinline init: ((@SceneDsl IntField).() -> Unit)? = null): IntField = IntField().apply { init?.invoke(this) }
@JvmOverloads inline fun ChildManager.intField(noinline init: ((@SceneDsl IntField).() -> Unit)? = null): IntField = IntField().apply { init?.invoke(this) }.add()
@JvmOverloads inline fun ItemManager.intField(noinline init: ((@SceneDsl IntField).() -> Unit)? = null): IntField = IntField().apply { init?.invoke(this) }.add()