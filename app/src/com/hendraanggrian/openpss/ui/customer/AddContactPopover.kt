package com.hendraanggrian.openpss.ui.customer

import com.hendraanggrian.openpss.R
import com.hendraanggrian.openpss.control.DefaultPopover
import com.hendraanggrian.openpss.db.schemas.Customer
import com.hendraanggrian.openpss.db.schemas.Customer.Contact.Type.PHONE
import com.hendraanggrian.openpss.db.schemas.Customer.Contact.Type.values
import com.hendraanggrian.openpss.localization.Resourced
import javafx.scene.control.ChoiceBox
import javafx.scene.control.TextField
import ktfx.beans.binding.booleanBindingOf
import ktfx.collections.toObservableList
import ktfx.layouts.choiceBox
import ktfx.layouts.gridPane
import ktfx.layouts.label
import ktfx.layouts.textField
import ktfx.listeners.converter
import ktfx.scene.layout.gap
import org.apache.commons.validator.routines.EmailValidator

class AddContactPopover(resourced: Resourced) : DefaultPopover<Customer.Contact>(resourced, R.string.add_contact) {

    companion object {
        /** Taken from `android.util.Patterns`, but instead use `kotlin.Regex`. */
        private val REGEX_PHONE = Regex("(\\+[0-9]+[\\- \\.]*)?" +
            "(\\([0-9]+\\)[\\- \\.]*)?" +
            "([0-9][0-9\\- \\.]+[0-9])")
    }

    private lateinit var typeChoice: ChoiceBox<Customer.Contact.Type>
    private lateinit var contactField: TextField

    init {
        gridPane {
            gap = 8.0
            label(getString(R.string.type)) col 0 row 0
            typeChoice = choiceBox(values().toObservableList()) {
                converter { toString { it!!.toString(this@AddContactPopover) } }
            } col 1 row 0
            label(getString(R.string.contact)) col 0 row 1
            contactField = textField { promptText = getString(R.string.contact) } col 1 row 1
        }
        defaultButton.run {
            text = getString(R.string.add)
            disableProperty().bind(booleanBindingOf(typeChoice.valueProperty(), contactField.textProperty()) {
                when (typeChoice.value) {
                    null -> true
                    PHONE -> contactField.text.isBlank() || !contactField.text.matches(REGEX_PHONE)
                    else -> contactField.text.isBlank() || !EmailValidator.getInstance().isValid(contactField.text)
                }
            })
        }
    }

    override fun getResult(): Customer.Contact = Customer.Contact.new(typeChoice.value, contactField.text)
}