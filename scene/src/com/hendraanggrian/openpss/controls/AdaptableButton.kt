@file:Suppress("NOTHING_TO_INLINE")

package com.hendraanggrian.openpss.controls

import com.hendraanggrian.openpss.util.adaptableText
import javafx.beans.DefaultProperty
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
 * When it doesn't, [AdaptableButton] will store its text as tooltip.
 */
@DefaultProperty("graphic")
class AdaptableButton @JvmOverloads constructor(
    adaptableText: String,
    imageUrl: String? = null
) : Button(null, imageUrl?.let { ImageView(it) }) {

    val adaptableTextProperty: StringProperty = SimpleStringProperty()
    var adaptableText: String by adaptableTextProperty

    init {
        this.adaptableTextProperty.listener { _, _, value -> adaptableText(value) }
        this.adaptableText = adaptableText
    }
}

/** Creates an [AdaptableButton]. */
fun adaptableButton(
    adaptableText: String,
    imageUrl: String? = null,
    init: ((@LayoutDsl AdaptableButton).() -> Unit)? = null
): AdaptableButton = AdaptableButton(adaptableText, imageUrl).also {
    init?.invoke(it)
}

/** Creates an [AdaptableButton] and add it to this [LayoutManager]. */
inline fun LayoutManager<Node>.adaptableButton(
    adaptableText: String,
    imageUrl: String? = null,
    noinline init: ((@LayoutDsl AdaptableButton).() -> Unit)? = null
): AdaptableButton = com.hendraanggrian.openpss.controls.adaptableButton(adaptableText, imageUrl, init).add()

/** Create a styled [AdaptableButton]. */
fun styledAdaptableButton(
    styleClass: String,
    adaptableText: String,
    imageUrl: String? = null,
    init: ((@LayoutDsl AdaptableButton).() -> Unit)? = null
): AdaptableButton = AdaptableButton(adaptableText, imageUrl).also {
    it.styleClass += styleClass
    init?.invoke(it)
}

/** Creates a styled [AdaptableButton] and add it to this [LayoutManager]. */
inline fun LayoutManager<Node>.styledAdaptableButton(
    styleClass: String,
    adaptableText: String,
    imageUrl: String? = null,
    noinline init: ((@LayoutDsl AdaptableButton).() -> Unit)? = null
): AdaptableButton = com.hendraanggrian.openpss.controls
    .styledAdaptableButton(styleClass, adaptableText, imageUrl, init).add()