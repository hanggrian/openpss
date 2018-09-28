package com.hendraanggrian.openpss.ui.invoice

import com.hendraanggrian.openpss.App.Companion.STYLE_SEARCH_TEXTFIELD
import com.hendraanggrian.openpss.R
import com.hendraanggrian.openpss.control.popover.ResultablePopover
import com.hendraanggrian.openpss.db.schemas.Customer
import com.hendraanggrian.openpss.db.schemas.Customers
import com.hendraanggrian.openpss.db.transaction
import com.hendraanggrian.openpss.i18n.Resourced
import com.hendraanggrian.openpss.ui.Selectable
import javafx.scene.control.ListView
import javafx.scene.control.SelectionModel
import javafx.scene.control.TextField
import javafx.scene.input.KeyCode.ENTER
import ktfx.beans.binding.bindingOf
import ktfx.collections.toMutableObservableList
import ktfx.coroutines.listener
import ktfx.coroutines.onKeyPressed
import ktfx.coroutines.onMouseClicked
import ktfx.layouts.listView
import ktfx.layouts.textField
import ktfx.layouts.vbox
import ktfx.scene.input.isDoubleClick
import kotlin.text.RegexOption.IGNORE_CASE

class SearchCustomerPopover(resourced: Resourced) : ResultablePopover<Customer>(resourced, R.string.search_customer),
    Selectable<Customer> {

    private companion object {
        const val ITEMS_PER_PAGE = 10
    }

    private lateinit var searchField: TextField
    private lateinit var customerList: ListView<Customer>

    init {
        vbox {
            searchField = textField {
                styleClass += STYLE_SEARCH_TEXTFIELD
                promptText = getString(R.string.customer)
            }
            customerList = listView<Customer> {
                prefHeight = 252.0
                itemsProperty().bind(bindingOf(searchField.textProperty()) {
                    transaction {
                        when {
                            searchField.text.isEmpty() -> Customers()
                            else -> Customers { it.name.matches(searchField.text.toRegex(IGNORE_CASE).toPattern()) }
                        }.take(ITEMS_PER_PAGE).toMutableObservableList()
                    }
                })
                itemsProperty().listener { _, _, value -> if (value.isNotEmpty()) selectionModel.selectFirst() }
                onMouseClicked { if (it.isDoubleClick() && selected != null) defaultButton.fire() }
                onKeyPressed { if (it.code == ENTER && selected != null) defaultButton.fire() }
            } marginTop R.dimen.padding_medium.toDouble()
        }
        defaultButton.disableProperty().bind(!selectedBinding)
    }

    override val nullableResult: Customer? get() = selected!!

    override val selectionModel: SelectionModel<Customer> get() = customerList.selectionModel
}