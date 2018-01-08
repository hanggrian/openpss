package com.wijayaprinting.dialog

import com.wijayaprinting.R
import com.wijayaprinting.Resourced
import com.wijayaprinting.dao.Customer
import com.wijayaprinting.dao.Customers
import com.wijayaprinting.utils.expose
import javafx.scene.control.ButtonType.CANCEL
import javafx.scene.control.ButtonType.OK
import javafx.scene.control.Dialog
import javafx.scene.control.ListView
import javafx.scene.control.TextField
import javafx.scene.image.ImageView
import kotfx.*
import org.jetbrains.exposed.sql.or

class SearchCustomerDialog(val resourced: Resourced) : Dialog<Customer>(), Resourced by resourced {

    companion object {
        private const val ITEMS_PER_PAGE = 15
    }

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
                    expose {
                        when {
                            textField.text.isEmpty() -> Customer.all()
                            else -> Customer.find { Customers.id eq textField.text.toIntOrNull() or (Customers.name regexp textField.text) }
                        }.limit(ITEMS_PER_PAGE).toMutableObservableList()
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