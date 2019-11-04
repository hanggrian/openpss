package com.hendraanggrian.openpss.ui.customer

import com.hendraanggrian.openpss.PATTERN_DATE
import com.hendraanggrian.openpss.R
import com.hendraanggrian.openpss.R2
import com.hendraanggrian.openpss.api.OpenPSSApi
import com.hendraanggrian.openpss.control.Action
import com.hendraanggrian.openpss.control.PaginatedPane
import com.hendraanggrian.openpss.schema.Customer
import com.hendraanggrian.openpss.schema.typedType
import com.hendraanggrian.openpss.ui.ActionController
import com.hendraanggrian.openpss.ui.ConfirmDialog
import com.hendraanggrian.openpss.ui.Refreshable
import com.hendraanggrian.openpss.util.stringCell
import java.net.URL
import java.util.ResourceBundle
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
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import ktfx.bindings.buildBinding
import ktfx.bindings.buildStringBinding
import ktfx.collections.emptyObservableList
import ktfx.collections.toMutableObservableList
import ktfx.collections.toObservableList
import ktfx.coroutines.onAction
import ktfx.jfoenix.layouts.jfxTextField
import ktfx.layouts.NodeManager
import ktfx.layouts.addNode
import ktfx.layouts.contextMenu
import ktfx.layouts.tooltip
import ktfx.runLater
import org.controlsfx.control.MasterDetailPane

class CustomerController : ActionController(), Refreshable {

    @FXML lateinit var masterDetailPane: MasterDetailPane
    @FXML lateinit var customerPagination: PaginatedPane
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
        refreshButton = addNode(Action(getString(R2.string.refresh), R.image.action_refresh)) {
            onAction { refresh() }
        }
        addButton = addNode(Action(getString(R2.string.add), R.image.action_add)) {
            onAction { this@CustomerController.add() }
        }
        searchField = jfxTextField {
            promptText = getString(R2.string.search)
        }
    }

    override fun initialize(location: URL, resources: ResourceBundle) {
        super.initialize(location, resources)
        sinceImage.tooltip(getString(R2.string.since))
        addressImage.tooltip(getString(R2.string.address))
        noteImage.tooltip(getString(R2.string.note))
        contactImage.tooltip(getString(R2.string.contact))
        typeColumn.stringCell { typedType.toString(this@CustomerController) }
        valueColumn.stringCell { value }
    }

    override fun refresh() = ktfx.runLater {
        customerPagination.contentFactoryProperty().bind(buildBinding(searchField.textProperty()) {
            Callback<Pair<Int, Int>, Node> { (page, count) ->
                customerList = CustomerListView().apply {
                    styleClass += R.style.list_view_no_scrollbar_vertical
                    runLater {
                        contextMenu {
                            getString(R2.string.edit)(ImageView(R.image.menu_edit)) {
                                disableProperty().bind(selectionModel.selectedItemProperty().isNull)
                                onAction { edit() }
                            }
                        }
                        deleteContactItem.disableProperty()
                            .bind(contactTable.selectionModel.selectedItemProperty().isNull)
                    }
                    runBlocking {
                        val (pageCount, customers) = withContext(Dispatchers.IO) {
                            OpenPSSApi.getCustomers(searchField.text, page, count)
                        }
                        customerPagination.pageCount = pageCount
                        items = customers.toMutableObservableList()
                    }
                }
                titleProperty().bind(buildStringBinding(customerList.selectionModel.selectedItemProperty()) {
                    customerList.selectionModel.selectedItem?.name
                })
                sinceLabel.bindLabel {
                    customerList.selectionModel.selectedItem?.since?.toString(PATTERN_DATE)
                        .orEmpty()
                }
                addressLabel.bindLabel { customerList.selectionModel.selectedItem?.address ?: "-" }
                noteLabel.bindLabel { customerList.selectionModel.selectedItem?.note ?: "-" }
                contactTable.itemsProperty()
                    .bind(buildBinding(customerList.selectionModel.selectedItemProperty()) {
                        customerList.selectionModel.selectedItem?.contacts?.toObservableList()
                            ?: emptyObservableList()
                    })
                masterDetailPane.showDetailNodeProperty()
                    .bind(customerList.selectionModel.selectedItemProperty().isNotNull)
                customerList
            }
        })
    }

    fun add() = AddCustomerDialog(this).show { customer ->
        customerList.items.add(OpenPSSApi.addCustomer(customer!!))
        customerList.selectionModel.select(customerList.items.lastIndex)
    }

    private fun edit() = EditCustomerDialog(this, customerList.selectionModel.selectedItem).show {
        withPermission {
            OpenPSSApi.editCustomer(login, customerList.selectionModel.selectedItem.id, it!!)
            reload()
        }
    }

    @FXML
    fun addContact() = AddContactPopOver(this).show(contactTable) {
        OpenPSSApi.addContact(customerList.selectionModel.selectedItem.id, it!!)
        reload()
    }

    @FXML
    fun deleteContact() = ConfirmDialog(this, R2.string.delete_contact).show {
        withPermission {
            OpenPSSApi.deleteContact(
                login,
                customerList.selectionModel.selectedItem.id,
                contactTable.selectionModel.selectedItem
            )
            reload()
        }
    }

    private fun Label.bindLabel(target: () -> String) = textProperty()
        .bind(buildStringBinding(customerList.selectionModel.selectedItemProperty()) { target() })

    private fun reload() {
        val index = customerList.selectionModel.selectedIndex
        refresh()
        GlobalScope.launch(Dispatchers.JavaFx) {
            delay(250)
            customerList.selectionModel.clearAndSelect(index)
        }
    }
}
