package com.hendraanggrian.openpss.ui.customer

import com.hendraanggrian.openpss.R
import com.hendraanggrian.openpss.control.DefaultPopOver
import com.hendraanggrian.openpss.db.schemas.Customer
import com.hendraanggrian.openpss.localization.Resourced
import com.hendraanggrian.openpss.util.isName
import com.hendraanggrian.openpss.util.orNull
import javafx.scene.control.TextArea
import javafx.scene.control.TextField
import ktfx.layouts.gridPane
import ktfx.layouts.label
import ktfx.layouts.textArea
import ktfx.layouts.textField
import ktfx.scene.layout.gap

class EditCustomerPopOver(
    resourced: Resourced,
    private val customer: Customer
) : DefaultPopOver<Customer>(resourced, R.string.edit_customer) {

    private lateinit var nameField: TextField
    private lateinit var addressField: TextField
    private lateinit var noteArea: TextArea

    init {
        gridPane {
            gap = 8.0
            label(getString(R.string.name)) col 0 row 0
            nameField = textField(customer.name) col 1 row 0
            label(getString(R.string.address)) col 0 row 1
            addressField = textField(customer.address.orEmpty()) col 1 row 1
            label(getString(R.string.note)) col 0 row 2
            noteArea = textArea(customer.note.orEmpty()) col 1 row 2
        }
        defaultButton.run {
            text = getString(R.string.edit)
            disableProperty().bind(!nameField.textProperty().isName())
        }
    }

    override fun getResult(): Customer = customer.apply {
        name = nameField.text
        address = addressField.text.orNull()
        note = noteArea.text.orNull()
    }
}