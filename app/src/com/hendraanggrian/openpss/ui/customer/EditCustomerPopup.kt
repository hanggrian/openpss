package com.hendraanggrian.openpss.ui.customer

import com.hendraanggrian.openpss.R
import com.hendraanggrian.openpss.controls.Popup
import com.hendraanggrian.openpss.db.schemas.Customer
import com.hendraanggrian.openpss.resources.Resourced
import com.hendraanggrian.openpss.util.isName
import com.hendraanggrian.openpss.util.orNull
import javafx.scene.Node
import javafx.scene.control.TextArea
import javafx.scene.control.TextField
import ktfx.layouts.LayoutManager
import ktfx.layouts.button
import ktfx.layouts.gridPane
import ktfx.layouts.label
import ktfx.layouts.textArea
import ktfx.layouts.textField
import ktfx.scene.layout.gap

class EditCustomerPopup(
    resourced: Resourced,
    private val customer: Customer
) : Popup<Customer>(resourced, R.string.edit_customer) {

    private lateinit var nameField: TextField
    private lateinit var addressField: TextField
    private lateinit var noteArea: TextArea

    override val content: Node = gridPane {
        gap = 8.0
        label(getString(R.string.name)) col 0 row 0
        nameField = textField(customer.name) col 1 row 0
        label(getString(R.string.address)) col 0 row 1
        addressField = textField(customer.address.orEmpty()) col 1 row 1
        label(getString(R.string.note)) col 0 row 2
        noteArea = textArea(customer.note.orEmpty()) col 1 row 2
    }

    override fun LayoutManager<Node>.buttons() {
        button(getString(R.string.edit)) {
            isDefaultButton = true
            disableProperty().bind(!nameField.textProperty().isName())
        }
    }

    override fun getResult(): Customer = customer.apply {
        name = nameField.text
        address = addressField.text.orNull()
        note = noteArea.text.orNull()
    }
}