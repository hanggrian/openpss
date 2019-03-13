@file:Suppress("NOTHING_TO_INLINE")

package com.hendraanggrian.openpss.control

import com.hendraanggrian.openpss.R
import com.jfoenix.controls.JFXButton
import javafx.beans.DefaultProperty
import javafx.beans.property.StringProperty
import javafx.scene.Node
import javafx.scene.control.Tooltip
import javafx.scene.image.ImageView
import ktfx.bindings.buildBinding
import ktfx.getValue
import ktfx.layouts.LayoutMarker
import ktfx.layouts.NodeManager
import ktfx.setValue
import ktfx.toProperty

@DefaultProperty("graphic")
class Action @JvmOverloads constructor(text: String? = null, graphic: Node? = null) :
    JFXButton(null, graphic) {

    private val tooltipTextProperty = text.toProperty()

    fun tooltipTextProperty(): StringProperty = tooltipTextProperty

    var tooltipText: String? by tooltipTextProperty

    init {
        buttonType = ButtonType.FLAT
        styleClass += R.style.flat
        tooltipProperty().bind(buildBinding(tooltipTextProperty) {
            tooltipText?.let { Tooltip(it) }
        })
    }
}

fun action(
    text: String? = null,
    graphic: Node? = null,
    init: ((@LayoutMarker Action).() -> Unit)?
): Action = Action(text, graphic).also { init?.invoke(it) }

inline fun action(
    text: String? = null,
    graphic: String? = null,
    noinline init: ((@LayoutMarker Action).() -> Unit)?
): Action = action(text, graphic?.let { ImageView(it) }, init)

inline fun NodeManager.action(
    text: String? = null,
    graphic: Node? = null,
    noinline init: ((@LayoutMarker Action).() -> Unit)?
): Action = com.hendraanggrian.openpss.control.action(text, graphic, init).add()

inline fun NodeManager.action(
    text: String? = null,
    graphic: String? = null,
    noinline init: ((@LayoutMarker Action).() -> Unit)?
): Action = com.hendraanggrian.openpss.control.action(text, graphic, init).add()