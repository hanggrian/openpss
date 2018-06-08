@file:Suppress("NOTHING_TO_INLINE")

package com.hendraanggrian.openpss.control

import javafx.beans.DefaultProperty
import javafx.beans.property.IntegerProperty
import javafx.beans.property.SimpleIntegerProperty
import javafx.beans.property.SimpleStringProperty
import javafx.beans.property.StringProperty
import javafx.scene.Node
import javafx.scene.control.ToggleButton
import ktfx.layouts.LayoutDsl
import ktfx.layouts.LayoutManager

@DefaultProperty("graphic")
class StretchableToggleButton @JvmOverloads constructor(
    stretchPoint: Int = -1,
    stretchableText: String? = null,
    graphic: Node? = null
) : ToggleButton(null, graphic), StretchableLabeled {

    private val stretchPointProperty = SimpleIntegerProperty(stretchPoint)
    override fun stretchPointProperty(): IntegerProperty = stretchPointProperty

    private val stretchableTextProperty = SimpleStringProperty(stretchableText)
    override fun stretchableTextProperty(): StringProperty = stretchableTextProperty

    init {
        initialize()
    }
}

/** Creates an [StretchableToggleButton]. */
fun stretchableToggleButton(
    stretchPoint: Int,
    adaptableText: String,
    graphic: Node? = null,
    init: ((@LayoutDsl StretchableToggleButton).() -> Unit)? = null
): StretchableToggleButton = StretchableToggleButton(stretchPoint, adaptableText, graphic).also {
    init?.invoke(it)
}

/** Creates an [StretchableToggleButton] and add it to this [LayoutManager]. */
inline fun LayoutManager<Node>.stretchableToggleButton(
    stretchPoint: Int,
    adaptableText: String,
    graphic: Node? = null,
    noinline init: ((@LayoutDsl StretchableToggleButton).() -> Unit)? = null
): StretchableToggleButton =
    com.hendraanggrian.openpss.control.stretchableToggleButton(stretchPoint, adaptableText, graphic, init)()

/** Create a styled [StretchableToggleButton]. */
fun styledStretchableToggleButton(
    styleClass: String,
    stretchPoint: Int,
    adaptableText: String,
    graphic: Node? = null,
    init: ((@LayoutDsl StretchableToggleButton).() -> Unit)? = null
): StretchableToggleButton = StretchableToggleButton(stretchPoint, adaptableText, graphic).also {
    it.styleClass += styleClass
    init?.invoke(it)
}

/** Creates a styled [StretchableToggleButton] and add it to this [LayoutManager]. */
inline fun LayoutManager<Node>.styledStretchableToggleButton(
    styleClass: String,
    stretchPoint: Int,
    adaptableText: String,
    graphic: Node? = null,
    noinline init: ((@LayoutDsl StretchableToggleButton).() -> Unit)? = null
): StretchableToggleButton = com.hendraanggrian.openpss.control
    .styledStretchableToggleButton(styleClass, stretchPoint, adaptableText, graphic, init)()