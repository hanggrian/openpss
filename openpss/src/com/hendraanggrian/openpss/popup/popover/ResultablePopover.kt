package com.hendraanggrian.openpss.popup.popover

import com.hendraanggrian.openpss.App
import com.hendraanggrian.openpss.Context
import com.hendraanggrian.openpss.R
import com.hendraanggrian.openpss.popup.ResultablePopup
import com.jfoenix.controls.JFXButton
import javafx.scene.Node
import javafx.scene.control.Button
import ktfx.coroutines.onAction
import ktfx.jfoenix.jfxButton
import org.controlsfx.control.PopOver

/** [PopOver] with default button and return type. */
open class ResultablePopover<T>(
    context: Context,
    titleId: String
) : Popover(context, titleId), ResultablePopup<T> {

    protected val defaultButton: Button

    init {
        buttonManager.run {
            defaultButton = jfxButton(getString(R.string.ok)) {
                isDefaultButton = true
                styleClass += App.STYLE_BUTTON_RAISED
                buttonType = JFXButton.ButtonType.RAISED
            }
        }
    }

    fun show(node: Node, onAction: (T?) -> Unit) {
        show(node)
        defaultButton.onAction {
            onAction(nullableResult!!)
            hide()
        }
    }
}