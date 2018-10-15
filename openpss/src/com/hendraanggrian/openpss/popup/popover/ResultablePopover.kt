package com.hendraanggrian.openpss.popup.popover

import com.hendraanggrian.openpss.App
import com.hendraanggrian.openpss.R
import com.hendraanggrian.openpss.i18n.Resourced
import com.hendraanggrian.openpss.popup.ResultablePopup
import com.jfoenix.controls.JFXButton
import javafx.scene.Node
import javafx.scene.control.Button
import ktfx.NodeManager
import ktfx.coroutines.onAction
import ktfx.jfoenix.jfxButton
import org.controlsfx.control.PopOver

/** [PopOver] with default button and return type. */
abstract class ResultablePopover<T>(
    resourced: Resourced,
    titleId: String
) : Popover(resourced, titleId),
    ResultablePopup<T> {

    protected lateinit var defaultButton: Button

    override fun onCreateActions(manager: NodeManager) {
        super.onCreateActions(manager)
        manager.run {
            defaultButton = jfxButton(getString(R.string.ok)) {
                isDefaultButton = true
                styleClass += App.STYLE_BUTTON_RAISED
                buttonType = JFXButton.ButtonType.RAISED
            }
        }
    }

    fun showAt(node: Node, onAction: (T) -> Unit) {
        showAt(node)
        defaultButton.onAction {
            onAction(nullableResult!!)
            hide()
        }
    }
}