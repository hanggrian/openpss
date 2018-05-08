@file:Suppress("NOTHING_TO_INLINE")

package com.hendraanggrian.openpss.controls

import com.hendraanggrian.openpss.resources.Display.XGA_
import javafx.beans.DefaultProperty
import javafx.beans.property.SimpleStringProperty
import javafx.beans.property.StringProperty
import javafx.scene.Node
import javafx.scene.control.Button
import javafx.scene.control.Tooltip
import javafx.scene.image.ImageView
import kotlinx.coroutines.experimental.delay
import ktfx.beans.binding.`when`
import ktfx.beans.binding.otherwise
import ktfx.beans.binding.then
import ktfx.beans.value.getValue
import ktfx.beans.value.greaterEq
import ktfx.beans.value.setValue
import ktfx.coroutines.listener
import ktfx.layouts.LayoutDsl
import ktfx.layouts.LayoutManager

/**
 * A button that will display text when the window have sufficient width.
 * When it doesn't, [AdaptableButton] will store its text as tooltip.
 */
@DefaultProperty("graphic")
class AdaptableButton(
    adaptableText: String,
    imageUrl: String? = null
) : Button(null, imageUrl?.let { ImageView(it) }) {

    private companion object {
        const val INIT_DELAY = 250
    }

    val adaptableTextProperty: StringProperty = SimpleStringProperty()
    var adaptableText: String by adaptableTextProperty

    init {
        this.adaptableTextProperty.listener { _, _, value ->
            when (scene != null && scene.widthProperty() != null) {
                true -> init(value)
                else -> {
                    while (scene == null || scene.widthProperty() == null) delay(INIT_DELAY)
                    init(value)
                }
            }
        }
        this.adaptableText = adaptableText
    }

    private fun init(text: String) {
        val condition = `when`(scene.widthProperty() greaterEq XGA_.width)
        textProperty().bind(condition then text otherwise "")
        tooltipProperty().bind(condition then null as Tooltip? otherwise Tooltip(text))
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