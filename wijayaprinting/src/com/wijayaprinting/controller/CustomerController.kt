package com.wijayaprinting.controller

import com.wijayaprinting.dialog.CustomerDialog
import com.wijayaprinting.nosql.Customer
import com.wijayaprinting.nosql.Customers
import com.wijayaprinting.utils.transaction
import javafx.fxml.FXML
import javafx.scene.Node
import javafx.scene.control.*
import javafx.scene.text.Font.loadFont
import javafx.util.Callback
import kotfx.*
import kotlinx.nosql.equal

class CustomerController : Controller() {

    companion object {
        private const val ITEMS_PER_PAGE = 20
    }

    @FXML lateinit var editButton: Button

    @FXML lateinit var customerField: TextField
    @FXML lateinit var customerPagination: Pagination
    @FXML lateinit var emailLabel: Label
    @FXML lateinit var phone1Label: Label
    @FXML lateinit var phone2Label: Label
    @FXML lateinit var noteLabel: Label

    private lateinit var listView: ListView<Customer>

    @FXML
    fun initialize() {
        emailLabel.font = loadFont(latoBold, 13.0)
        phone1Label.font = loadFont(latoBold, 13.0)
        phone2Label.font = loadFont(latoBold, 13.0)
        noteLabel.font = loadFont(latoBold, 13.0)

        /*customerPagination.pageFactoryProperty() bind bindingOf(customerField.textProperty()) {
            Callback<Int, Node> { page ->
                listView = listView {
                    runFX {
                        transaction {
                            val customers = Customers.find { name.equal(customerField.text) }
                            items = customers.skip(ITEMS_PER_PAGE * (page - 1)).take(ITEMS_PER_PAGE).toList().toMutableObservableList()
                            customerPagination.pageCount = (customers.count() / ITEMS_PER_PAGE) + 1
                        }
                        editButton.disableProperty() rebind selectionModel.selectedItemProperty().isNull
                        // emailLabel.textProperty() rebind stringBindingOf(selectionModel.selectedItemProperty()) { selectionModel.selectedItem?.email ?: "" }
                        // phone1Label.textProperty() rebind stringBindingOf(selectionModel.selectedItemProperty()) { selectionModel.selectedItem?.phone1 ?: "" }
                        // phone2Label.textProperty() rebind stringBindingOf(selectionModel.selectedItemProperty()) { selectionModel.selectedItem?.phone2 ?: "" }
                        noteLabel.textProperty() rebind stringBindingOf(selectionModel.selectedItemProperty()) { selectionModel.selectedItem?.note ?: "" }
                    }
                }
                listView
            }
        }*/
    }

    @FXML fun clearOnAction() {
        customerField.text = ""
    }

    @FXML
    fun addOnAction() = CustomerDialog(this).showAndWait().ifPresent { customer ->
        customerField.text = customer.id.toString()
    }

    @FXML
    fun editOnAction() = CustomerDialog(this, listView.selectionModel.selectedItem).showAndWait().ifPresent { customer ->
        customerField.text = customer.id.toString()
    }
}