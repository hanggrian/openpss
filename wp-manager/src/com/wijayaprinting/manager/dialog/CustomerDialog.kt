package com.wijayaprinting.manager.dialog

import com.wijayaprinting.dao.Customer
import com.wijayaprinting.manager.R
import com.wijayaprinting.manager.Resourced
import com.wijayaprinting.manager.scene.utils.gap
import com.wijayaprinting.manager.utils.safeTransaction
import javafx.scene.control.ButtonType.CANCEL
import javafx.scene.control.ButtonType.OK
import javafx.scene.control.Dialog
import javafx.scene.control.TextArea
import javafx.scene.control.TextField
import javafx.scene.image.ImageView
import kotfx.*
import org.joda.time.DateTime.now

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
            nameField = textField { promptText = getString(R.string.name) } col 1 row 0
            label(getString(R.string.email)) col 0 row 1
            emailField = textField { promptText = getString(R.string.email) } col 1 row 1
            label("${getString(R.string.phone)} 1") col 0 row 2
            phone1Field = textField { promptText = getString(R.string.phone) } col 1 row 2
            label("${getString(R.string.phone)} 2") col 0 row 3
            phone2Field = textField { promptText = getString(R.string.phone) } col 1 row 3
            label(getString(R.string.note)) col 0 row 4
            noteArea = textArea { promptText = getString(R.string.note) } col 1 row 4
        }
        button(CANCEL)
        button(OK).disableProperty() bind nameField.textProperty().isEmpty
        runFX { nameField.requestFocus() }
        setResultConverter {
            when {
                it == CANCEL -> null
                isAdd -> safeTransaction {
                    Customer.new {
                        since = now()
                        name = nameField.text
                        email = emailField.text
                        phone1 = phone1Field.text
                        phone2 = phone2Field.text
                        note = noteArea.text
                    }
                }
                else -> null
            }
        }
    }

    val isAdd: Boolean get() = prefill == null
}