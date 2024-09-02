package com.hanggrian.openpss.ui.customer

import com.hanggrian.openpss.OpenPssApp
import com.hanggrian.openpss.PATTERN_DATE
import com.hanggrian.openpss.R
import com.hanggrian.openpss.control.CustomerListView
import com.hanggrian.openpss.db.schemas.Customer
import com.hanggrian.openpss.db.schemas.Customers
import com.hanggrian.openpss.db.transaction
import com.hanggrian.openpss.popup.dialog.ConfirmDialog
import com.hanggrian.openpss.ui.ActionController
import com.hanggrian.openpss.ui.Refreshable
import com.hanggrian.openpss.util.isNotEmpty
import com.hanggrian.openpss.util.matches
import com.hanggrian.openpss.util.stringCell
import javafx.fxml.FXML
import javafx.scene.control.Button
import javafx.scene.control.Label
import javafx.scene.control.ListView
import javafx.scene.control.MenuItem
import javafx.scene.control.Pagination
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
import ktfx.bindings.bindingBy
import ktfx.bindings.bindingOf
import ktfx.bindings.given
import ktfx.bindings.otherwise
import ktfx.bindings.stringBindingBy
import ktfx.bindings.then
import ktfx.collections.emptyObservableList
import ktfx.collections.toMutableObservableList
import ktfx.collections.toObservableList
import ktfx.controls.selectedBinding
import ktfx.coroutines.onAction
import ktfx.jfoenix.controls.jfxSnackbar
import ktfx.jfoenix.controls.show
import ktfx.jfoenix.layouts.jfxTextField
import ktfx.jfoenix.layouts.styledJfxButton
import ktfx.layouts.NodeContainer
import ktfx.layouts.contextMenu
import ktfx.layouts.tooltip
import ktfx.runLater
import org.controlsfx.control.MasterDetailPane
import java.net.URL
import java.util.ResourceBundle
import java.util.regex.Pattern.CASE_INSENSITIVE

