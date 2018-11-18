package com.hendraanggrian.openpss.popup.popover

import com.hendraanggrian.openpss.content.Context
import com.hendraanggrian.openpss.popup.ResultablePopup
import javafx.scene.Node
import javafx.scene.control.Button
import ktfx.coroutines.onAction
import org.controlsfx.control.PopOver

/** [PopOver] with default button and return type. */
open class ResultablePopover<T>(
    context: Context,
    titleId: String
) : Popover(context, titleId), ResultablePopup<T> {

    override lateinit var defaultButton: Button

    fun show(node: Node, onAction: (T?) -> Unit) {
        show(node)
        defaultButton.onAction {
            onAction(nullableResult!!)
            hide()
        }
    }
}