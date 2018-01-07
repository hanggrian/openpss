package com.wijayaprinting.manager.controller

import com.wijayaprinting.dao.Customer
import com.wijayaprinting.dao.Customers
import com.wijayaprinting.manager.dialog.CustomerDialog
import com.wijayaprinting.manager.utils.safeTransaction
import javafx.fxml.FXML
import javafx.scene.Node
import javafx.scene.control.Label
import javafx.scene.control.ListView
import javafx.scene.control.Pagination
import javafx.scene.control.TextField
import javafx.util.Callback
import kotfx.*
import org.jetbrains.exposed.sql.or

class CustomerController : Controller() {

    companion object {
        const val ITEMS_PER_PAGE = 20
    }

    @FXML lateinit var customerField: TextField
    @FXML lateinit var customerPagination: Pagination
    @FXML lateinit var emailLabel: Label
    @FXML lateinit var phone1Label: Label
    @FXML lateinit var phone2Label: Label
    @FXML lateinit var noteLabel: Label

    @FXML
    fun initialize() {
        customerPagination.pageFactoryProperty() bind bindingOf(customerField.textProperty()) {
            Callback<Int, Node> { page ->
                createPage(page).apply {
                    emailLabel.textProperty().unbind()
                    emailLabel.textProperty() bind stringBindingOf(selectionModel.selectedItemProperty()) { selectionModel.selectedItem?.email ?: "" }
                    phone1Label.textProperty().unbind()
                    phone1Label.textProperty() bind stringBindingOf(selectionModel.selectedItemProperty()) { selectionModel.selectedItem?.phone1 ?: "" }
                    phone2Label.textProperty().unbind()
                    phone2Label.textProperty() bind stringBindingOf(selectionModel.selectedItemProperty()) { selectionModel.selectedItem?.phone2 ?: "" }
                    noteLabel.textProperty().unbind()
                    noteLabel.textProperty() bind stringBindingOf(selectionModel.selectedItemProperty()) { selectionModel.selectedItem?.note ?: "" }
                }
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

    private fun createPage(page: Int): ListView<Customer> = listView {
        safeTransaction {
            val customers = when {
                customerField.text.isEmpty() -> Customer.all()
                else -> Customer.find { Customers.id eq customerField.text.toIntOrNull() or (Customers.name regexp customerField.text) }
            }
            items = customers.limit(ITEMS_PER_PAGE, ITEMS_PER_PAGE * page).toMutableObservableList()
            customerPagination.pageCount = (customers.count() / ITEMS_PER_PAGE) + 1
        }
    }
}