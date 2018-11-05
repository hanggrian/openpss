@file:Suppress("NOTHING_TO_INLINE")

package com.hendraanggrian.openpss.control

import com.hendraanggrian.openpss.control.base.StretchableLabeled
import com.jfoenix.controls.JFXCheckBox
import javafx.beans.DefaultProperty
import javafx.beans.property.DoubleProperty
import javafx.beans.property.SimpleDoubleProperty
import javafx.beans.property.SimpleStringProperty
import javafx.beans.property.StringProperty
import ktfx.LayoutDsl
import ktfx.NodeInvokable

@DefaultProperty("graphic")
class StretchableCheckBox @JvmOverloads constructor(
    stretchPoint: Double = -1.0,
    stretchableText: String? = null
) : JFXCheckBox(null), StretchableLabeled {

    private val stretchPointProperty = SimpleDoubleProperty(stretchPoint)
    override fun stretchPointProperty(): DoubleProperty = stretchPointProperty

    private val stretchableTextProperty = SimpleStringProperty(stretchableText)
    override fun stretchableTextProperty(): StringProperty = stretchableTextProperty

    init {
        initialize()
    }
}

/** Creates an [StretchableCheckBox]. */
fun stretchableCheckBox(
    stretchPoint: Double,
    adaptableText: String,
    init: ((@LayoutDsl StretchableCheckBox).() -> Unit)? = null
): StretchableCheckBox = StretchableCheckBox(
    stretchPoint,
    adaptableText
).also {
    init?.invoke(it)
}

/** Creates an [StretchableCheckBox] and add it to this manager. */
inline fun NodeInvokable.stretchableCheckBox(
    stretchPoint: Double,
    adaptableText: String,
    noinline init: ((@LayoutDsl StretchableCheckBox).() -> Unit)? = null
): StretchableCheckBox = com.hendraanggrian.openpss.control.stretchableCheckBox(stretchPoint, adaptableText, init)()