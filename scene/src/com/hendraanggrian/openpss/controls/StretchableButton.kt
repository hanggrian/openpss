@file:Suppress("NOTHING_TO_INLINE")

package com.hendraanggrian.openpss.controls

import com.hendraanggrian.openpss.util.stretchableText
import javafx.beans.property.SimpleStringProperty
import javafx.beans.property.StringProperty
import javafx.scene.Node
import javafx.scene.control.Button
import javafx.scene.image.ImageView
import ktfx.beans.value.getValue
import ktfx.beans.value.setValue
import ktfx.coroutines.listener
import ktfx.layouts.LayoutDsl
import ktfx.layouts.LayoutManager

/**
 * A button that will display text when the window have sufficient width.
 * When it doesn't, [StretchableButton] will store its text as tooltip.
 */
class StretchableButton @JvmOverloads constructor(
    stretchableText: String? = null,
    graphicUrl: String? = null
) : Button() {

    private var stretchableTextProperty = SimpleStringProperty()
    fun stretchableTextProperty(): StringProperty = stretchableTextProperty
    var stretchableText: String? by stretchableTextProperty

    private var graphicUrlProperty = SimpleStringProperty()
    fun graphicUrlProperty(): StringProperty = graphicUrlProperty
    var graphicUrl: String? by graphicUrlProperty

    init {
        this.stretchableTextProperty.listener { _, _, value -> stretchableText(value) }
        this.stretchableText = stretchableText
        this.graphicUrlProperty.listener { _, _, value -> graphic = value?.let { ImageView(it) } }
        this.graphicUrl = graphicUrl
    }
}

/** Creates an [StretchableButton]. */
fun stretchableButton(
    adaptableText: String,
    imageUrl: String? = null,
    init: ((@LayoutDsl StretchableButton).() -> Unit)? = null
): StretchableButton = StretchableButton(adaptableText, imageUrl).also {
    init?.invoke(it)
}

/** Creates an [StretchableButton] and add it to this [LayoutManager]. */
inline fun LayoutManager<Node>.stretchableButton(
    adaptableText: String,
    imageUrl: String? = null,
    noinline init: ((@LayoutDsl StretchableButton).() -> Unit)? = null
): StretchableButton = com.hendraanggrian.openpss.controls.stretchableButton(adaptableText, imageUrl, init).add()

/** Create a styled [StretchableButton]. */
fun styledStretchableButton(
    styleClass: String,
    adaptableText: String,
    imageUrl: String? = null,
    init: ((@LayoutDsl StretchableButton).() -> Unit)? = null
): StretchableButton = StretchableButton(adaptableText, imageUrl).also {
    it.styleClass += styleClass
    init?.invoke(it)
}

/** Creates a styled [StretchableButton] and add it to this [LayoutManager]. */
inline fun LayoutManager<Node>.styledStretchableButton(
    styleClass: String,
    adaptableText: String,
    imageUrl: String? = null,
    noinline init: ((@LayoutDsl StretchableButton).() -> Unit)? = null
): StretchableButton = com.hendraanggrian.openpss.controls
    .styledStretchableButton(styleClass, adaptableText, imageUrl, init).add()