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
import ktfx.layouts.LayoutDsl
import ktfx.layouts.MenuItemInvokable
import ktfx.layouts.menuItem

/**
 * A button that will display log when the window have sufficient width.
 * When it doesn't, [StretchableSplitMenuButton] will store its log as tooltip.
 */
@DefaultProperty("graphic")
class StretchableSplitMenuButton @JvmOverloads constructor(
    stretchPoint: Double = -1.0,
    stretchableText: String? = null,
    graphic: Node? = null
) : SplitMenuButton(), StretchableLabeled, MenuItemInvokable {

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