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
import ktfx.booleanProperty
import ktfx.controls.gap
import ktfx.coroutines.listener
import ktfx.jfoenix.layouts.jfxTextArea
import ktfx.jfoenix.layouts.jfxTextField
import ktfx.layouts.gridPane
import ktfx.layouts.imageView
import ktfx.layouts.label
import ktfx.or

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
            ) col (0 to 2) row 0
            description = label(
                getString(
                    when {
                        customer.isCompany -> R2.string._company_requirement
                        else -> R2.string._person_requirement
                    }
                )
            ) col (0 to 2) row 1
            label(getString(R2.string.name)) col 0 row 2
            nameField = jfxTextField(customer.name) { bindTextField() } col 1 row 2
            label(getString(R2.string.address)) col 0 row 3
            addressField = jfxTextField(customer.address.orEmpty()) { bindTextField() } col 1 row 3
            label(getString(R2.string.note)) col 0 row 4
            noteArea = jfxTextArea(customer.note.orEmpty()) { bindTextField() } col 1 row 4
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
