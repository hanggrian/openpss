package com.hendraanggrian.openpss.ui.customer

import com.hendraanggrian.openpss.R
import com.hendraanggrian.openpss.db.schema.Contact
import com.hendraanggrian.openpss.ui.Resourced
import javafx.scene.control.ButtonType.OK
import javafx.scene.control.ChoiceBox
import javafx.scene.control.Dialog
import javafx.scene.control.TextField
import javafx.scene.image.ImageView
import ktfx.beans.binding.or
import ktfx.layouts.choiceBox
import ktfx.layouts.gridPane
import ktfx.layouts.label
import ktfx.layouts.textField
import ktfx.scene.control.cancelButton
import ktfx.scene.control.graphicIcon
import ktfx.scene.control.headerTitle
import ktfx.scene.control.okButton
import ktfx.scene.layout.gap

class AddContactDialog(resourced: Resourced) : Dialog<Contact>(), Resourced by resourced {

    private lateinit var typeChoice: ChoiceBox<String>
    private lateinit var contactField: TextField

    init {
        headerTitle = getString(R.string.add_contact)
        graphicIcon = ImageView(R.image.ic_contact)
        dialogPane.content = gridPane {
            gap = 8.0
            label(getString(R.string.type)) col 0 row 0
            typeChoice = choiceBox(Contact.listTypes()) col 1 row 0
            label(getString(R.string.contact)) col 0 row 1
            contactField = textField { promptText = getString(R.string.contact) } col 1 row 1
        }
        cancelButton()
        okButton {
            disableProperty().bind(typeChoice.valueProperty().isNull or
                contactField.textProperty().isEmpty)
        }
        setResultConverter { if (it == OK) Contact(typeChoice.value, contactField.text) else null }
    }
}