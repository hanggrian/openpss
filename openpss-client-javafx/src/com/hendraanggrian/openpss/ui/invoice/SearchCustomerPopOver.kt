package com.hendraanggrian.openpss.ui.invoice

import com.hendraanggrian.openpss.FxComponent
import com.hendraanggrian.openpss.R
import com.hendraanggrian.openpss.R2
import com.hendraanggrian.openpss.api.OpenPSSApi
import com.hendraanggrian.openpss.schema.Customer
import com.hendraanggrian.openpss.ui.ResultablePopOver
import com.hendraanggrian.openpss.ui.customer.CustomerListView
import javafx.scene.control.ListView
import javafx.scene.control.TextField
import javafx.scene.input.KeyCode.ENTER
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import ktfx.bindings.buildBinding
import ktfx.collections.toObservableList
import ktfx.controls.isSelected
import ktfx.controls.notSelectedBinding
import ktfx.coroutines.listener
import ktfx.coroutines.onKeyPressed
import ktfx.coroutines.onMouseClicked
import ktfx.inputs.isDoubleClick
import ktfx.jfoenix.layouts.jfxTextField
import ktfx.layouts.addNode
import ktfx.layouts.vbox

class SearchCustomerPopOver(component: FxComponent) :
    ResultablePopOver<Customer>(component, R2.string.search_customer) {

    private companion object {
        const val ITEMS_PER_PAGE = 10
    }

    private val searchField: TextField
    private lateinit var customerList: ListView<Customer>

    init {
        vbox {
            searchField = jfxTextField {
                promptText = getString(R2.string.name)
            }
            customerList = addNode(CustomerListView()) {
                marginTop = getDouble(R.value.padding_medium)
                prefHeight = 262.0
                itemsProperty().bind(buildBinding(searchField.textProperty()) {
                    runBlocking(Dispatchers.IO) {
                        OpenPSSApi.getCustomers(searchField.text, 0, ITEMS_PER_PAGE)
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
            }
        }
        defaultButton.disableProperty().bind(customerList.selectionModel.notSelectedBinding)
    }

    override val nullableResult: Customer? get() = customerList.selectionModel.selectedItem
}
