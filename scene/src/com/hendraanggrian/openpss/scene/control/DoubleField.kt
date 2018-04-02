@file:Suppress("NOTHING_TO_INLINE", "UNUSED")

package com.hendraanggrian.openpss.scene.control

import javafx.beans.property.BooleanProperty
import javafx.beans.property.DoubleProperty
import javafx.beans.property.SimpleBooleanProperty
import javafx.beans.property.SimpleDoubleProperty
import javafx.scene.Node
import javafx.scene.control.TextField
import ktfx.beans.binding.booleanBindingOf
import ktfx.beans.value.getValue
import ktfx.beans.value.setValue
import ktfx.coroutines.listener
import ktfx.layouts.LayoutDsl
import ktfx.layouts.LayoutManager
import ktfx.listeners.bindBidirectional

open class DoubleField : TextField() {

    val valueProperty: DoubleProperty = SimpleDoubleProperty()
    var value: Double by valueProperty

    val validProperty: BooleanProperty = SimpleBooleanProperty()
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

inline fun doubleField(): DoubleField = doubleField { }

inline fun doubleField(
    init: (@LayoutDsl DoubleField).() -> Unit
): DoubleField = DoubleField().apply(init)

inline fun LayoutManager<Node>.doubleField(): DoubleField = doubleField { }

inline fun LayoutManager<Node>.doubleField(
    init: (@LayoutDsl DoubleField).() -> Unit
): DoubleField = com.hendraanggrian.openpss.scene.control.doubleField(init).add()