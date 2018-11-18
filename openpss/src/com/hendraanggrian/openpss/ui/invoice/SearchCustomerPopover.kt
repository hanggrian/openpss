package com.hendraanggrian.openpss.ui.invoice

import com.hendraanggrian.openpss.R
import com.hendraanggrian.openpss.content.Context
import com.hendraanggrian.openpss.control.CustomerListView
import com.hendraanggrian.openpss.db.schemas.Customer
import com.hendraanggrian.openpss.db.schemas.Customers
import com.hendraanggrian.openpss.db.transaction
import com.hendraanggrian.openpss.popup.popover.ResultablePopover
import javafx.scene.control.ListView
import javafx.scene.control.TextField
import javafx.scene.input.KeyCode.ENTER
import ktfx.beans.binding.buildBinding
import ktfx.collections.toMutableObservableList
import ktfx.coroutines.listener
import ktfx.coroutines.onKeyPressed
import ktfx.coroutines.onMouseClicked
import ktfx.jfoenix.jfxTextField
import ktfx.layouts.vbox
import ktfx.scene.control.isSelected
import ktfx.scene.input.isDoubleClick
import kotlin.text.RegexOption.IGNORE_CASE

class SearchCustomerPopover(context: Context) : ResultablePopover<Customer>(context, R.string.search_customer) {

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
                    transaction {
                        when {
                            searchField.text.isEmpty() -> Customers()
                            else -> Customers { it.name.matches(searchField.text.toRegex(IGNORE_CASE).toPattern()) }
                        }.take(ITEMS_PER_PAGE).toMutableObservableList()
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