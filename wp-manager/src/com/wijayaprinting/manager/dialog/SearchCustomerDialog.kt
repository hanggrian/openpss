package com.wijayaprinting.manager.dialog

import com.hendraanggrian.rxexposed.SQLSingles
import com.wijayaprinting.dao.Customer
import com.wijayaprinting.dao.Customers
import com.wijayaprinting.manager.Component
import com.wijayaprinting.manager.R
import io.reactivex.rxkotlin.subscribeBy
import javafx.scene.control.Dialog
import javafx.scene.control.ListView
import kotfx.*

class SearchCustomerDialog(private val component: Component) : Dialog<Customer>(), Component by component {

    init {
        content = vbox {
            lateinit var listView: ListView<Customer>
            textField {
                promptText = getString(R.string.customer)
                textProperty().addListener { _, _, name ->
                    SQLSingles.transaction { Customer.find { Customers.name eq name }.toMutableObservableList() }
                            .subscribeBy({}) { customer -> listView.items = customer }
                            .register()
                }
            }
            listView = listView<Customer> {
                SQLSingles.transaction { Customer.all().toMutableObservableList() }
                        .subscribeBy({}) { customer -> items = customer }
                        .register()
            } marginTop 8
        }
    }
}