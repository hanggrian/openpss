package com.hendraanggrian.openpss.ui.customer

import com.hendraanggrian.openpss.App
import com.hendraanggrian.openpss.R
import com.hendraanggrian.openpss.content.PATTERN_DATE
import com.hendraanggrian.openpss.control.CustomerListView
import com.hendraanggrian.openpss.control.PaginatedPane
import com.hendraanggrian.openpss.db.schemas.Customer
import com.hendraanggrian.openpss.db.schemas.Customers
import com.hendraanggrian.openpss.db.transaction
import com.hendraanggrian.openpss.popup.dialog.ConfirmDialog
import com.hendraanggrian.openpss.ui.ActionController
import com.hendraanggrian.openpss.ui.Refreshable
import com.hendraanggrian.openpss.util.isNotEmpty
import com.hendraanggrian.openpss.util.matches
import com.hendraanggrian.openpss.util.stringCell
import javafx.fxml.FXML
import javafx.scene.Node
import javafx.scene.control.Button
import javafx.scene.control.Label
import javafx.scene.control.ListView
import javafx.scene.control.MenuItem
import javafx.scene.control.TableColumn
import javafx.scene.control.TableView
import javafx.scene.control.TextField
import javafx.scene.image.ImageView
import javafx.util.Callback
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.javafx.JavaFx
import kotlinx.coroutines.launch
import ktfx.bindings.asAny
import ktfx.bindings.asString
import ktfx.bindings.bindingOf
import ktfx.collections.emptyObservableList
import ktfx.collections.toMutableObservableList
import ktfx.collections.toObservableList
import ktfx.coroutines.onAction
import ktfx.jfoenix.controls.jfxSnackbar
import ktfx.jfoenix.layouts.jfxTextField
import ktfx.jfoenix.layouts.styledJFXButton
import ktfx.layouts.NodeManager
import ktfx.layouts.contextMenu
import ktfx.layouts.tooltip
import ktfx.runLater
import org.controlsfx.control.MasterDetailPane
import java.net.URL
import java.util.ResourceBundle
import java.util.regex.Pattern.CASE_INSENSITIVE
import kotlin.math.ceil

class CustomerController : ActionController(), Refreshable {

    @FXML lateinit var masterDetailPane: MasterDetailPane
    @FXML lateinit var customerPagination: PaginatedPane
    @FXML lateinit var noImage: ImageView
    @FXML lateinit var noLabel: Label
    @FXML lateinit var sinceImage: ImageView
    @FXML lateinit var sinceLabel: Label
    @FXML lateinit var addressImage: ImageView
    @FXML lateinit var addressLabel: Label
    @FXML lateinit var noteImage: ImageView
    @FXML lateinit var noteLabel: Label
    @FXML lateinit var contactImage: ImageView
    @FXML lateinit var contactTable: TableView<Customer.Contact>
    @FXML lateinit var typeColumn: TableColumn<Customer.Contact, String>
    @FXML lateinit var valueColumn: TableColumn<Customer.Contact, String>
    @FXML lateinit var addContactItem: MenuItem
    @FXML lateinit var deleteContactItem: MenuItem

    private lateinit var refreshButton: Button
    private lateinit var addButton: Button
    private lateinit var searchField: TextField

    private lateinit var customerList: ListView<Customer>

    override fun NodeManager.onCreateActions() {
        refreshButton = styledJFXButton(null, ImageView(R.image.act_refresh), R.style.flat) {
            tooltip(getString(R.string.refresh))
            onAction { refresh() }
        }
        addButton = styledJFXButton(null, ImageView(R.image.act_add), R.style.flat) {
            tooltip(getString(R.string.add))
            onAction { refresh() }
        }
        searchField = jfxTextField {
            promptText = getString(R.string.search)
        }
    }

    override fun initialize(location: URL, resources: ResourceBundle) {
        super.initialize(location, resources)
        noImage.tooltip(getString(R.string.id))
        sinceImage.tooltip(getString(R.string.since))
        addressImage.tooltip(getString(R.string.address))
        noteImage.tooltip(getString(R.string.note))
        contactImage.tooltip(getString(R.string.contact))
        typeColumn.stringCell { typedType.toString(this@CustomerController) }
        valueColumn.stringCell { value }
    }

