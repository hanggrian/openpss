@file:Suppress("NOTHING_TO_INLINE")

package com.hendraanggrian.openpss.control

import com.hendraanggrian.openpss.control.base.DoubleFieldBase
import com.jfoenix.controls.JFXTextField
import javafx.beans.property.BooleanProperty
import javafx.beans.property.DoubleProperty
import javafx.beans.property.SimpleBooleanProperty
import javafx.beans.property.SimpleDoubleProperty
import javafx.scene.control.TextField
import ktfx.LayoutDsl
import ktfx.NodeInvokable
import ktfx.beans.value.getValue
import ktfx.beans.value.setValue

class JFXDoubleField : JFXTextField(), DoubleFieldBase {

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

/** Creates a [JFXDoubleField]. */
fun jfxDoubleField(
    init: ((@LayoutDsl JFXDoubleField).() -> Unit)? = null
): JFXDoubleField = JFXDoubleField().also {
    init?.invoke(it)
}

/** Creates a [JFXDoubleField] and add it to this manager. */
inline fun NodeInvokable.jfxDoubleField(
    noinline init: ((@LayoutDsl JFXDoubleField).() -> Unit)? = null
): JFXDoubleField = (com.hendraanggrian.openpss.control.jfxDoubleField(init))()