@file:Suppress("NOTHING_TO_INLINE", "UNUSED")

package com.wijayaprinting.manager.scene.control

import com.wijayaprinting.manager.scene.utils.isDecimal
import javafx.beans.property.SimpleBooleanProperty
import javafx.beans.property.SimpleDoubleProperty
import javafx.scene.control.TextField
import kotfx.bindings.booleanBindingOf
import kotfx.internal.ChildManager
import kotfx.internal.ControlDsl
import kotfx.internal.ItemManager
import kotfx.properties.bind
import kotfx.stringConverter

open class DoubleField : TextField() {

    val valueProperty = SimpleDoubleProperty()
    val validProperty = SimpleBooleanProperty()

    init {
        textProperty().bindBidirectional(valueProperty, stringConverter<Number>({ if (!isDecimal) 0.0 else it.toDouble() }))
        validProperty bind booleanBindingOf(textProperty()) { isDecimal }
    }

    var value: Double
        get() = valueProperty.get()
        set(value) = valueProperty.set(value)

    val isValid: Boolean = validProperty.value
}

@JvmOverloads
inline fun doubleFieldOf(
        noinline init: ((@ControlDsl DoubleField).() -> Unit)? = null
): DoubleField = DoubleField().apply { init?.invoke(this) }

@JvmOverloads
inline fun ChildManager.doubleField(
        noinline init: ((@ControlDsl DoubleField).() -> Unit)? = null
): DoubleField = DoubleField().apply { init?.invoke(this) }.add()

@JvmOverloads
inline fun ItemManager.doubleField(
        noinline init: ((@ControlDsl DoubleField).() -> Unit)? = null
): DoubleField = DoubleField().apply { init?.invoke(this) }.add()