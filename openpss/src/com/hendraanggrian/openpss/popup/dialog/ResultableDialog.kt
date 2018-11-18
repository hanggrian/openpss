package com.hendraanggrian.openpss.popup.dialog

import com.hendraanggrian.openpss.content.Context
import com.hendraanggrian.openpss.popup.ResultablePopup
import javafx.scene.control.Button
import kotlinx.coroutines.CoroutineScope
import ktfx.coroutines.onAction

open class ResultableDialog<T>(
    context: Context,
    titleId: String
) : Dialog(context, titleId), ResultablePopup<T> {

    override lateinit var defaultButton: Button

    fun show(onAction: suspend CoroutineScope.(T?) -> Unit) {
        super.show()
        defaultButton.onAction {
            onAction(nullableResult)
            close()
        }
    }
}