package com.hendraanggrian.openpss.ui.customer

import com.hendraanggrian.openpss.FxComponent
import com.hendraanggrian.openpss.R
import com.hendraanggrian.openpss.R2
import com.hendraanggrian.openpss.schema.Customer
import com.hendraanggrian.openpss.ui.ResultableDialog
import com.hendraanggrian.openpss.util.orNull
import com.hendraanggrian.openpss.util.personNameBinding
import javafx.scene.Node
import javafx.scene.control.Label
import javafx.scene.control.TextArea
import javafx.scene.control.TextField
import javafx.scene.control.TextInputControl
import javafx.scene.image.ImageView
import ktfx.bindings.or
import ktfx.booleanProperty
import ktfx.coroutines.listener
import ktfx.jfoenix.layouts.jfxTextArea
import ktfx.jfoenix.layouts.jfxTextField
import ktfx.layouts.gap
import ktfx.layouts.gridPane
import ktfx.layouts.imageView
import ktfx.layouts.label

class EditCustomerDialog(
    component: FxComponent,
    private val customer: Customer
) : ResultableDialog<Customer>(component, R2.string.edit_customer) {

    private val unchangedProperty = booleanProperty(true)

    private val image: ImageView
    private val description: Label
    private val nameField: TextField
    private val addressField: TextField
    private val noteArea: TextArea

    override val focusedNode: Node? get() = nameField

    init {
        gridPane {
            gap = getDouble(R.value.padding_medium)
            image = imageView(
                when {
                    customer.isCompany -> R.image.display_company
                    else -> R.image.display_person
                }
            ) {
                gridAt(0, 0, colSpans = 2)
            }
            description = label(
                getString(
                    when {
                        customer.isCompany -> R2.string._company_requirement
                        else -> R2.string._person_requirement
                    }
                )
            ) {
                gridAt(0, 1, colSpans = 2)
            }
            label(getString(R2.string.name)) {
                gridAt(2, 0)
            }
            nameField = jfxTextField(customer.name) {
                gridAt(2, 1)
                bindTextField()
            }
            label(getString(R2.string.address)) {
                gridAt(3, 0)
            }
            addressField = jfxTextField(customer.address.orEmpty()) {
                gridAt(3, 1)
                bindTextField()
            }
            label(getString(R2.string.note)) {
                gridAt(4, 0)
            }
            noteArea = jfxTextArea(customer.note.orEmpty()) {
                gridAt(4, 1)
                bindTextField()
            }
        }
        defaultButton.run {
            text = getString(R2.string.edit)
            disableProperty().bind(
                when {
                    customer.isCompany -> unchangedProperty
                    else -> unchangedProperty or !nameField.textProperty().personNameBinding
                }
            )
        }
    }

    override val nullableResult: Customer?
        get() = Customer(
            nameField.text,
            customer.isCompany,
            customer.since,
            addressField.text.orNull(),
            noteArea.text,
            customer.contacts
        )

    private fun TextInputControl.bindTextField() {
        textProperty().listener { unchangedProperty.set(false) }
    }
}
