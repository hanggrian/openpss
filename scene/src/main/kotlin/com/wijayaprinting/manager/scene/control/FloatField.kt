@file:Suppress("NOTHING_TO_INLINE", "UNUSED")

package com.wijayaprinting.manager.scene.control

import com.wijayaprinting.manager.scene.utils.isDecimal
import javafx.beans.property.SimpleBooleanProperty
import javafx.beans.property.SimpleFloatProperty
import javafx.scene.control.TextField
import kotfx.bind
import kotfx.booleanBindingOf
import kotfx.controls.ItemManager
import kotfx.internal.KotfxDsl
import kotfx.layouts.ChildManager
import kotfx.stringConverter

open class FloatField : TextField() {

    val valueProperty = SimpleFloatProperty()
    val validProperty = SimpleBooleanProperty()

    init {
        textProperty().bindBidirectional(valueProperty, stringConverter<Number>({ if (!isDecimal) 0f else it.toFloat() }))
        validProperty bind booleanBindingOf(textProperty()) { isDecimal }
    }

    var value: Float
        get() = valueProperty.get()
        set(value) = valueProperty.set(value)

    val isValid: Boolean = validProperty.value
}

@JvmOverloads
inline fun floatField(
        noinline init: ((@KotfxDsl FloatField).() -> Unit)? = null
): FloatField = FloatField().apply { init?.invoke(this) }

@JvmOverloads
inline fun ChildManager.floatField(
        noinline init: ((@KotfxDsl FloatField).() -> Unit)? = null
): FloatField = FloatField().apply { init?.invoke(this) }.add()

@JvmOverloads
inline fun ItemManager.floatField(
        noinline init: ((@KotfxDsl FloatField).() -> Unit)? = null
): FloatField = FloatField().apply { init?.invoke(this) }.add()