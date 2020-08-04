package com.hendraanggrian.openpss.ui.customer

import com.hendraanggrian.openpss.R
import com.hendraanggrian.openpss.content.Context
import com.hendraanggrian.openpss.db.schemas.Customer
import com.hendraanggrian.openpss.popup.dialog.ResultableDialog
import com.hendraanggrian.openpss.util.isPersonName
import com.hendraanggrian.openpss.util.orNull
import javafx.scene.Node
import javafx.scene.control.Label
import javafx.scene.control.TextArea
import javafx.scene.control.TextField
import javafx.scene.control.TextInputControl
import javafx.scene.image.ImageView
import ktfx.beans.property.asProperty
import ktfx.beans.value.or
import ktfx.coroutines.listener
import ktfx.jfoenix.jfxTextArea
import ktfx.jfoenix.jfxTextField
import ktfx.layouts.gridPane
import ktfx.layouts.imageView
import ktfx.layouts.label
import ktfx.scene.layout.gap

class EditCustomerDialog(
    context: Context,
    private val customer: Customer
) : ResultableDialog<Customer>(context, R.string.edit_customer) {

    private val unchangedProperty = true.asProperty()

    private lateinit var image: ImageView
    private lateinit var description: Label
    private lateinit var nameField: TextField
    private lateinit var addressField: TextField
    private lateinit var noteArea: TextArea

    override val focusedNode: Node? get() = nameField

    init {
        gridPane {
            gap = getDouble(R.dimen.padding_medium)
            image = imageView(
                when {
                    customer.isCompany -> R.image.display_company
                    else -> R.image.display_person
                }
            ) col 0 row 0 colSpans 2
            description = label(
                getString(
                    when {
                        customer.isCompany -> R.string._company_requirement
                        else -> R.string._person_requirement
                    }
                )
            ) col 0 row 1 colSpans 2
            label(getString(R.string.name)) col 0 row 2
            nameField = jfxTextField(customer.name) { bindTextField() } col 1 row 2
            label(getString(R.string.address)) col 0 row 3
            addressField = jfxTextField(customer.address.orEmpty()) { bindTextField() } col 1 row 3
            label(getString(R.string.note)) col 0 row 4
            noteArea = jfxTextArea(customer.note.orEmpty()) { bindTextField() } col 1 row 4
        }
        defaultButton.run {
            text = getString(R.string.edit)
            disableProperty().bind(
                when {
                    customer.isCompany -> unchangedProperty
                    else -> unchangedProperty or !nameField.textProperty().isPersonName()
                }
            )
        }
    }

    override val nullableResult: Customer?
        get() = Customer(
            customer.no,
            nameField.text,
            customer.isCompany,
            customer.since,
            addressField.text.orNull(),
            customer.note,
            customer.contacts
        )

    private fun TextInputControl.bindTextField() {
        textProperty().listener { unchangedProperty.set(false) }
    }
}
