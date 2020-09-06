package com.hendraanggrian.openpss.ui.customer

import com.hendraanggrian.openpss.R
import com.hendraanggrian.openpss.content.Context
import com.hendraanggrian.openpss.db.schemas.Customer
import com.hendraanggrian.openpss.db.schemas.Customer.Contact.Type.PHONE
import com.hendraanggrian.openpss.db.schemas.Customer.Contact.Type.values
import com.hendraanggrian.openpss.popup.popover.ResultablePopover
import javafx.scene.Node
import javafx.scene.control.ComboBox
import javafx.scene.control.TextField
import ktfx.bindings.booleanBindingOf
import ktfx.collections.toObservableList
import ktfx.jfoenix.layouts.jfxComboBox
import ktfx.jfoenix.layouts.jfxTextField
import ktfx.layouts.gridPane
import ktfx.layouts.label
import ktfx.text.buildStringConverter
import org.apache.commons.validator.routines.EmailValidator

class AddContactPopover(context: Context) : ResultablePopover<Customer.Contact>(context, R.string.add_contact) {
    private companion object {
        /** Taken from `android.util.Patterns`, but instead use `kotlin.Regex`. */
        val REGEX_PHONE = Regex(
            "(\\+[0-9]+[\\- \\.]*)?" +
                "(\\([0-9]+\\)[\\- \\.]*)?" +
                "([0-9][0-9\\- \\.]+[0-9])"
        )
    }

    private var typeChoice: ComboBox<Customer.Contact.Type>
    private var contactField: TextField

    override val focusedNode: Node? get() = typeChoice

    init {
        gridPane {
            hgap = getDouble(R.dimen.padding_medium)
            vgap = getDouble(R.dimen.padding_medium)
            label(getString(R.string.type)).grid(0, 0)
            typeChoice = jfxComboBox(values().toObservableList()) {
                converter = buildStringConverter { toString { it!!.toString(this@AddContactPopover) } }
            }.grid(0, 1)
            label(getString(R.string.contact)).grid(1, 0)
            contactField = jfxTextField { promptText = getString(R.string.contact) }.grid(1, 1)
        }
        defaultButton.run {
            text = getString(R.string.add)
            disableProperty().bind(
                booleanBindingOf(typeChoice.valueProperty(), contactField.textProperty()) {
                    when (typeChoice.value) {
                        null -> true
                        PHONE -> contactField.text.isBlank() || !contactField.text.matches(REGEX_PHONE)
                        else -> contactField.text.isBlank() || !EmailValidator.getInstance().isValid(contactField.text)
                    }
                }
            )
        }
    }

    override val nullableResult: Customer.Contact? get() = Customer.Contact.new(typeChoice.value, contactField.text)
}
