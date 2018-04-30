package com.hendraanggrian.openpss.ui.customer

import com.hendraanggrian.openpss.R
import com.hendraanggrian.openpss.db.schemas.Customer
import com.hendraanggrian.openpss.resources.Resourced
import com.hendraanggrian.openpss.util.getStyle
import javafx.scene.control.ButtonType.OK
import javafx.scene.control.ChoiceBox
import javafx.scene.control.Dialog
import javafx.scene.control.TextField
import javafx.scene.image.ImageView
import ktfx.beans.binding.booleanBindingOf
import ktfx.collections.toObservableList
import ktfx.layouts.choiceBox
import ktfx.layouts.gridPane
import ktfx.layouts.label
import ktfx.layouts.textField
import ktfx.listeners.converter
import ktfx.scene.control.cancelButton
import ktfx.scene.control.graphicIcon
import ktfx.scene.control.headerTitle
import ktfx.scene.control.okButton
import ktfx.scene.layout.gap
import org.apache.commons.validator.routines.EmailValidator

class AddContactDialog(resourced: Resourced) : Dialog<Customer.Contact>(), Resourced by resourced {
    companion object {
        /** Taken from `android.util.Patterns`, but instead use `kotlin.Regex`. */
        private val REGEX_PHONE = Regex("(\\+[0-9]+[\\- \\.]*)?" +
            "(\\([0-9]+\\)[\\- \\.]*)?" +
            "([0-9][0-9\\- \\.]+[0-9])")
    }

    private lateinit var typeChoice: ChoiceBox<Customer.Contact.Type>
    private lateinit var contactField: TextField

    init {
        headerTitle = getString(R.string.add_contact)
        graphicIcon = ImageView(R.image.header_contact)
        dialogPane.run {
            stylesheets += getStyle(R.style.openpss)
            content = gridPane {
                gap = 8.0
                label(getString(R.string.type)) col 0 row 0
                typeChoice = choiceBox(Customer.Contact.Type.values().toObservableList()) {
                    converter { toString { it!!.toString(this@AddContactDialog) } }
                } col 1 row 0
                label(getString(R.string.contact)) col 0 row 1
                contactField = textField { promptText = getString(R.string.contact) } col 1 row 1
            }
        }
        cancelButton()
        okButton().disableProperty().bind(booleanBindingOf(typeChoice.valueProperty(), contactField.textProperty()) {
            when (typeChoice.value) {
                null -> true
                Customer.Contact.Type.PHONE -> contactField.text.isBlank() || !contactField.text.matches(REGEX_PHONE)
                else -> contactField.text.isBlank() || !EmailValidator.getInstance().isValid(contactField.text)
            }
        })
        setResultConverter {
            when (it) {
                OK -> Customer.Contact.new(typeChoice.value, contactField.text)
                else -> null
            }
        }
    }
}