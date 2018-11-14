@file:Suppress("NOTHING_TO_INLINE")

package com.hendraanggrian.openpss.control

import com.hendraanggrian.openpss.control.base.IntFieldBase
import javafx.beans.property.IntegerProperty
import javafx.beans.property.SimpleIntegerProperty
import javafx.scene.control.TextField
import ktfx.beans.value.getValue
import ktfx.beans.value.setValue
import ktfx.layouts.LayoutDsl
import ktfx.layouts.NodeInvokable

class IntField : TextField(), IntFieldBase {

    override val actual: TextField get() = this

    private val valueProperty = SimpleIntegerProperty()
    override fun valueProperty(): IntegerProperty = valueProperty
    var value: Int by valueProperty

    init {
        initialize()
    }
}

/** Creates a [IntField]. */
fun intField(
    init: ((@LayoutDsl IntField).() -> Unit)? = null
): IntField = IntField().also {
    init?.invoke(it)
}

/** Creates a [IntField] and add it to this manager. */
inline fun NodeInvokable.intField(
    noinline init: ((@LayoutDsl IntField).() -> Unit)? = null
): IntField = com.hendraanggrian.openpss.control.intField(init)()