@file:Suppress("NOTHING_TO_INLINE")

package com.hendraanggrian.openpss.control

import com.hendraanggrian.openpss.control.base.DoubleFieldBase
import javafx.beans.property.BooleanProperty
import javafx.beans.property.DoubleProperty
import javafx.beans.property.SimpleBooleanProperty
import javafx.beans.property.SimpleDoubleProperty
import javafx.scene.control.TextField
import ktfx.beans.value.getValue
import ktfx.beans.value.setValue
import ktfx.layouts.LayoutDsl
import ktfx.layouts.NodeInvokable

class DoubleField : TextField(), DoubleFieldBase {

    override val actual: TextField get() = this

    private val valueProperty = SimpleDoubleProperty()
    override fun valueProperty(): DoubleProperty = valueProperty
    var value: Double by valueProperty

    private val validProperty = SimpleBooleanProperty()
    override fun validProperty(): BooleanProperty = validProperty
    val isValid: Boolean by validProperty

    init {
        initialize()
    }
}

/** Creates a [DoubleField]. */
fun doubleField(
    init: ((@LayoutDsl DoubleField).() -> Unit)? = null
): DoubleField = DoubleField().also {
    init?.invoke(it)
}

/** Creates a [DoubleField] and add it to this manager. */
inline fun NodeInvokable.doubleField(
    noinline init: ((@LayoutDsl DoubleField).() -> Unit)? = null
): DoubleField = (com.hendraanggrian.openpss.control.doubleField(init))()