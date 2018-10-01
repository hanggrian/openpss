@file:Suppress("NOTHING_TO_INLINE")

package com.hendraanggrian.openpss.control

import javafx.beans.DefaultProperty
import javafx.beans.property.DoubleProperty
import javafx.beans.property.SimpleDoubleProperty
import javafx.beans.property.SimpleStringProperty
import javafx.beans.property.StringProperty
import javafx.scene.Node
import javafx.scene.control.MenuButton
import javafx.scene.control.MenuItem
import javafx.scene.image.ImageView
import ktfx.layouts.LayoutDsl
import ktfx.layouts.LayoutManager
import ktfx.layouts.menuItem

/**
 * A button that will display text when the window have sufficient width.
 * When it doesn't, [StretchableMenuButton] will store its text as tooltip.
 */
@DefaultProperty("graphic")
class StretchableMenuButton @JvmOverloads constructor(
    stretchPoint: Double = -1.0,
    stretchableText: String? = null,
    graphic: Node? = null
) : MenuButton(), StretchableLabeled, LayoutManager<MenuItem> {

    override val childs: MutableCollection<MenuItem> get() = items

    private val stretchPointProperty = SimpleDoubleProperty(stretchPoint)
    override fun stretchPointProperty(): DoubleProperty = stretchPointProperty

    private val stretchableTextProperty = SimpleStringProperty(stretchableText)
    override fun stretchableTextProperty(): StringProperty = stretchableTextProperty

    init {
        initialize()
        setGraphic(graphic)
    }

    inline operator fun String.invoke(
        graphic: ImageView? = null,
        noinline init: ((@LayoutDsl MenuItem).() -> Unit)? = null
    ): MenuItem = menuItem(this, graphic, init)
}

/** Creates an [StretchableMenuButton]. */
fun stretchableMenuButton(
    stretchPoint: Double,
    adaptableText: String,
    graphic: Node? = null,
    init: ((@LayoutDsl StretchableMenuButton).() -> Unit)? = null
): StretchableMenuButton = StretchableMenuButton(stretchPoint, adaptableText, graphic).also {
    init?.invoke(it)
}

/** Creates an [StretchableMenuButton] and add it to this [LayoutManager]. */
inline fun LayoutManager<Node>.stretchableMenuButton(
    stretchPoint: Double,
    adaptableText: String,
    graphic: Node? = null,
    noinline init: ((@LayoutDsl StretchableMenuButton).() -> Unit)? = null
): StretchableMenuButton =
    com.hendraanggrian.openpss.control.stretchableMenuButton(stretchPoint, adaptableText, graphic, init)()