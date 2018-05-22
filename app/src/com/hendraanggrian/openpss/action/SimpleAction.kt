package com.hendraanggrian.openpss.action

import com.hendraanggrian.openpss.localization.Resourced
import javafx.event.ActionEvent
import org.controlsfx.control.action.Action

class SimpleAction(
    resourced: Resourced,
    textId: String,
    action: (ActionEvent) -> Unit
) : Action(action), Resourced by resourced {

    init {
        text = getString(textId)
    }
}