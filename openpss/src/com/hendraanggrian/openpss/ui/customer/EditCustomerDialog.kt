package com.hendraanggrian.openpss.ui.customer

import com.hendraanggrian.openpss.content.Context
import com.hendraanggrian.openpss.R
import com.hendraanggrian.openpss.db.schemas.Customer
import com.hendraanggrian.openpss.popup.dialog.ResultableDialog
import com.hendraanggrian.openpss.util.isName
import com.hendraanggrian.openpss.util.orNull
import javafx.scene.control.TextArea
import javafx.scene.control.TextField
import ktfx.jfoenix.jfxTextArea
import ktfx.jfoenix.jfxTextField
import ktfx.layouts.gridPane
import ktfx.layouts.label
import ktfx.scene.layout.gap

class EditCustomerDialog(
    context: Context,
    private val customer: Customer
) : ResultableDialog<Customer>(context, R.string.edit_customer) {

    private lateinit var nameField: TextField
    private lateinit var addressField: TextField
    private lateinit var noteArea: TextArea

    init {
        gridPane {
            gap = R.dimen.padding_medium.toDouble()
            label(getString(R.string.name)) col 0 row 0
            nameField = jfxTextField(customer.name) col 1 row 0
            label(getString(R.string.address)) col 0 row 1
            addressField = jfxTextField(customer.address.orEmpty()) col 1 row 1
            label(getString(R.string.note)) col 0 row 2
            noteArea = jfxTextArea(customer.note.orEmpty()) col 1 row 2
        }
        defaultButton.run {
            text = getString(R.string.edit)
            disableProperty().bind(!nameField.textProperty().isName())
        }
    }

    override val nullableResult: Customer?
        get() = customer.apply {
            name = nameField.text
            address = addressField.text.orNull()
            note = noteArea.text.orNull()
        }
}