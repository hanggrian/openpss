@file:Suppress("NOTHING_TO_INLINE", "UNUSED")

package com.wijayaprinting.ui.scene.control

import javafx.beans.property.SimpleBooleanProperty
import javafx.beans.property.SimpleFloatProperty
import javafx.scene.control.TextField
import kotfx.annotations.SceneDsl
import kotfx.bindings.booleanBindingOf
import kotfx.properties.MutableBooleanProperty
import kotfx.properties.MutableFloatProperty
import kotfx.scene.ChildRoot
import kotfx.scene.ItemRoot
import kotfx.stringConverterOf

open class FloatField : TextField() {

    val valueProperty: MutableFloatProperty = SimpleFloatProperty()
    val validProperty: MutableBooleanProperty = SimpleBooleanProperty()

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

@JvmOverloads inline fun floatField(noinline init: ((@SceneDsl FloatField).() -> Unit)? = null): FloatField = FloatField().apply { init?.invoke(this) }
@JvmOverloads inline fun ChildRoot.floatField(noinline init: ((@SceneDsl FloatField).() -> Unit)? = null): FloatField = FloatField().apply { init?.invoke(this) }.add()
@JvmOverloads inline fun ItemRoot.floatField(noinline init: ((@SceneDsl FloatField).() -> Unit)? = null): FloatField = FloatField().apply { init?.invoke(this) }.add()