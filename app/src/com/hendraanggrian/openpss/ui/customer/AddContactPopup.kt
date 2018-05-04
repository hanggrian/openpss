package com.hendraanggrian.openpss.ui.customer

import com.hendraanggrian.openpss.R
import com.hendraanggrian.openpss.controls.Popup
import com.hendraanggrian.openpss.db.schemas.Customer
import com.hendraanggrian.openpss.resources.Resourced
import javafx.scene.Node
import javafx.scene.control.Button
import javafx.scene.control.ChoiceBox
import javafx.scene.control.TextField
import ktfx.beans.binding.booleanBindingOf
import ktfx.collections.toObservableList
import ktfx.layouts.button
import ktfx.layouts.choiceBox
import ktfx.layouts.gridPane
import ktfx.layouts.label
import ktfx.layouts.textField
import ktfx.listeners.converter
import ktfx.scene.layout.gap
import org.apache.commons.validator.routines.EmailValidator

class AddContactPopup(resourced: Resourced) : Popup<Customer.Contact>(resourced, R.string.add_contact) {

    companion object {
        /** Taken from `android.util.Patterns`, but instead use `kotlin.Regex`. */
        private val REGEX_PHONE = Regex("(\\+[0-9]+[\\- \\.]*)?" +
            "(\\([0-9]+\\)[\\- \\.]*)?" +
            "([0-9][0-9\\- \\.]+[0-9])")
    }

    private lateinit var typeChoice: ChoiceBox<Customer.Contact.Type>
    private lateinit var contactField: TextField

    override val content: Node = gridPane {
        gap = 8.0
        label(getString(R.string.type)) col 0 row 0
        typeChoice = choiceBox(Customer.Contact.Type.values().toObservableList()) {
            converter { toString { it!!.toString(this@AddContactPopup) } }
        } col 1 row 0
        label(getString(R.string.contact)) col 0 row 1
        contactField = textField { promptText = getString(R.string.contact) } col 1 row 1
    }

    override val buttons: List<Button> = listOf(button(getString(R.string.add)) {
        isDefaultButton = true
        disableProperty().bind(booleanBindingOf(typeChoice.valueProperty(), contactField.textProperty()) {
            when (typeChoice.value) {
                null -> true
                Customer.Contact.Type.PHONE -> contactField.text.isBlank() || !contactField.text.matches(REGEX_PHONE)
                else -> contactField.text.isBlank() || !EmailValidator.getInstance().isValid(contactField.text)
            }
        })
    })

    override fun getResult(): Customer.Contact = Customer.Contact.new(typeChoice.value, contactField.text)
}