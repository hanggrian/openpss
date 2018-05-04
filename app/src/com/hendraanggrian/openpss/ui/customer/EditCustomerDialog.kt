package com.hendraanggrian.openpss.ui.customer

import com.hendraanggrian.openpss.R
import com.hendraanggrian.openpss.db.schemas.Customer
import com.hendraanggrian.openpss.resources.Resourced
import com.hendraanggrian.openpss.util.getStyle
import com.hendraanggrian.openpss.util.isName
import com.hendraanggrian.openpss.util.orNull
import javafx.scene.control.ButtonType.CANCEL
import javafx.scene.control.Dialog
import javafx.scene.control.TextArea
import javafx.scene.control.TextField
import javafx.scene.image.ImageView
import ktfx.layouts.gridPane
import ktfx.layouts.label
import ktfx.layouts.textArea
import ktfx.layouts.textField
import ktfx.scene.control.cancelButton
import ktfx.scene.control.graphicIcon
import ktfx.scene.control.headerTitle
import ktfx.scene.control.okButton
import ktfx.scene.layout.gap

class EditCustomerDialog(resourced: Resourced, customer: Customer) : Dialog<Customer>(), Resourced by resourced {

    private lateinit var nameField: TextField
    private lateinit var addressField: TextField
    private lateinit var noteArea: TextArea

    init {
        headerTitle = getString(R.string.edit_customer)
        graphicIcon = ImageView(R.image.header_customer)
        dialogPane.run {
            stylesheets += getStyle(R.style.openpss)
            content = gridPane {
                gap = 8.0
                label(getString(R.string.name)) col 0 row 0
                nameField = textField(customer.name) col 1 row 0
                label(getString(R.string.address)) col 0 row 1
                addressField = textField(customer.address.orEmpty()) col 1 row 1
                label(getString(R.string.note)) col 0 row 2
                noteArea = textArea(customer.note.orEmpty()) col 1 row 2
            }
        }
        cancelButton()
        okButton().disableProperty().bind(!nameField.textProperty().isName())
        setResultConverter {
            when (it) {
                CANCEL -> null
                else -> customer.apply {
                    name = nameField.text
                    address = addressField.text.orNull()
                    note = noteArea.text.orNull()
                }
            }
        }
    }
}