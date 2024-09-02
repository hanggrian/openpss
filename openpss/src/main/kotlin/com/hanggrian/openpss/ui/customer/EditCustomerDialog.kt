package com.hanggrian.openpss.ui.customer

import com.hanggrian.openpss.Context
import com.hanggrian.openpss.R
import com.hanggrian.openpss.db.schemas.Customer
import com.hanggrian.openpss.popup.dialog.ResultableDialog
import com.hanggrian.openpss.util.isPersonName
import com.hanggrian.openpss.util.orNull
import javafx.scene.Node
import javafx.scene.control.Label
import javafx.scene.control.TextArea
import javafx.scene.control.TextField
import javafx.scene.control.TextInputControl
import javafx.scene.image.ImageView
import ktfx.bindings.or
import ktfx.booleanPropertyOf
import ktfx.coroutines.listener
import ktfx.jfoenix.layouts.jfxTextArea
import ktfx.jfoenix.layouts.jfxTextField
import ktfx.layouts.gridPane
import ktfx.layouts.imageView
import ktfx.layouts.label

class EditCustomerDialog(context: Context, private val customer: Customer) :
    ResultableDialog<Customer>(context, R.string_edit_customer) {
    private val unchangedProperty = booleanPropertyOf(true)

    private val image: ImageView
    private val description: Label
    private val nameField: TextField
    private val addressField: TextField
    private val noteArea: TextArea

    init {
        gridPane {
            hgap = getDouble(R.dimen_padding_medium)
            vgap = getDouble(R.dimen_padding_medium)
            image =
                imageView(
                    when {
                        customer.isCompany -> R.image_display_company
                        else -> R.image_display_person
                    },
                ).grid(0, 0 to 2)
            description =
                label(
                    getString(
                        when {
                            customer.isCompany -> R.string__company_requirement
                            else -> R.string__person_requirement
                        },
                    ),
                ).grid(1, 0 to 2)
            label(getString(R.string_name))
                .grid(2, 0)
            nameField =
                jfxTextField(customer.name) { bindTextField() }
                    .grid(2, 1)
            label(getString(R.string_address))
                .grid(3, 0)
            addressField =
                jfxTextField(customer.address.orEmpty()) { bindTextField() }
                    .grid(3, 1)
            label(getString(R.string_note))
                .grid(4, 0)
            noteArea =
                jfxTextArea(customer.note.orEmpty()) { bindTextField() }
                    .grid(4, 1)
        }
        defaultButton.run {
            text = getString(R.string_edit)
            disableProperty().bind(
                when {
                    customer.isCompany -> unchangedProperty
                    else -> unchangedProperty or !nameField.textProperty().isPersonName()
                },
            )
        }
    }

    override val focusedNode: Node get() = nameField

    override val nullableResult: Customer
        get() =
            Customer(
                customer.no,
                nameField.text,
                customer.isCompany,
                customer.since,
                addressField.text.orNull(),
                customer.note,
                customer.contacts,
            )

    private fun TextInputControl.bindTextField() {
        textProperty().listener { unchangedProperty.set(false) }
    }
}
