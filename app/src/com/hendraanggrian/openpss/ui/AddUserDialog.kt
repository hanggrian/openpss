package com.hendraanggrian.openpss.ui

import com.hendraanggrian.openpss.R
import com.hendraanggrian.openpss.utils.clean
import com.hendraanggrian.openpss.utils.isName
import javafx.scene.control.ButtonBar.ButtonData.OK_DONE
import javafx.scene.control.TextInputDialog
import javafx.scene.image.ImageView
import ktfx.scene.control.errorAlert
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
        setResultConverter {
            when {
                it.buttonData != OK_DONE -> null
                editor.text.isName() -> editor.text.clean()
                else -> {
                    errorAlert(getString(R.string.complete_name_is_required)).showAndWait()
                    null
                }
            }
        }
    }
}