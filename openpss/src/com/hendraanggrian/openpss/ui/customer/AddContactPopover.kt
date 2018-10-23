package com.hendraanggrian.openpss.ui.customer

import com.hendraanggrian.openpss.R
import com.hendraanggrian.openpss.db.schemas.Customer
import com.hendraanggrian.openpss.db.schemas.Customer.Contact.Type.PHONE
import com.hendraanggrian.openpss.db.schemas.Customer.Contact.Type.values
import com.hendraanggrian.openpss.i18n.Resourced
import com.hendraanggrian.openpss.popup.popover.ResultablePopover
import javafx.scene.control.ComboBox
import javafx.scene.control.TextField
import ktfx.beans.binding.booleanBindingOf
import ktfx.collections.toObservableList
import ktfx.jfoenix.jfxComboBox
import ktfx.layouts.gridPane
import ktfx.layouts.label
import ktfx.layouts.textField
import ktfx.listeners.converter
import ktfx.scene.layout.gap
import org.apache.commons.validator.routines.EmailValidator

class AddContactPopover(resourced: Resourced) : ResultablePopover<Customer.Contact>(resourced, R.string.add_contact) {

    private companion object {
        /** Taken from `android.util.Patterns`, but instead use `kotlin.Regex`. */
        val REGEX_PHONE = Regex(
            "(\\+[0-9]+[\\- \\.]*)?" +
                "(\\([0-9]+\\)[\\- \\.]*)?" +
                "([0-9][0-9\\- \\.]+[0-9])"
        )
    }

    private lateinit var typeChoice: ComboBox<Customer.Contact.Type>
    private lateinit var contactField: TextField

    init {
        gridPane {
            gap = R.dimen.padding_medium.toDouble()
            label(getString(R.string.type)) col 0 row 0
            typeChoice = jfxComboBox(values().toObservableList()) {
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

    override val nullableResult: Customer.Contact? get() = Customer.Contact.new(typeChoice.value, contactField.text)
}