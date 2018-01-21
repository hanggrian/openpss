package com.wijayaprinting.dialogs

import com.wijayaprinting.R
import com.wijayaprinting.base.Resourced
import com.wijayaprinting.db.Customer
import com.wijayaprinting.db.Customers
import com.wijayaprinting.db.transaction
import javafx.scene.control.ButtonType.CANCEL
import javafx.scene.control.ButtonType.OK
import javafx.scene.control.Dialog
import javafx.scene.control.ListView
import javafx.scene.control.TextField
import javafx.scene.image.ImageView
import kotfx.*
import kotlinx.nosql.equal

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
                    transaction {
                        when {
                            textField.text.isEmpty() -> Customers.find()
                            else -> Customers.find { name.equal(textField.text) }
                        }.take(ITEMS_PER_PAGE).toMutableObservableList()
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