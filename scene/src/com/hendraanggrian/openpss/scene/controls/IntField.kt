@file:Suppress("NOTHING_TO_INLINE", "UNUSED")

package com.hendraanggrian.openpss.scene.controls

import javafx.beans.property.IntegerProperty
import javafx.beans.property.SimpleIntegerProperty
import javafx.scene.Node
import javafx.scene.control.TextField
import ktfx.beans.value.getValue
import ktfx.beans.value.setValue
import ktfx.coroutines.listener
import ktfx.layouts.LayoutDsl
import ktfx.layouts.LayoutManager
import ktfx.listeners.bindBidirectional

open class IntField : TextField() {

    val valueProperty: IntegerProperty = SimpleIntegerProperty()
    var value: Int by valueProperty

    init {
        textProperty().bindBidirectional(valueProperty) {
            fromString { it.toIntOrNull() ?: 0 }
        }
        textProperty().addListener { _, oldValue, value ->
            text = if (value.isEmpty()) "0" else value.toIntOrNull()?.toString() ?: oldValue
            end()
        }
        focusedProperty().listener { _, _, focused -> if (focused && text.isNotEmpty()) selectAll() }
    }
}

inline fun intField(): IntField = intField { }

inline fun intField(
    init: (@LayoutDsl IntField).() -> Unit
): IntField = IntField().apply(init)

inline fun LayoutManager<Node>.intField(): IntField = intField { }

inline fun LayoutManager<Node>.intField(
    init: (@LayoutDsl IntField).() -> Unit
): IntField = com.hendraanggrian.openpss.scene.controls.intField(init).add()