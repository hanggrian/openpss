@file:Suppress("NOTHING_TO_INLINE", "UNUSED")

package com.wijayaprinting.manager.scene.control

import com.wijayaprinting.manager.scene.utils.isDecimal
import javafx.beans.property.SimpleBooleanProperty
import javafx.beans.property.SimpleFloatProperty
import javafx.scene.control.TextField
import kotfx.bind
import kotfx.booleanBindingOf
import kotfx.internal.ChildManager
import kotfx.internal.ControlDsl
import kotfx.internal.ItemManager
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
inline fun floatFieldOf(
        noinline init: ((@ControlDsl FloatField).() -> Unit)? = null
): FloatField = FloatField().apply { init?.invoke(this) }

@JvmOverloads
inline fun ChildManager.floatField(
        noinline init: ((@ControlDsl FloatField).() -> Unit)? = null
): FloatField = FloatField().apply { init?.invoke(this) }.add()

@JvmOverloads
inline fun ItemManager.floatField(
        noinline init: ((@ControlDsl FloatField).() -> Unit)? = null
): FloatField = FloatField().apply { init?.invoke(this) }.add()