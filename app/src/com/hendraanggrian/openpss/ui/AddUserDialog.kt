package com.hendraanggrian.openpss.ui

import com.hendraanggrian.openpss.R
import com.hendraanggrian.openpss.utils.clean
import com.hendraanggrian.openpss.utils.isName
import javafx.scene.control.ButtonBar.ButtonData.OK_DONE
import javafx.scene.control.ButtonType.OK
import javafx.scene.control.TextInputDialog
import javafx.scene.image.ImageView
import ktfx.beans.binding.booleanBindingOf
import ktfx.scene.control.graphicIcon
import ktfx.scene.control.headerTitle

class AddUserDialog(
    resourced: Resourced,
    headerId: String,
    graphicId: String
) : TextInputDialog(), Resourced by resourced {

    init {
        headerTitle = getString(headerId)
        graphicIcon = ImageView(graphicId)
        contentText = getString(R.string.name)
        dialogPane.lookupButton(OK).disableProperty().bind(booleanBindingOf(editor.textProperty()) {
            !editor.text.isName()
        })
        setResultConverter {
            when {
                it.buttonData != OK_DONE -> null
                else -> editor.text.clean()
            }
        }
    }
}