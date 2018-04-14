package com.hendraanggrian.openpss.ui.customer

import com.hendraanggrian.openpss.R
import com.hendraanggrian.openpss.ui.Resourced
import javafx.scene.control.ButtonType.CANCEL
import javafx.scene.control.Dialog
import javafx.scene.control.TextArea
import javafx.scene.image.ImageView
import ktfx.beans.value.isBlank
import ktfx.layouts.gridPane
import ktfx.layouts.textArea
import ktfx.scene.control.cancelButton
import ktfx.scene.control.graphicIcon
import ktfx.scene.control.headerTitle
import ktfx.scene.control.okButton

class EditNoteDialog(resourced: Resourced, prefill: String) : Dialog<String>(), Resourced by resourced {

    private lateinit var noteArea: TextArea

    init {
        headerTitle = getString(R.string.edit_note)
        graphicIcon = ImageView(R.image.ic_note)
        dialogPane.content = gridPane {
            noteArea = textArea { text = prefill } marginAll 8.0
        }
        cancelButton()
        okButton {
            disableProperty().bind(noteArea.textProperty().isBlank())
        }
        setResultConverter {
            when (it) {
                CANCEL -> null
                else -> noteArea.text
            }
        }
    }
}