package com.hendraanggrian.openpss.ui.customer

import com.hendraanggrian.openpss.R
import com.hendraanggrian.openpss.db.schemas.Customer
import com.hendraanggrian.openpss.db.schemas.Customers
import com.hendraanggrian.openpss.ui.Resourced
import com.hendraanggrian.openpss.utils.REGEX_PHONE
import com.hendraanggrian.openpss.utils.style
import javafx.scene.control.ButtonType.OK
import javafx.scene.control.ChoiceBox
import javafx.scene.control.Dialog
import javafx.scene.control.TextField
import javafx.scene.image.ImageView
import ktfx.beans.binding.booleanBindingOf
import ktfx.layouts.choiceBox
import ktfx.layouts.gridPane
import ktfx.layouts.label
import ktfx.layouts.textField
import ktfx.scene.control.cancelButton
import ktfx.scene.control.graphicIcon
import ktfx.scene.control.headerTitle
import ktfx.scene.control.okButton
import ktfx.scene.layout.gap
import org.apache.commons.validator.routines.EmailValidator

class AddContactDialog(resourced: Resourced) : Dialog<Customer.Contact>(), Resourced by resourced {

    private lateinit var typeChoice: ChoiceBox<Customer.Contact.Type>
    private lateinit var contactField: TextField

    init {
        style()
        headerTitle = getString(R.string.add_contact)
        graphicIcon = ImageView(R.image.ic_contact)
        dialogPane.content = gridPane {
            gap = 8.0
            label(getString(R.string.type)) col 0 row 0
            typeChoice = choiceBox(Customer.Contact.Type.listAll(this@AddContactDialog)) col 1 row 0
            label(getString(R.string.contact)) col 0 row 1
            contactField = textField { promptText = getString(R.string.contact) } col 1 row 1
        }
        cancelButton()
        okButton().disableProperty().bind(booleanBindingOf(typeChoice.valueProperty(), contactField.textProperty()) {
            when (typeChoice.value) {
                null -> true
                is Customer.Contact.Type.Phone -> contactField.text.isBlank() || !contactField.text.matches(REGEX_PHONE)
                else -> contactField.text.isBlank() || !EmailValidator.getInstance().isValid(contactField.text)
            }
        })
        setResultConverter {
            when (it) {
                OK -> Customers.Contacts.new(typeChoice.value, contactField.text)
                else -> null
            }
        }
    }
}