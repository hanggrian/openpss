package com.wijayaprinting.manager.controller

import com.wijayaprinting.dao.Customer
import com.wijayaprinting.dao.Customers
import com.wijayaprinting.manager.dialog.CustomerDialog
import com.wijayaprinting.manager.utils.safeTransaction
import javafx.fxml.FXML
import javafx.scene.control.Label
import javafx.scene.control.ListView
import javafx.scene.control.Pagination
import javafx.scene.control.TextField
import kotfx.bind
import kotfx.listView
import kotfx.stringBindingOf
import kotfx.toMutableObservableList
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
        customerPagination.setPageFactory { page ->
            val listView = createPage(page)
            emailLabel.textProperty().unbind()
            emailLabel.textProperty() bind stringBindingOf(listView.selectionModel.selectedItemProperty()) { listView.selectionModel.selectedItem?.email ?: "-" }
            phone1Label.textProperty().unbind()
            phone1Label.textProperty() bind stringBindingOf(listView.selectionModel.selectedItemProperty()) { listView.selectionModel.selectedItem?.phone1 ?: "-" }
            phone2Label.textProperty().unbind()
            phone2Label.textProperty() bind stringBindingOf(listView.selectionModel.selectedItemProperty()) { listView.selectionModel.selectedItem?.phone2 ?: "-" }
            noteLabel.textProperty().unbind()
            noteLabel.textProperty() bind stringBindingOf(listView.selectionModel.selectedItemProperty()) { listView.selectionModel.selectedItem?.note ?: "-" }
            listView
        }
    }

    @FXML
    fun refreshOnAction() = safeTransaction {
        // customerPagination.update()
    }

    @FXML
    fun addOnAction() = CustomerDialog(this).showAndWait().ifPresent {

    }

    private fun createPage(page: Int): ListView<Customer> = listView {
        items = safeTransaction {
            when {
                customerField.text.isEmpty() -> Customer.all()
                else -> Customer.find { Customers.id eq customerField.text.toIntOrNull() or (Customers.name regexp customerField.text) }
            }.limit(ITEMS_PER_PAGE, ITEMS_PER_PAGE * page).toMutableObservableList()
        }
    }
}