package com.hendraanggrian.openpss.control.dialog

import com.hendraanggrian.openpss.control.Resultable
import com.hendraanggrian.openpss.i18n.Resourced
import javafx.scene.control.Button
import javafx.scene.layout.StackPane
import kotlinx.coroutines.experimental.CoroutineScope
import ktfx.coroutines.onAction

abstract class MaterialResultableDialog<T>(
    resourced: Resourced,
    titleId: String
) : MaterialDialog(resourced, titleId), Resultable<T> {

    protected lateinit var defaultButton: Button

    fun show(container: StackPane, onAction: suspend CoroutineScope.(T?) -> Unit) {
        show(container)
        defaultButton.onAction {
            onAction(nullableResult)
            close()
        }
    }
}