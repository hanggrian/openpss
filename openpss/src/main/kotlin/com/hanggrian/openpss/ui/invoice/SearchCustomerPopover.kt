package com.hanggrian.openpss.ui.invoice

import com.hanggrian.openpss.Context
import com.hanggrian.openpss.R
import com.hanggrian.openpss.control.CustomerListView
import com.hanggrian.openpss.db.schemas.Customer
import com.hanggrian.openpss.db.schemas.Customers
import com.hanggrian.openpss.db.transaction
import com.hanggrian.openpss.popup.popover.ResultablePopover
import javafx.scene.control.ListView
import javafx.scene.control.TextField
import javafx.scene.input.KeyCode.ENTER
import ktfx.bindings.bindingBy
import ktfx.collections.toMutableObservableList
import ktfx.controls.insetsOf
import ktfx.controls.isSelected
import ktfx.controls.notSelectedBinding
import ktfx.coroutines.listener
import ktfx.coroutines.onKeyPressed
import ktfx.coroutines.onMouseClicked
import ktfx.inputs.isDoubleClick
import ktfx.jfoenix.layouts.jfxTextField
import ktfx.layouts.vbox
import kotlin.text.RegexOption.IGNORE_CASE

class SearchCustomerPopover(context: Context) :
    ResultablePopover<Customer>(context, R.string_search_customer) {
    private val searchField: TextField
    private val customerList: ListView<Customer>

    init {
        vbox {
            searchField = jfxTextField { promptText = getString(R.string_name) }
            customerList =
                addChild(
                    CustomerListView().apply {
                        prefHeight = 262.0
                        itemsProperty().bind(
                            searchField.textProperty().bindingBy { s ->
                                transaction {
                                    when {
                                        s.isNullOrBlank() -> Customers()
                                        else ->
                                            Customers {
                                                it.name.matches(s.toRegex(IGNORE_CASE).toPattern())
                                            }
                                    }.take(ITEMS_PER_PAGE).toMutableObservableList()
                                }
                            },
                        )
                        itemsProperty().listener { _, _, value ->
                            if (value.isNotEmpty()) {
                                selectionModel.selectFirst()
                            }
                        }
                        onMouseClicked {
                            if (it.isDoubleClick() && selectionModel.isSelected()) {
                                defaultButton.fire()
                            }
                        }
                        onKeyPressed {
                            if (it.code == ENTER && selectionModel.isSelected()) {
                                defaultButton.fire()
                            }
                        }
                    },
                ).margin(insetsOf(top = getDouble(R.dimen_padding_medium)))
        }
        defaultButton
            .disableProperty()
            .bind(customerList.selectionModel.notSelectedBinding)
    }

    override val nullableResult: Customer? get() = customerList.selectionModel.selectedItem

    private companion object {
        const val ITEMS_PER_PAGE = 10
    }
}
