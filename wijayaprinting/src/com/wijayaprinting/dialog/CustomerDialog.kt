package com.wijayaprinting.dialog

import com.wijayaprinting.R
import com.wijayaprinting.Resourced
import com.wijayaprinting.nosql.Customer
import com.wijayaprinting.utils.gap
import javafx.scene.control.ButtonType.CANCEL
import javafx.scene.control.ButtonType.OK
import javafx.scene.control.Dialog
import javafx.scene.control.TextArea
import javafx.scene.control.TextField
import javafx.scene.image.ImageView
import kotfx.*

class CustomerDialog @JvmOverloads constructor(val resourced: Resourced, var prefill: Customer? = null) : Dialog<Customer>(), Resourced by resourced {

    private lateinit var nameField: TextField
    private lateinit var emailField: TextField
    private lateinit var phone1Field: TextField
    private lateinit var phone2Field: TextField
    private lateinit var noteArea: TextArea

    init {
        title = getString(if (isAdd) R.string.add_customer else R.string.edit_customer)
        headerText = getString(if (isAdd) R.string.add_customer else R.string.edit_customer)
        graphic = ImageView(R.png.ic_user)
        content = gridPane {
            gap(8)
            label(getString(R.string.name)) col 0 row 0
            nameField = textField(prefill?.name ?: "") { promptText = getString(R.string.name) } col 1 row 0
            label(getString(R.string.note)) col 0 row 1
            noteArea = textArea(prefill?.note ?: "") { promptText = getString(R.string.note) } col 1 row 1
        }
        button(CANCEL)
        button(OK).disableProperty() bind nameField.textProperty().isEmpty
        runFX { nameField.requestFocus() }
        setResultConverter {
            null
            /*when {
                it == CANCEL -> null
                isAdd -> expose {
                    Customer.new {
                        since = now()
                        name = nameField.text
                        email = emailField.text
                        phone1 = phone1Field.text
                        phone2 = phone2Field.text
                        note = noteArea.text
                    }
                }
                else -> expose {
                    prefill!!.apply {
                        name = nameField.text
                        email = emailField.text
                        phone1 = phone1Field.text
                        phone2 = phone2Field.text
                        note = noteArea.text
                    }
                }
            }*/
        }
    }

    private val isAdd: Boolean get() = prefill == null
}