    override fun refresh() = runLater {
        customerPagination.contentFactoryProperty().bind(
            bindingOf(searchField.textProperty()) {
                Callback<Pair<Int, Int>, Node> { (page, count) ->
                    customerList = CustomerListView().apply {
                        styleClass += R.style.list_view_no_scrollbar_vertical
                        runLater {
                            transaction {
                                val customers = Customers.buildQuery {
                                    if (searchField.text.isNotBlank()) {
                                        or(it.name.matches(searchField.text, CASE_INSENSITIVE))
                                        or(it.address.matches(searchField.text, CASE_INSENSITIVE))
                                        or(it.note.matches(searchField.text, CASE_INSENSITIVE))
                                    }
                                }
                                customerPagination.pageCount = ceil(customers.count() / count.toDouble()).toInt()
                                items = customers
                                    .skip(count * page)
                                    .take(count)
                                    .toMutableObservableList()
                                contextMenu {
                                    getString(R.string.edit)(ImageView(R.image.menu_edit)) {
                                        disableProperty().bind(selectionModel.selectedItemProperty().isNull)
                                        onAction { edit() }
                                    }
                                }
                                deleteContactItem.disableProperty()
                                    .bind(contactTable.selectionModel.selectedItemProperty().isNull)
                            }
                        }
                    }
                    titleProperty().bind(
                        customerList.selectionModel.selectedItemProperty().asString { it?.name.orEmpty() }
                    )
                    noLabel.textProperty().bind(
                        customerList.selectionModel.selectedItemProperty().asString { it?.no?.toString().orEmpty() }
                    )
                    sinceLabel.textProperty().bind(
                        customerList.selectionModel.selectedItemProperty()
                            .asString { it?.since?.toString(PATTERN_DATE).orEmpty() }
                    )
                    addressLabel.textProperty().bind(
                        customerList.selectionModel.selectedItemProperty().asString { it?.address ?: "-" }
                    )
                    noteLabel.textProperty().bind(
                        customerList.selectionModel.selectedItemProperty().asString { it?.note ?: "-" }
                    )
                    contactTable.itemsProperty().bind(
                        customerList.selectionModel.selectedItemProperty()
                            .asAny { it?.contacts?.toObservableList() ?: emptyObservableList() }
                    )
                    masterDetailPane.showDetailNodeProperty()
                        .bind(customerList.selectionModel.selectedItemProperty().isNotNull)
                    customerList
                }
            }
        )
    }

    fun add() = AddCustomerDialog(this).show { customer ->
        transaction {
            when {
                Customers { Customers.name.matches("^$customer$", CASE_INSENSITIVE) }.isNotEmpty() ->
                    stack.jfxSnackbar(getString(R.string.name_taken), App.DURATION_SHORT)
                else -> {
                    (AddCustomerAction(this@CustomerController, customer!!)) {
                        customerList.items.add(it)
                        customerList.selectionModel.select(customerList.items.lastIndex)
                    }
                }
            }
        }
    }

    private fun edit() = EditCustomerDialog(this, customerList.selectionModel.selectedItem).show {
        (
            EditCustomerAction(
                this@CustomerController,
                customerList.selectionModel.selectedItem,
                it!!.name,
                it.address,
                it.note
            )
            ) { reload() }
    }

    @FXML fun addContact() = AddContactPopover(this).show(contactTable) {
        (AddContactAction(this@CustomerController, customerList.selectionModel.selectedItem, it!!)) { reload() }
    }

    @FXML fun deleteContact() = ConfirmDialog(this, R.string.delete_contact).show {
        (
            DeleteContactAction(
                this@CustomerController,
                customerList.selectionModel.selectedItem,
                contactTable.selectionModel.selectedItem
            )
            ) { reload() }
    }

    private fun reload() {
        val index = customerList.selectionModel.selectedIndex
        refresh()
        GlobalScope.launch(Dispatchers.JavaFx) {
            delay(250)
            customerList.selectionModel.clearAndSelect(index)
        }
    }
}
