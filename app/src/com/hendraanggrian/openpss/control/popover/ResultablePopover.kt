package com.hendraanggrian.openpss.control.popover

import com.hendraanggrian.openpss.R
import com.hendraanggrian.openpss.control.Resultable
import com.hendraanggrian.openpss.localization.Resourced
import javafx.scene.Node
import javafx.scene.control.Button
import ktfx.coroutines.onAction
import org.controlsfx.control.PopOver

/** [PopOver] with default button and return type. */
abstract class ResultablePopover<T>(
    resourced: Resourced,
    titleId: String
) : Popover(resourced, titleId), Resultable<T> {

    protected val defaultButton: Button = ktfx.layouts.button(getString(R.string.ok)) {
        isDefaultButton = true
    }

    init {
        buttonBar.buttons += defaultButton
    }

    fun showAt(node: Node, onAction: (T) -> Unit) {
        showAt(node)
        defaultButton.onAction {
            onAction(optionalResult!!)
            hide()
        }
    }
}