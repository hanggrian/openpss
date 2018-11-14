package com.hendraanggrian.openpss.control.popover

import com.hendraanggrian.openpss.R
import com.hendraanggrian.openpss.content.Context
import com.hendraanggrian.openpss.control.ResultablePopup
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
        buttonInvokable.run {
            defaultButton = jfxButton(getString(R.string.ok)) {
                isDefaultButton = true
                styleClass += R.style.raised
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