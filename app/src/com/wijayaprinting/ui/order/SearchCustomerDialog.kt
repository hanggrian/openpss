package com.wijayaprinting.ui.order

import com.wijayaprinting.R
import com.wijayaprinting.db.dao.Customer
import com.wijayaprinting.db.schema.Customers
import com.wijayaprinting.db.transaction
import com.wijayaprinting.ui.Resourced
import javafx.scene.control.ButtonType.CANCEL
import javafx.scene.control.ButtonType.OK
import javafx.scene.control.Dialog
import javafx.scene.control.ListView
import javafx.scene.control.TextField
import javafx.scene.image.ImageView
import kotfx.bindings.bindingOf
import kotfx.collections.toMutableObservableList
import kotfx.dialogs.button
import kotfx.dialogs.content
import kotfx.runLater
import kotfx.scene.listView
import kotfx.scene.textField
import kotfx.scene.vbox
import kotlinx.nosql.equal

class SearchCustomerDialog(resourced: Resourced) : Dialog<Customer>(), Resourced by resourced {

    companion object {
        private const val ITEMS_PER_PAGE = 15
    }

    private lateinit var textField: TextField
    private lateinit var listView: ListView<Customer>

    init {
        title = getString(R.string.search_customer)
        headerText = getString(R.string.search_customer)
        graphic = ImageView(R.image.ic_user)
        content = vbox {
            textField = textField { promptText = getString(R.string.customer) }
            listView = listView<Customer> {
                itemsProperty().bind(bindingOf(textField.textProperty()) {
                    transaction {
                        when {
                            textField.text.isEmpty() -> Customers.find()
                            else -> Customers.find { name.equal(textField.text) }
                        }.take(ITEMS_PER_PAGE).toMutableObservableList()
                    }
                })
            } marginTop 8
        }
        button(CANCEL)
        button(OK).disableProperty().bind(listView.selectionModel.selectedItemProperty().isNull)
        runLater { textField.requestFocus() }
        setResultConverter {
            if (it == OK) listView.selectionModel.selectedItem
            else null
        }
    }
}