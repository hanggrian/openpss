@file:Suppress("NOTHING_TO_INLINE", "UNUSED")

package com.wijayaprinting.manager.scene.control

import javafx.beans.property.SimpleBooleanProperty
import javafx.beans.property.SimpleFloatProperty
import javafx.scene.control.TextField
import kotfx.*

open class FloatField : TextField() {

    val valueProperty = SimpleFloatProperty()
    val validProperty = SimpleBooleanProperty()

    init {
        textProperty().bindBidirectional(valueProperty, stringConverter<Number>({ it.toFloatOrNull() ?: 0f }))
        validProperty bind booleanBindingOf(textProperty()) {
            try {
                java.lang.Float.parseFloat(text)
                true
            } catch (e: NumberFormatException) {
                false
            }
        }
    }

    var value: Float
        get() = valueProperty.get()
        set(value) = valueProperty.set(value)

    val isValid: Boolean = validProperty.value
}

@JvmOverloads inline fun floatField(noinline init: ((@KotfxDsl FloatField).() -> Unit)? = null): FloatField = FloatField().apply { init?.invoke(this) }
@JvmOverloads inline fun ChildRoot.floatField(noinline init: ((@KotfxDsl FloatField).() -> Unit)? = null): FloatField = FloatField().apply { init?.invoke(this) }.add()
@JvmOverloads inline fun ItemRoot.floatField(noinline init: ((@KotfxDsl FloatField).() -> Unit)? = null): FloatField = FloatField().apply { init?.invoke(this) }.add()