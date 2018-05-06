package com.hendraanggrian.openpss.ui.invoice

import com.hendraanggrian.openpss.App.Companion.STYLE_SEARCH_TEXTFIELD
import com.hendraanggrian.openpss.R
import com.hendraanggrian.openpss.controls.Popup
import com.hendraanggrian.openpss.db.schemas.Customer
import com.hendraanggrian.openpss.db.schemas.Customers
import com.hendraanggrian.openpss.db.transaction
import com.hendraanggrian.openpss.resources.Resourced
import com.hendraanggrian.openpss.ui.Selectable
import javafx.scene.Node
import javafx.scene.control.ListView
import javafx.scene.control.SelectionModel
import javafx.scene.control.TextField
import javafx.scene.input.KeyCode.ENTER
import ktfx.beans.binding.bindingOf
import ktfx.collections.toMutableObservableList
import ktfx.coroutines.listener
import ktfx.coroutines.onKeyPressed
import ktfx.coroutines.onMouseClicked
import ktfx.layouts.LayoutManager
import ktfx.layouts.listView
import ktfx.layouts.styledTextField
import ktfx.layouts.vbox
import ktfx.scene.input.isDoubleClick
import kotlin.text.RegexOption.IGNORE_CASE

class SearchCustomerPopup(resourced: Resourced) : Popup<Customer>(resourced, R.string.search_customer),
    Selectable<Customer> {

    private companion object {
        const val ITEMS_PER_PAGE = 10
    }

    private lateinit var searchField: TextField
    private lateinit var customerList: ListView<Customer>

    override val content: Node = vbox {
        searchField = styledTextField(STYLE_SEARCH_TEXTFIELD) {
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
        } marginTop 8.0
    }

    override fun LayoutManager<Node>.buttons() {
        defaultButton().disableProperty().bind(!selectedBinding)
    }

    override fun getResult(): Customer = selected!!

    override val selectionModel: SelectionModel<Customer> get() = customerList.selectionModel
}