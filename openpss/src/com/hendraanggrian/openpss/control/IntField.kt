@file:Suppress("NOTHING_TO_INLINE")

package com.hendraanggrian.openpss.control

import javafx.beans.property.IntegerProperty
import javafx.beans.property.SimpleIntegerProperty
import javafx.scene.control.TextField
import ktfx.NodeManager
import ktfx.annotations.LayoutDsl
import ktfx.beans.value.getValue
import ktfx.beans.value.setValue
import ktfx.coroutines.listener
import ktfx.listeners.bindBidirectional

open class IntField : TextField() {

    private val valueProperty = SimpleIntegerProperty()
    fun valueProperty(): IntegerProperty = valueProperty
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

/** Creates a [IntField]. */
fun intField(
    init: ((@LayoutDsl IntField).() -> Unit)? = null
): IntField = IntField().also {
    init?.invoke(it)
}

/** Creates a [IntField] and add it to this [LayoutManager]. */
inline fun NodeManager.intField(
    noinline init: ((@LayoutDsl IntField).() -> Unit)? = null
): IntField = (com.hendraanggrian.openpss.control.intField(init))()