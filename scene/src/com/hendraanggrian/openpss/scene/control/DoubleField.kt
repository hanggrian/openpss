@file:Suppress("NOTHING_TO_INLINE", "UNUSED")

package com.hendraanggrian.openpss.scene.control

import javafx.beans.property.BooleanProperty
import javafx.beans.property.DoubleProperty
import javafx.beans.property.SimpleBooleanProperty
import javafx.beans.property.SimpleDoubleProperty
import javafx.scene.control.TextField
import kotfx.annotations.LayoutDsl
import kotfx.beans.binding.booleanBindingOf
import kotfx.coroutines.listener
import kotfx.layout.ChildManager
import kotfx.layout.ItemManager
import kotfx.listeners.bindBidirectional

open class DoubleField : TextField() {

    val valueProperty: DoubleProperty = SimpleDoubleProperty()
    val validProperty: BooleanProperty = SimpleBooleanProperty()

    init {
        textProperty().bindBidirectional(valueProperty) {
            fromString { it.toDoubleOrNull() ?: 0.0 }
        }
        validProperty.bind(booleanBindingOf(textProperty()) {
            try {
                java.lang.Double.parseDouble(text)
                true
            } catch (e: NumberFormatException) {
                false
            }
        })
        focusedProperty().listener { _, _, focused -> if (focused && text.isNotEmpty()) selectAll() }
    }

    var value: Double
        get() = valueProperty.get()
        set(value) = valueProperty.set(value)

    val isValid: Boolean get() = validProperty.get()
}

inline fun doubleField(noinline init: ((@LayoutDsl DoubleField).() -> Unit)? = null): DoubleField = DoubleField().apply { init?.invoke(this) }
inline fun ChildManager.doubleField(noinline init: ((@LayoutDsl DoubleField).() -> Unit)? = null): DoubleField = DoubleField().apply { init?.invoke(this) }.add()
inline fun ItemManager.doubleField(noinline init: ((@LayoutDsl DoubleField).() -> Unit)? = null): DoubleField = DoubleField().apply { init?.invoke(this) }.add()