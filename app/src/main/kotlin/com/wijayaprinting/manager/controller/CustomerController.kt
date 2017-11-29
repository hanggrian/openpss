package com.wijayaprinting.manager.controller

import com.wijayaprinting.manager.safeTransaction
import com.wijayaprinting.manager.control.CustomerPagination
import javafx.fxml.FXML
import javafx.scene.control.Label
import javafx.scene.control.TextField

class CustomerController {

    @FXML lateinit var customerField: TextField
    @FXML lateinit var customerPagination: CustomerPagination
    @FXML lateinit var nameLabel: Label

    @FXML
    fun initialize() {
        customerPagination.textProperty.bind(customerField.textProperty())
        // nameLabel.textProperty().bind(stringBindingOf(customerPagination.currentPageContainer))
    }

    @FXML
    fun refreshButtonOnAction() = safeTransaction {
        customerPagination.update()
    }
}