package com.hendraanggrian.openpss.control.dialog

import com.hendraanggrian.openpss.R
import com.hendraanggrian.openpss.content.Context
import com.hendraanggrian.openpss.control.ResultablePopup
import com.jfoenix.controls.JFXButton
import javafx.scene.control.Button
import kotlinx.coroutines.CoroutineScope
import ktfx.coroutines.onAction
import ktfx.jfoenix.jfxButton

open class ResultableDialog<T>(
    context: Context,
    titleId: String
) : Dialog(context, titleId), ResultablePopup<T> {

    protected val defaultButton: Button

    init {
        buttonInvokable.run {
            defaultButton = jfxButton(getString(R.string.ok)) {
                isDefaultButton = true
                styleClass += "raised"
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