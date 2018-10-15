package com.hendraanggrian.openpss.popup.dialog

import com.hendraanggrian.openpss.App
import com.hendraanggrian.openpss.R
import com.hendraanggrian.openpss.i18n.Resourced
import com.hendraanggrian.openpss.popup.ResultablePopup
import com.jfoenix.controls.JFXButton
import javafx.scene.control.Button
import javafx.scene.layout.StackPane
import kotlinx.coroutines.experimental.CoroutineScope
import ktfx.NodeManager
import ktfx.coroutines.onAction
import ktfx.jfoenix.jfxButton

open class ResultableDialog<T>(
    resourced: Resourced,
    titleId: String
) : Dialog(resourced, titleId), ResultablePopup<T> {

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

    fun show(container: StackPane, onAction: suspend CoroutineScope.(T?) -> Unit) {
        show(container)
        defaultButton.onAction {
            onAction(nullableResult)
            close()
        }
    }
}