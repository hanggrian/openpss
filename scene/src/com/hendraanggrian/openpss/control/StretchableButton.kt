@file:Suppress("NOTHING_TO_INLINE")

package com.hendraanggrian.openpss.control

import javafx.beans.DefaultProperty
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
    stretchableText: String? = null,
    graphic: Node? = null
) : Button(null, graphic), StretchableLabeled {

    private val stretchableTextProperty = SimpleStringProperty()
    override fun stretchableTextProperty(): StringProperty = stretchableTextProperty

    init {
        initialize(stretchableText)
    }
}

/** Creates an [StretchableButton]. */
fun stretchableButton(
    adaptableText: String,
    graphic: Node? = null,
    init: ((@LayoutDsl StretchableButton).() -> Unit)? = null
): StretchableButton = StretchableButton(adaptableText, graphic).also {
    init?.invoke(it)
}

/** Creates an [StretchableButton] and add it to this [LayoutManager]. */
inline fun LayoutManager<Node>.stretchableButton(
    adaptableText: String,
    graphic: Node? = null,
    noinline init: ((@LayoutDsl StretchableButton).() -> Unit)? = null
): StretchableButton = com.hendraanggrian.openpss.control.stretchableButton(adaptableText, graphic, init).add()

/** Create a styled [StretchableButton]. */
fun styledStretchableButton(
    styleClass: String,
    adaptableText: String,
    graphic: Node? = null,
    init: ((@LayoutDsl StretchableButton).() -> Unit)? = null
): StretchableButton = StretchableButton(adaptableText, graphic).also {
    it.styleClass += styleClass
    init?.invoke(it)
}

/** Creates a styled [StretchableButton] and add it to this [LayoutManager]. */
inline fun LayoutManager<Node>.styledStretchableButton(
    styleClass: String,
    adaptableText: String,
    graphic: Node? = null,
    noinline init: ((@LayoutDsl StretchableButton).() -> Unit)? = null
): StretchableButton = com.hendraanggrian.openpss.control
    .styledStretchableButton(styleClass, adaptableText, graphic, init).add()