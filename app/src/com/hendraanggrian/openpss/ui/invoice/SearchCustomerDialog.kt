package com.hendraanggrian.openpss.ui.invoice

import com.hendraanggrian.openpss.R
import com.hendraanggrian.openpss.db.schemas.Customer
import com.hendraanggrian.openpss.db.schemas.Customers
import com.hendraanggrian.openpss.db.transaction
import com.hendraanggrian.openpss.ui.Resourced
import com.hendraanggrian.openpss.ui.Selectable
import com.hendraanggrian.openpss.utils.getResource
import javafx.scene.control.ButtonType.OK
import javafx.scene.control.Dialog
import javafx.scene.control.ListView
import javafx.scene.control.SelectionModel
import javafx.scene.control.TextField
import javafx.scene.image.ImageView
import javafx.scene.input.KeyCode.ENTER
import ktfx.application.later
import ktfx.beans.binding.bindingOf
import ktfx.collections.toMutableObservableList
import ktfx.coroutines.listener
import ktfx.coroutines.onKeyPressed
import ktfx.coroutines.onMouseClicked
import ktfx.layouts.listView
import ktfx.layouts.textField
import ktfx.layouts.vbox
import ktfx.scene.control.cancelButton
import ktfx.scene.control.graphicIcon
import ktfx.scene.control.headerTitle
import ktfx.scene.control.okButton
import ktfx.scene.input.isDoubleClick
import kotlin.text.RegexOption.IGNORE_CASE

class SearchCustomerDialog(resourced: Resourced) : Dialog<Customer>(), Resourced by resourced,
    Selectable<Customer> {

    private companion object {
        const val ITEMS_PER_PAGE = 10
    }

    private lateinit var searchField: TextField
    private lateinit var customerList: ListView<Customer>

    init {
        headerTitle = getString(R.string.search_customer)
        graphicIcon = ImageView(R.image.ic_customer)
        dialogPane.content = vbox {
            searchField = textField {
                promptText = getString(R.string.customer)
                stylesheets += getResource(R.style.textfield_search).toExternalForm()
            }
            customerList = listView<Customer> {
                prefHeight = 252.0
                itemsProperty().bind(bindingOf(searchField.textProperty()) {
                    transaction {
                        when {
                            searchField.text.isEmpty() -> Customers.find()
                            else -> Customers.find { name.matches(searchField.text.toRegex(IGNORE_CASE).toPattern()) }
                        }.take(ITEMS_PER_PAGE).toMutableObservableList()
                    }
                })
                itemsProperty().listener { _, _, value -> if (value.isNotEmpty()) selectionModel.selectFirst() }
                onMouseClicked {
                    if (selected != null && it.isDoubleClick()) {
                        result = selected
                        close()
                    }
                }
                onKeyPressed {
                    if (selected != null && it.code == ENTER) {
                        result = selected
                        close()
                    }
                }
            } marginTop 8.0
        }
        cancelButton()
        okButton { disableProperty().bind(!selectedBinding) }
        later { searchField.requestFocus() }
        setResultConverter { if (it == OK) selected else null }
    }

    override val selectionModel: SelectionModel<Customer> get() = customerList.selectionModel
}