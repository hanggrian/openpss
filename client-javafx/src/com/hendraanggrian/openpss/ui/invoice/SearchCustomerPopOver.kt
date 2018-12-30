package com.hendraanggrian.openpss.ui.invoice

import com.hendraanggrian.openpss.R
import com.hendraanggrian.openpss.ui.customer.CustomerListView
import com.hendraanggrian.openpss.data.Customer
import com.hendraanggrian.openpss.ui.FxComponent
import com.hendraanggrian.openpss.ui.ResultablePopOver
import javafx.scene.control.ListView
import javafx.scene.control.TextField
import javafx.scene.input.KeyCode.ENTER
import kotlinx.coroutines.runBlocking
import ktfx.bindings.buildBinding
import ktfx.collections.toObservableList
import ktfx.controls.isSelected
import ktfx.coroutines.listener
import ktfx.coroutines.onKeyPressed
import ktfx.coroutines.onMouseClicked
import ktfx.inputs.isDoubleClick
import ktfx.jfoenix.jfxTextField
import ktfx.layouts.vbox

class SearchCustomerPopOver(component: FxComponent) :
    ResultablePopOver<Customer>(component, R.string.search_customer) {

    private companion object {
        const val ITEMS_PER_PAGE = 10
    }

    private lateinit var searchField: TextField
    private lateinit var customerList: ListView<Customer>

    init {
        vbox {
            searchField = jfxTextField {
                promptText = getString(R.string.name)
            }
            customerList = CustomerListView().apply {
                prefHeight = 262.0
                itemsProperty().bind(buildBinding(searchField.textProperty()) {
                    runBlocking {
                        api.getCustomers(searchField.text, 0, ITEMS_PER_PAGE)
                            .items
                            .take(ITEMS_PER_PAGE)
                            .toObservableList()
                    }
                })
                itemsProperty().listener { _, _, value ->
                    if (value.isNotEmpty()) selectionModel.selectFirst()
                }
                onMouseClicked {
                    if (it.isDoubleClick() && customerList.selectionModel.isSelected()) {
                        defaultButton.fire()
                    }
                }
                onKeyPressed {
                    if (it.code == ENTER && customerList.selectionModel.isSelected()) {
                        defaultButton.fire()
                    }
                }
            }() marginTop getDouble(R.value.padding_medium)
        }
        defaultButton.disableProperty()
            .bind(customerList.selectionModel.selectedItemProperty().isNull)
    }

    override val nullableResult: Customer? get() = customerList.selectionModel.selectedItem
}