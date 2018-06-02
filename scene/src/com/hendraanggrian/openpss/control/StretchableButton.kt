@file:Suppress("NOTHING_TO_INLINE")

package com.hendraanggrian.openpss.control

import javafx.beans.DefaultProperty
import javafx.beans.property.IntegerProperty
import javafx.beans.property.SimpleIntegerProperty
import javafx.beans.property.SimpleStringProperty
import javafx.beans.property.StringProperty
import javafx.scene.Node
import javafx.scene.control.Button
import ktfx.layouts.LayoutDsl
import ktfx.layouts.LayoutManager

/**
 * A button that will display text when the window have sufficient width.
 * When it doesn't, [StretchableButton] will store its text as tooltip.
 */
@DefaultProperty("graphic")
class StretchableButton @JvmOverloads constructor(
    stretchPoint: Int = -1,
    stretchableText: String? = null,
    graphic: Node? = null
) : Button(null, graphic), StretchableLabeled {

    private val stretchPointProperty = SimpleIntegerProperty(stretchPoint)
    override fun stretchPointProperty(): IntegerProperty = stretchPointProperty

    private val stretchableTextProperty = SimpleStringProperty(stretchableText)
    override fun stretchableTextProperty(): StringProperty = stretchableTextProperty

    init {
        initialize()
    }
}

/** Creates an [StretchableButton]. */
fun stretchableButton(
    stretchPoint: Int,
    adaptableText: String,
    graphic: Node? = null,
    init: ((@LayoutDsl StretchableButton).() -> Unit)? = null
): StretchableButton = StretchableButton(stretchPoint, adaptableText, graphic).also {
    init?.invoke(it)
}

/** Creates an [StretchableButton] and add it to this [LayoutManager]. */
inline fun LayoutManager<Node>.stretchableButton(
    stretchPoint: Int,
    adaptableText: String,
    graphic: Node? = null,
    noinline init: ((@LayoutDsl StretchableButton).() -> Unit)? = null
): StretchableButton = com.hendraanggrian.openpss.control.stretchableButton(stretchPoint, adaptableText, graphic, init)
    .add()

/** Create a styled [StretchableButton]. */
fun styledStretchableButton(
    styleClass: String,
    stretchPoint: Int,
    adaptableText: String,
    graphic: Node? = null,
    init: ((@LayoutDsl StretchableButton).() -> Unit)? = null
): StretchableButton = StretchableButton(stretchPoint, adaptableText, graphic).also {
    it.styleClass += styleClass
    init?.invoke(it)
}

/** Creates a styled [StretchableButton] and add it to this [LayoutManager]. */
inline fun LayoutManager<Node>.styledStretchableButton(
    styleClass: String,
    stretchPoint: Int,
    adaptableText: String,
    graphic: Node? = null,
    noinline init: ((@LayoutDsl StretchableButton).() -> Unit)? = null
): StretchableButton = com.hendraanggrian.openpss.control
    .styledStretchableButton(styleClass, stretchPoint, adaptableText, graphic, init).add()