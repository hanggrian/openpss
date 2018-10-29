package com.hendraanggrian.openpss.ui.customer

import com.hendraanggrian.openpss.App
import com.hendraanggrian.openpss.App.Companion.STRETCH_POINT
import com.hendraanggrian.openpss.PATTERN_DATE
import com.hendraanggrian.openpss.R
import com.hendraanggrian.openpss.control.PaginatedPane
import com.hendraanggrian.openpss.control.space
import com.hendraanggrian.openpss.control.stretchableButton
import com.hendraanggrian.openpss.control.stringCell
import com.hendraanggrian.openpss.db.matches
import com.hendraanggrian.openpss.db.schemas.Customer
import com.hendraanggrian.openpss.db.schemas.Customers
import com.hendraanggrian.openpss.db.transaction
import com.hendraanggrian.openpss.popup.dialog.ConfirmDialog
import com.hendraanggrian.openpss.popup.popover.InputUserPopover
import com.hendraanggrian.openpss.ui.ActionController
import com.hendraanggrian.openpss.ui.Refreshable
import com.hendraanggrian.openpss.ui.Selectable
import com.hendraanggrian.openpss.ui.Selectable2
import com.hendraanggrian.openpss.util.isNotEmpty
import javafx.fxml.FXML
import javafx.scene.Node
import javafx.scene.control.Button
import javafx.scene.control.Label
import javafx.scene.control.ListView
import javafx.scene.control.MenuItem
import javafx.scene.control.SelectionModel
import javafx.scene.control.TableColumn
import javafx.scene.control.TableView
import javafx.scene.control.TextField
import javafx.scene.image.ImageView
import javafx.util.Callback
import kotlinx.coroutines.experimental.Dispatchers
import kotlinx.coroutines.experimental.GlobalScope
import kotlinx.coroutines.experimental.delay
import kotlinx.coroutines.experimental.javafx.JavaFx
import kotlinx.coroutines.experimental.launch
import kotlinx.nosql.update
import ktfx.NodeManager
import ktfx.application.later
import ktfx.beans.binding.bindingOf
import ktfx.beans.binding.stringBindingOf
import ktfx.beans.value.or
import ktfx.collections.emptyObservableList
import ktfx.collections.toMutableObservableList
import ktfx.collections.toObservableList
import ktfx.coroutines.onAction
import ktfx.jfoenix.jfxSnackbar
import ktfx.jfoenix.jfxTextField
import ktfx.layouts.contextMenu
import ktfx.layouts.listView
import ktfx.layouts.tooltip
import org.controlsfx.control.MasterDetailPane
import java.net.URL
import java.util.ResourceBundle
import java.util.regex.Pattern.CASE_INSENSITIVE
import kotlin.math.ceil

class CustomerController : ActionController(), Refreshable, Selectable<Customer>, Selectable2<Customer.Contact> {

    @FXML lateinit var masterDetailPane: MasterDetailPane
    @FXML lateinit var customerPagination: PaginatedPane
    @FXML lateinit var nameLabel: Label
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

    override val selectionModel: SelectionModel<Customer> get() = customerList.selectionModel
    override val selectionModel2: SelectionModel<Customer.Contact> get() = contactTable.selectionModel

    override fun NodeManager.onCreateActions() {
        refreshButton = stretchableButton(STRETCH_POINT, getString(R.string.refresh), ImageView(R.image.act_refresh)) {
            onAction { refresh() }
        }
        addButton = stretchableButton(STRETCH_POINT, getString(R.string.add), ImageView(R.image.act_add)) {
            onAction { add() }
        }
        space(R.dimen.padding_large.toDouble())
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

    override fun refresh() = later {
        customerPagination.contentFactoryProperty().bind(bindingOf(searchField.textProperty()) {
            Callback<Pair<Int, Int>, Node> { (page, count) ->
                customerList = listView {
                    later {
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
                                .take(count).toMutableObservableList()
                            val fullAccess = isAdminProperty()
                            contextMenu {
                                getString(R.string.edit)(ImageView(R.image.menu_edit)) {
                                    disableProperty().bind(!selectedBinding or !fullAccess)
                                    onAction { edit() }
                                }
                            }
                            addContactItem.disableProperty().bind(!selectedBinding)
                            deleteContactItem.disableProperty().bind(!selectedBinding2 or !fullAccess)
                        }
                    }
                }
                nameLabel.bindLabel { selected?.name.orEmpty() }
                noLabel.bindLabel { selected?.no?.toString().orEmpty() }
                sinceLabel.bindLabel { selected?.since?.toString(PATTERN_DATE).orEmpty() }
                addressLabel.bindLabel { selected?.address ?: "-" }
                noteLabel.bindLabel { selected?.note ?: "-" }
                contactTable.itemsProperty().bind(bindingOf(selectedProperty) {
                    selected?.contacts?.toObservableList() ?: emptyObservableList()
                })
                masterDetailPane.showDetailNodeProperty().bind(selectedBinding)
                customerList
            }
        })
    }

    fun add() = InputUserPopover(this, R.string.add_customer).show(addButton) { name ->
        transaction {
            when {
                Customers { it.name.matches("^$name$", CASE_INSENSITIVE) }.isNotEmpty() ->
                    root.jfxSnackbar(getString(R.string.name_taken), App.DURATION_SHORT)
                else -> {
                    Customer.new(name!!).let {
                        it.id = Customers.insert(it)
                        customerList.items.add(it)
                        customerList.selectionModel.select(customerList.items.lastIndex)
                    }
                }
            }
        }
    }

    private fun edit() = EditCustomerDialog(this, selected!!).show {
        transaction {
            Customers[selected!!].projection { name + address + note }.update(it!!.name, it.address, it.note)
        }
        reload()
    }

    @FXML fun addContact() = AddContactPopover(this).show(contactTable) {
        transaction {
            Customers[selected!!].projection { contacts }.update(selected!!.contacts + it!!)
        }
        reload()
    }

    @FXML fun deleteContact() = ConfirmDialog(this, R.string.delete_contact).show {
        transaction {
            Customers[selected!!].projection { contacts }.update(selected!!.contacts - selected2!!)
        }
        reload()
    }

    private fun Label.bindLabel(target: () -> String) = textProperty()
        .bind(stringBindingOf(customerList.selectionModel.selectedItemProperty()) { target() })

    private fun reload() {
        val index = selectedIndex
        refresh()
        GlobalScope.launch(Dispatchers.JavaFx) {
            delay(250)
            reselect(index)
        }
    }
}