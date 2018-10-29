package com.hendraanggrian.openpss.popup.dialog

import com.hendraanggrian.openpss.App
import com.hendraanggrian.openpss.Context
import com.hendraanggrian.openpss.R
import com.hendraanggrian.openpss.popup.ResultablePopup
import com.jfoenix.controls.JFXButton
import javafx.scene.control.Button
import kotlinx.coroutines.CoroutineScope
import ktfx.coroutines.onAction
import ktfx.jfoenix.jfxButton

open class ResultableDialog<T>(
    context: Context,
    titleId: String
) : Dialog(context, titleId), ResultablePopup<T> {

    protected var defaultButton: Button

    init {
        buttonManager.run {
            defaultButton = jfxButton(getString(R.string.ok)) {
                isDefaultButton = true
                styleClass += App.STYLE_BUTTON_RAISED
                buttonType = JFXButton.ButtonType.RAISED
            }
        }
    }

    fun show(onAction: suspend CoroutineScope.(T?) -> Unit) {
        super.show()
        defaultButton.onAction {
            onAction(nullableResult)
            close()
        }
    }
}