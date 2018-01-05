package com.wijayaprinting.manager.dialog

import com.wijayaprinting.dao.Customer
import com.wijayaprinting.dao.Customers
import com.wijayaprinting.manager.R
import com.wijayaprinting.manager.Resourced
import com.wijayaprinting.manager.utils.safeTransaction
import javafx.scene.control.ButtonType.CANCEL
import javafx.scene.control.ButtonType.OK
import javafx.scene.control.Dialog
import javafx.scene.control.ListView
import javafx.scene.control.TextField
import javafx.scene.image.ImageView
import kotfx.*
import org.jetbrains.exposed.sql.or

class SearchCustomerDialog(val resourced: Resourced) : Dialog<Customer>(), Resourced by resourced {

    private lateinit var textField: TextField
    private lateinit var listView: ListView<Customer>

    init {
        title = getString(R.string.search_customer)
        headerText = getString(R.string.search_customer)
        graphic = ImageView(R.png.ic_user)
        content = vbox {
            textField = textField { promptText = getString(R.string.customer) }
            listView = listView<Customer> {
                itemsProperty() bind bindingOf(textField.textProperty()) {
                    safeTransaction {
                        when {
                            textField.text.isEmpty() -> Customer.all()
                            else -> Customer.find { Customers.id eq textField.text.toIntOrNull() or (Customers.name regexp textField.text) }
                        }.limit(10).toMutableObservableList()
                    }
                }
            } marginTop 8
        }
        button(CANCEL)
        button(OK).disableProperty() bind listView.selectionModel.selectedItemProperty().isNull
        runFX { textField.requestFocus() }
        setResultConverter {
            if (it == OK) listView.selectionModel.selectedItem
            else null
        }
    }
}