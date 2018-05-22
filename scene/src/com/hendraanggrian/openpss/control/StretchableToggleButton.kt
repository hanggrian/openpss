@file:Suppress("NOTHING_TO_INLINE")

package com.hendraanggrian.openpss.control

import javafx.beans.DefaultProperty
import javafx.beans.property.SimpleStringProperty
import javafx.beans.property.StringProperty
import javafx.scene.Node
import javafx.scene.control.ToggleButton
import ktfx.layouts.LayoutDsl
import ktfx.layouts.LayoutManager

@DefaultProperty("graphic")
class StretchableToggleButton @JvmOverloads constructor(
    stretchableText: String? = null,
    graphic: Node? = null
) : ToggleButton(null, graphic), StretchableLabeled {

    private val stretchableTextProperty = SimpleStringProperty()
    override fun stretchableTextProperty(): StringProperty = stretchableTextProperty

    init {
        initialize(stretchableText)
    }
}

/** Creates an [StretchableToggleButton]. */
fun stretchableToggleButton(
    adaptableText: String,
    graphic: Node? = null,
    init: ((@LayoutDsl StretchableToggleButton).() -> Unit)? = null
): StretchableToggleButton = StretchableToggleButton(adaptableText, graphic).also {
    init?.invoke(it)
}

/** Creates an [StretchableToggleButton] and add it to this [LayoutManager]. */
inline fun LayoutManager<Node>.stretchableToggleButton(
    adaptableText: String,
    graphic: Node? = null,
    noinline init: ((@LayoutDsl StretchableButton).() -> Unit)? = null
): StretchableButton = com.hendraanggrian.openpss.control.stretchableButton(adaptableText, graphic, init).add()

/** Create a styled [StretchableToggleButton]. */
fun styledStretchableToggleButton(
    styleClass: String,
    adaptableText: String,
    graphic: Node? = null,
    init: ((@LayoutDsl StretchableToggleButton).() -> Unit)? = null
): StretchableToggleButton = StretchableToggleButton(adaptableText, graphic).also {
    it.styleClass += styleClass
    init?.invoke(it)
}

/** Creates a styled [StretchableToggleButton] and add it to this [LayoutManager]. */
inline fun LayoutManager<Node>.styledStretchableToggleButton(
    styleClass: String,
    adaptableText: String,
    graphic: Node? = null,
    noinline init: ((@LayoutDsl StretchableToggleButton).() -> Unit)? = null
): StretchableToggleButton = com.hendraanggrian.openpss.control
    .styledStretchableToggleButton(styleClass, adaptableText, graphic, init).add()