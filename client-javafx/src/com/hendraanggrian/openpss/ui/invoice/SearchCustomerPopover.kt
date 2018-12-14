package com.hendraanggrian.openpss.ui.invoice

import com.hendraanggrian.openpss.R
import com.hendraanggrian.openpss.content.FxComponent
import com.hendraanggrian.openpss.control.CustomerListView
import com.hendraanggrian.openpss.db.schemas.Customer
import com.hendraanggrian.openpss.popup.popover.ResultablePopover
import javafx.scene.control.ListView
import javafx.scene.control.TextField
import javafx.scene.input.KeyCode.ENTER
import kotlinx.coroutines.runBlocking
import ktfx.beans.binding.buildBinding
import ktfx.collections.toObservableList
import ktfx.coroutines.listener
import ktfx.coroutines.onKeyPressed
import ktfx.coroutines.onMouseClicked
import ktfx.jfoenix.jfxTextField
import ktfx.layouts.vbox
import ktfx.scene.control.isSelected
import ktfx.scene.input.isDoubleClick

class SearchCustomerPopover(component: FxComponent) : ResultablePopover<Customer>(component, R.string.search_customer) {

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
                        api.getCustomers(searchField.text, 1, ITEMS_PER_PAGE)
                            .items
                            .take(ITEMS_PER_PAGE)
                            .toObservableList()
                    }
                })
                itemsProperty().listener { _, _, value -> if (value.isNotEmpty()) selectionModel.selectFirst() }
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
            }() marginTop getDouble(R.dimen.padding_medium)
        }
        defaultButton.disableProperty().bind(customerList.selectionModel.selectedItemProperty().isNull)
    }

    override val nullableResult: Customer? get() = customerList.selectionModel.selectedItem
}