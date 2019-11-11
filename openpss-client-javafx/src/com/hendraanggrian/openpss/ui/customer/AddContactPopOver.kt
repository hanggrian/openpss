package com.hendraanggrian.openpss.ui.customer

import com.hendraanggrian.openpss.FxComponent
import com.hendraanggrian.openpss.R
import com.hendraanggrian.openpss.R2
import com.hendraanggrian.openpss.schema.ContactType
import com.hendraanggrian.openpss.schema.Customer
import com.hendraanggrian.openpss.schema.new
import com.hendraanggrian.openpss.ui.ResultablePopOver
import javafx.scene.Node
import javafx.scene.control.ComboBox
import javafx.scene.control.TextField
import ktfx.bindings.booleanBindingOf
import ktfx.buildStringConverter
import ktfx.collections.toObservableList
import ktfx.jfoenix.layouts.jfxComboBox
import ktfx.jfoenix.layouts.jfxTextField
import ktfx.layouts.gap
import ktfx.layouts.gridPane
import ktfx.layouts.label
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

    private val typeChoice: ComboBox<ContactType>
    private val contactField: TextField

    override val focusedNode: Node? get() = typeChoice

    init {
        gridPane {
            gap = getDouble(R.value.padding_medium)
            label(getString(R2.string.type)) {
                gridAt(0, 0)
            }
            typeChoice = jfxComboBox(ContactType.values().toObservableList()) {
                gridAt(0, 1)
                converter = buildStringConverter {
                    toString { it!!.toString(this@AddContactPopOver) }
                }
            }
            label(getString(R2.string.contact)) {
                gridAt(1, 0)
            }
            contactField = jfxTextField {
                gridAt(1, 1)
                promptText = getString(R2.string.contact)
            }
        }
        defaultButton.run {
            text = getString(R2.string.add)
            disableProperty().bind(
                booleanBindingOf(
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
