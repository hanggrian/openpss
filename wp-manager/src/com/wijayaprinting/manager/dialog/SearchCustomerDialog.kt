package com.wijayaprinting.manager.dialog

import com.wijayaprinting.dao.Customer
import com.wijayaprinting.dao.Customers
import com.wijayaprinting.manager.Component
import com.wijayaprinting.manager.R
import com.wijayaprinting.manager.utils.safeTransaction
import javafx.scene.control.Dialog
import kotfx.*
import org.jetbrains.exposed.sql.or

class SearchCustomerDialog(private val component: Component) : Dialog<Customer>(), Component by component {

    init {
        content = vbox {
            val field = textField { promptText = getString(R.string.customer) }
            listView<Customer> {
                itemsProperty() bind bindingOf(field.textProperty()) {
                    safeTransaction {
                        when {
                            field.text.isEmpty() -> Customer.all()
                            else -> Customer.find { Customers.name regexp field.text or (Customers.name regexp field.text) }
                        }.limit(10).toMutableObservableList()
                    }
                }
            } marginTop 8
        }
    }
}