@file:Suppress("NOTHING_TO_INLINE", "UNUSED")

package com.wijayaprinting.scene.control

import javafx.beans.property.BooleanProperty
import javafx.beans.property.FloatProperty
import javafx.beans.property.SimpleBooleanProperty
import javafx.beans.property.SimpleFloatProperty
import javafx.scene.control.TextField
import kotfx.annotations.LayoutDsl
import kotfx.bindings.booleanBindingOf
import kotfx.layout.ChildManager
import kotfx.layout.ItemManager
import kotfx.stringConverterOf

open class FloatField : TextField() {

    val valueProperty: FloatProperty = SimpleFloatProperty()
    val validProperty: BooleanProperty = SimpleBooleanProperty()

    init {
        textProperty().bindBidirectional(valueProperty, stringConverterOf<Number> { it.toFloatOrNull() ?: 0f })
        validProperty.bind(booleanBindingOf(textProperty()) {
            try {
                java.lang.Float.parseFloat(text)
                true
            } catch (e: NumberFormatException) {
                false
            }
        })
    }

    var value: Float
        get() = valueProperty.get()
        set(value) = valueProperty.set(value)

    val isValid: Boolean get() = validProperty.get()
}

inline fun floatField(noinline init: ((@LayoutDsl FloatField).() -> Unit)? = null): FloatField = FloatField().apply { init?.invoke(this) }
inline fun ChildManager.floatField(noinline init: ((@LayoutDsl FloatField).() -> Unit)? = null): FloatField = FloatField().apply { init?.invoke(this) }.add()
inline fun ItemManager.floatField(noinline init: ((@LayoutDsl FloatField).() -> Unit)? = null): FloatField = FloatField().apply { init?.invoke(this) }.add()