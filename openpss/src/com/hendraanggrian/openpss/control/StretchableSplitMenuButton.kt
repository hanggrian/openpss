@file:Suppress("NOTHING_TO_INLINE")

package com.hendraanggrian.openpss.control

import javafx.beans.DefaultProperty
import javafx.beans.property.DoubleProperty
import javafx.beans.property.SimpleDoubleProperty
import javafx.beans.property.SimpleStringProperty
import javafx.beans.property.StringProperty
import javafx.scene.Node
import javafx.scene.control.MenuItem
import javafx.scene.control.SplitMenuButton
import javafx.scene.image.ImageView
import ktfx.LayoutDsl
import ktfx.MenuItemManager
import ktfx.NodeManager
import ktfx.layouts.menuItem

/**
 * A button that will display text when the window have sufficient width.
 * When it doesn't, [StretchableSplitMenuButton] will store its text as tooltip.
 */
@DefaultProperty("graphic")
class StretchableSplitMenuButton @JvmOverloads constructor(
    stretchPoint: Double = -1.0,
    stretchableText: String? = null,
    graphic: Node? = null
) : SplitMenuButton(), StretchableLabeled, MenuItemManager {

    override fun <R : MenuItem> R.invoke(): R = also { items += it }

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

/** Creates an [StretchableSplitMenuButton]. */
fun stretchableSplitMenuButton(
    stretchPoint: Double,
    adaptableText: String,
    graphic: Node? = null,
    init: ((@LayoutDsl StretchableSplitMenuButton).() -> Unit)? = null
): StretchableSplitMenuButton = StretchableSplitMenuButton(
    stretchPoint,
    adaptableText,
    graphic
).also {
    init?.invoke(it)
}

/** Creates an [StretchableSplitMenuButton] and add it to this manager. */
inline fun NodeManager.stretchableSplitMenuButton(
    stretchPoint: Double,
    adaptableText: String,
    graphic: Node? = null,
    noinline init: ((@LayoutDsl StretchableSplitMenuButton).() -> Unit)? = null
): StretchableSplitMenuButton =
    com.hendraanggrian.openpss.control.stretchableSplitMenuButton(stretchPoint, adaptableText, graphic, init)()