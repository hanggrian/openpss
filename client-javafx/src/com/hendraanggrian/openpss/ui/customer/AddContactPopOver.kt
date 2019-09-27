package com.hendraanggrian.openpss.ui.customer

import com.hendraanggrian.openpss.FxComponent
import com.hendraanggrian.openpss.R
import com.hendraanggrian.openpss.R2
import com.hendraanggrian.openpss.data.Customer
import com.hendraanggrian.openpss.schema.ContactType
import com.hendraanggrian.openpss.schema.new
import com.hendraanggrian.openpss.ui.ResultablePopOver
import javafx.scene.Node
import javafx.scene.control.ComboBox
import javafx.scene.control.TextField
import ktfx.bindings.buildBooleanBinding
import ktfx.collections.toObservableList
import ktfx.controls.gap
import ktfx.jfoenix.jfxComboBox
import ktfx.jfoenix.jfxTextField
import ktfx.layouts.gridPane
import ktfx.layouts.label
import ktfx.listeners.converter
import org.apache.commons.validator.routines.EmailValidator

class AddContactPopOver(component: FxComponent) :
    ResultablePopOver<Customer.Contact>(component, R2.string.add_contact) {

    private companion object {

        /** Taken from `android.util.Patterns`, but instead use `kotlin.Regex`. */
        val REGEX_PHONE = Regex(
            "(\\+[0-9]+[\\- \\.]*)?" +
                "(\\([0-9]+\\)[\\- \\.]*)?" +
                "([0-9][0-9\\- \\.]+[0-9])"
        )
    }

    private lateinit var typeChoice: ComboBox<ContactType>
    private lateinit var contactField: TextField

    override val focusedNode: Node? get() = typeChoice

    init {
        gridPane {
            gap = getDouble(R.value.padding_medium)
            label(getString(R2.string.type)) col 0 row 0
            typeChoice = jfxComboBox(ContactType.values().toObservableList()) {
                converter { toString { it!!.toString(this@AddContactPopOver) } }
            } col 1 row 0
            label(getString(R2.string.contact)) col 0 row 1
            contactField = jfxTextField { promptText = getString(R2.string.contact) } col 1 row 1
        }
        defaultButton.run {
            text = getString(R2.string.add)
            disableProperty().bind(
                buildBooleanBinding(
                    typeChoice.valueProperty(),
                    contactField.textProperty()
                ) {
                    when (typeChoice.value) {
                        null -> true
                        ContactType.PHONE -> contactField.text.isBlank() || !contactField.text.matches(
                            REGEX_PHONE
                        )
                        else -> contactField.text.isBlank() || !EmailValidator.getInstance().isValid(
                            contactField.text
                        )
                    }
                })
        }
    }

    override val nullableResult: Customer.Contact?
        get() = Customer.Contact.new(
            typeChoice.value,
            contactField.text
        )
}