class CustomerController :
    ActionController(),
    Refreshable {
    @FXML
    lateinit var masterDetailPane: MasterDetailPane

    @FXML
    lateinit var customerPagination: Pagination

    @FXML
    lateinit var noImage: ImageView

    @FXML
    lateinit var noLabel: Label

    @FXML
    lateinit var sinceImage: ImageView

    @FXML
    lateinit var sinceLabel: Label

    @FXML
    lateinit var addressImage: ImageView

    @FXML
    lateinit var addressLabel: Label

    @FXML
    lateinit var noteImage: ImageView

    @FXML
    lateinit var noteLabel: Label

    @FXML
    lateinit var contactImage: ImageView

    @FXML
    lateinit var contactTable: TableView<Customer.Contact>

    @FXML
    lateinit var typeColumn: TableColumn<Customer.Contact, String>

    @FXML
    lateinit var valueColumn: TableColumn<Customer.Contact, String>

    @FXML
    lateinit var addContactItem: MenuItem

    @FXML
    lateinit var deleteContactItem: MenuItem

    private lateinit var refreshButton: Button
    private lateinit var addButton: Button
    private lateinit var searchField: TextField

    private lateinit var customerList: ListView<Customer>

    override fun NodeContainer.onCreateActions() {
        refreshButton =
            styledJfxButton(null, ImageView(R.image_act_refresh), R.style_flat) {
                tooltip(getString(R.string_refresh))
                onAction { refresh() }
            }
        addButton =
            styledJfxButton(null, ImageView(R.image_act_add), R.style_flat) {
                tooltip(getString(R.string_add))
                onAction { add() }
            }
        searchField = jfxTextField { promptText = getString(R.string_search) }
    }

    override fun initialize(location: URL, resources: ResourceBundle) {
        super.initialize(location, resources)
        noImage.tooltip(getString(R.string_id))
        sinceImage.tooltip(getString(R.string_since))
        addressImage.tooltip(getString(R.string_address))
        noteImage.tooltip(getString(R.string_note))
        contactImage.tooltip(getString(R.string_contact))
        typeColumn.stringCell { typedType.toString(this@CustomerController) }
        valueColumn.stringCell { value }
    }

    override fun refresh() =
        runLater {
            customerPagination.maxPageIndicatorCountProperty().bind(
                given(searchField.textProperty().isEmpty) then
                    customerPagination.pageCount otherwise
                    5,
            )
            customerPagination.pageFactoryProperty().bind(
                bindingOf(searchField.textProperty()) {
                    Callback { page ->
                        customerList =
                            CustomerListView().apply {
                                styleClass += R.style_list_view_no_scrollbar_vertical
                                runLater {
                                    transaction {
                                        val customers =
                                            when {
                                                searchField.text.isBlank() -> Customers()
                                                else ->
                                                    Customers {
                                                        it.name.matches(
                                                            searchField.text,
                                                            CASE_INSENSITIVE,
                                                        ) or
                                                            it.address.matches(
                                                                searchField.text,
                                                                CASE_INSENSITIVE,
                                                            ) or
                                                            it.note.matches(
                                                                searchField.text,
                                                                CASE_INSENSITIVE,
                                                            )
                                                    }
                                            }
                                        items =
                                            customers
                                                .skip(customerPagination.pageCount * page)
                                                .take(customerPagination.pageCount)
                                                .toMutableObservableList()
                                        contextMenu {
                                            getString(R.string_edit)(ImageView(R.image_menu_edit)) {
                                                disableProperty()
                                                    .bind(
                                                        selectionModel
                                                            .selectedItemProperty()
                                                            .isNull,
                                                    )
                                                onAction { edit() }
                                            }
                                        }
                                        deleteContactItem
                                            .disableProperty()
                                            .bind(
                                                contactTable.selectionModel
                                                    .selectedItemProperty()
                                                    .isNull,
                                            )
                                    }
                                }
                            }
                        titleProperty.bind(
                            customerList.selectionModel
                                .selectedItemProperty()
                                .stringBindingBy { it?.name.orEmpty() },
                        )
                        noLabel.textProperty().bind(
                            customerList.selectionModel
                                .selectedItemProperty()
                                .stringBindingBy { it?.no?.toString().orEmpty() },
                        )
                        sinceLabel.textProperty().bind(
                            customerList.selectionModel
                                .selectedItemProperty()
                                .stringBindingBy { it?.since?.toString(PATTERN_DATE).orEmpty() },
                        )
                        addressLabel.textProperty().bind(
                            customerList.selectionModel
                                .selectedItemProperty()
                                .stringBindingBy { it?.address ?: "-" },
                        )
                        noteLabel.textProperty().bind(
                            customerList.selectionModel
                                .selectedItemProperty()
                                .stringBindingBy { it?.note ?: "-" },
                        )
                        contactTable.itemsProperty().bind(
                            customerList.selectionModel
                                .selectedItemProperty()
                                .bindingBy {
                                    it?.contacts?.toObservableList() ?: emptyObservableList()
                                },
                        )
                        masterDetailPane
                            .showDetailNodeProperty()
                            .bind(customerList.selectionModel.selectedBinding)
                        customerList
                    }
                },
            )
        }

    fun add() =
        AddCustomerDialog(this).show { customer ->
            transaction {
                when {
                    Customers { Customers.name.matches("^$customer$", CASE_INSENSITIVE) }
                        .isNotEmpty() ->
                        stack.jfxSnackbar.show(
                            getString(R.string_name_taken),
                            OpenPssApp.DURATION_SHORT,
                        )
                    else -> {
                        (AddCustomerAction(this@CustomerController, customer!!)) {
                            customerList.items.add(it)
                            customerList.selectionModel.select(customerList.items.lastIndex)
                        }
                    }
                }
            }
        }

    private fun edit() =
        EditCustomerDialog(this, customerList.selectionModel.selectedItem).show {
            EditCustomerAction(
                this@CustomerController,
                customerList.selectionModel.selectedItem,
                it!!.name,
                it.address,
                it.note,
            ).invoke { reload() }
        }

    @FXML
    fun addContact() =
        AddContactPopover(this).show(contactTable) {
            AddContactAction(
                this@CustomerController,
                customerList.selectionModel.selectedItem,
                it!!,
            ).invoke { reload() }
        }

    @FXML
    fun deleteContact() {
        val contact = contactTable.selectionModel.selectedItem
        ConfirmDialog(this, getString(R.string__delete_contact, contact.value)).show {
            DeleteContactAction(
                this@CustomerController,
                customerList.selectionModel.selectedItem,
                contact,
            ).invoke { reload() }
        }
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
