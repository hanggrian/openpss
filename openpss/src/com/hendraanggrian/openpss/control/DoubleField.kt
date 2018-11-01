@file:Suppress("NOTHING_TO_INLINE")

package com.hendraanggrian.openpss.control

import com.jfoenix.controls.JFXTextField
import javafx.beans.property.BooleanProperty
import javafx.beans.property.DoubleProperty
import javafx.beans.property.SimpleBooleanProperty
import javafx.beans.property.SimpleDoubleProperty
import ktfx.LayoutDsl
import ktfx.NodeManager
import ktfx.beans.binding.booleanBindingOf
import ktfx.beans.value.getValue
import ktfx.beans.value.setValue
import ktfx.coroutines.listener
import ktfx.listeners.bindBidirectional

open class DoubleField : JFXTextField() {

    private val valueProperty = SimpleDoubleProperty()
    fun valueProperty(): DoubleProperty = valueProperty
    var value: Double by valueProperty

    private val validProperty = SimpleBooleanProperty()
    fun validProperty(): BooleanProperty = validProperty
    val isValid: Boolean by validProperty

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
}

/** Creates a [DoubleField]. */
fun doubleField(
    init: ((@LayoutDsl DoubleField).() -> Unit)? = null
): DoubleField = DoubleField().also {
    init?.invoke(it)
}

/** Creates a [DoubleField] and add it to this manager. */
inline fun NodeManager.doubleField(
    noinline init: ((@LayoutDsl DoubleField).() -> Unit)? = null
): DoubleField = (com.hendraanggrian.openpss.control.doubleField(init))()