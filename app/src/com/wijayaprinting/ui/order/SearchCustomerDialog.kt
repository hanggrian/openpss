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
import kotfx.coroutines.resultConverter
import kotfx.dialogs.addButton
import kotfx.dialogs.content
import kotfx.dialogs.graphicIcon
import kotfx.dialogs.headerTitle
import kotfx.layout.listView
import kotfx.layout.textField
import kotfx.layout.vbox
import kotfx.runLater
import kotlinx.nosql.equal

class SearchCustomerDialog(resourced: Resourced) : Dialog<Customer>(), Resourced by resourced {

    companion object {
        private const val ITEMS_PER_PAGE = 15
    }

    private lateinit var textField: TextField
    private lateinit var listView: ListView<Customer>

    init {
        headerTitle = getString(R.string.search_customer)
        graphicIcon = ImageView(R.image.ic_user)
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
        addButton(CANCEL)
        addButton(OK).disableProperty().bind(listView.selectionModel.selectedItemProperty().isNull)
        runLater { textField.requestFocus() }
        resultConverter {
            if (it == OK) listView.selectionModel.selectedItem
            else null
        }
    }
}