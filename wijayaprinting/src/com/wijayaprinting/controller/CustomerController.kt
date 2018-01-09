package com.wijayaprinting.controller

import bindings.`else`
import bindings.`if`
import bindings.then
import com.wijayaprinting.dao.Customer
import com.wijayaprinting.dao.Customers
import com.wijayaprinting.dialog.CustomerDialog
import com.wijayaprinting.utils.expose
import javafx.fxml.FXML
import javafx.scene.Node
import javafx.scene.control.*
import javafx.scene.text.Font.loadFont
import javafx.util.Callback
import kotfx.*
import org.jetbrains.exposed.sql.or

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

        customerPagination.pageFactoryProperty() bind bindingOf(customerField.textProperty()) {
            Callback<Int, Node> { page ->
                listView = listView {
                    expose {
                        val customers = when {
                            customerField.text.isEmpty() -> Customer.all()
                            else -> Customer.find { Customers.id eq customerField.text.toIntOrNull() or (Customers.name regexp customerField.text) }
                        }
                        items = customers.limit(ITEMS_PER_PAGE, ITEMS_PER_PAGE * page).toMutableObservableList()
                        customerPagination.pageCount = (customers.count() / ITEMS_PER_PAGE) + 1
                    }
                    editButton.disableProperty() rebind selectionModel.selectedItemProperty().isNull
                    emailLabel.textProperty() rebind stringBindingOf(selectionModel.selectedItemProperty()) { selectionModel.selectedItem?.email ?: "" }
                    phone1Label.textProperty() rebind stringBindingOf(selectionModel.selectedItemProperty()) { selectionModel.selectedItem?.phone1 ?: "" }
                    phone2Label.textProperty() rebind stringBindingOf(selectionModel.selectedItemProperty()) { selectionModel.selectedItem?.phone2 ?: "" }
                    noteLabel.textProperty() rebind stringBindingOf(selectionModel.selectedItemProperty()) { selectionModel.selectedItem?.note ?: "" }
                }
                listView
            }
        }
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