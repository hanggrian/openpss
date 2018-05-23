package com.hendraanggrian.openpss.action

import com.hendraanggrian.openpss.localization.Resourced
import javafx.event.ActionEvent
import org.controlsfx.control.action.Action

@Suppress("LeakingThis")
abstract class SimpleAction(
    resourced: Resourced,
    textId: String
) : Action("", null), Resourced by resourced {

    abstract fun onAction(event: ActionEvent): Boolean

    open fun onSuccess() {
    }

    open fun onError() {
    }

    init {
        text = getString(textId)
        setEventHandler {
            when {
                onAction(it) -> onSuccess()
                else -> onError()
            }
        }
    }
}