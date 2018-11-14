@file:Suppress("NOTHING_TO_INLINE")

package com.hendraanggrian.openpss.control

import com.hendraanggrian.openpss.control.base.IntFieldBase
import com.jfoenix.controls.JFXTextField
import javafx.beans.property.IntegerProperty
import javafx.beans.property.SimpleIntegerProperty
import javafx.scene.control.TextField
import ktfx.beans.value.getValue
import ktfx.beans.value.setValue
import ktfx.layouts.LayoutDsl
import ktfx.layouts.NodeInvokable

class JFXIntField : JFXTextField(), IntFieldBase {

    override val actual: TextField get() = this

    private val valueProperty = SimpleIntegerProperty()
    override fun valueProperty(): IntegerProperty = valueProperty
    var value: Int by valueProperty

    init {
        initialize()
    }
}

/** Creates a [JFXIntField]. */
fun jfxIntField(
    init: ((@LayoutDsl JFXIntField).() -> Unit)? = null
): JFXIntField = JFXIntField().also {
    init?.invoke(it)
}

/** Creates a [JFXIntField] and add it to this manager. */
inline fun NodeInvokable.jfxIntField(
    noinline init: ((@LayoutDsl JFXIntField).() -> Unit)? = null
): JFXIntField = com.hendraanggrian.openpss.control.jfxIntField(init)()