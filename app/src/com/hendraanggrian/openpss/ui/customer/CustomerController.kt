package com.hendraanggrian.openpss.ui.customer

import com.hendraanggrian.openpss.R
import com.hendraanggrian.openpss.controls.UserDialog
import com.hendraanggrian.openpss.db.SessionWrapper
import com.hendraanggrian.openpss.db.schemas.Customer
import com.hendraanggrian.openpss.db.schemas.Customers
import com.hendraanggrian.openpss.db.schemas.Employee.Role.MANAGER
import com.hendraanggrian.openpss.db.transaction
import com.hendraanggrian.openpss.io.properties.SettingsFile.CUSTOMER_PAGINATION_ITEMS
import com.hendraanggrian.openpss.ui.Controller
import com.hendraanggrian.openpss.ui.Refreshable
import com.hendraanggrian.openpss.ui.Selectable
import com.hendraanggrian.openpss.ui.Selectable2
import com.hendraanggrian.openpss.util.PATTERN_DATE
import com.hendraanggrian.openpss.util.getStyle
import com.hendraanggrian.openpss.util.isNotEmpty
import com.hendraanggrian.openpss.util.matches
import com.hendraanggrian.openpss.util.stringCell
import com.hendraanggrian.openpss.util.yesNoAlert
import javafx.fxml.FXML
import javafx.scene.Node
import javafx.scene.control.Button
import javafx.scene.control.CheckMenuItem
import javafx.scene.control.Label
import javafx.scene.control.ListView
import javafx.scene.control.Pagination
import javafx.scene.control.SelectionModel
import javafx.scene.control.SplitPane
import javafx.scene.control.TableColumn
import javafx.scene.control.TableView
import javafx.scene.control.TextField
import javafx.scene.image.ImageView
import javafx.scene.layout.Pane
import javafx.scene.text.Font.font
import javafx.util.Callback
import kotlinx.nosql.update
import ktfx.application.later
import ktfx.beans.binding.bindingOf
import ktfx.beans.binding.stringBindingOf
import ktfx.beans.binding.times
import ktfx.beans.property.toReadOnlyProperty
import ktfx.beans.value.or
import ktfx.collections.emptyObservableList
import ktfx.collections.toMutableObservableList
import ktfx.collections.toObservableList
import ktfx.layouts.listView
import ktfx.layouts.tooltip
import ktfx.scene.control.styledErrorAlert
import java.net.URL
import java.util.ResourceBundle
import java.util.regex.Pattern.CASE_INSENSITIVE
import kotlin.math.ceil

class CustomerController : Controller(), Refreshable, Selectable<Customer>, Selectable2<Customer.Contact> {

    @FXML lateinit var editButton: Button
    @FXML lateinit var addContactButton: Button
    @FXML lateinit var deleteContactButton: Button
    @FXML lateinit var searchField: TextField
    @FXML lateinit var filterNameItem: CheckMenuItem
    @FXML lateinit var filterAddressItem: CheckMenuItem
    @FXML lateinit var filterNoteItem: CheckMenuItem
    @FXML lateinit var splitPane: SplitPane
    @FXML lateinit var customerPane: Pane
    @FXML lateinit var customerPagination: Pagination
    @FXML lateinit var nameLabel: Label
    @FXML lateinit var idImage: ImageView
    @FXML lateinit var idLabel: Label
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
    @FXML lateinit var coverLabel: Label

    private lateinit var customerList: ListView<Customer>

    override fun initialize(location: URL, resources: ResourceBundle) {
        super.initialize(location, resources)
        customerPane.minWidthProperty().bind(splitPane.widthProperty() * 0.3)
        nameLabel.font = font(24.0)
        idImage.tooltip(getString(R.string.id))
        sinceImage.tooltip(getString(R.string.since))
        addressImage.tooltip(getString(R.string.address))
        noteImage.tooltip(getString(R.string.note))
        contactImage.tooltip(getString(R.string.contact))
        typeColumn.stringCell { typedType.toString(this@CustomerController) }
        valueColumn.stringCell { value }
    }

    override fun refresh() = customerPagination.pageFactoryProperty().bind(bindingOf(
        searchField.textProperty(),
        filterNameItem.selectedProperty(),
        filterAddressItem.selectedProperty(),
        filterNoteItem.selectedProperty()
    ) {
        Callback<Int, Node> { page ->
            customerList = listView {
                later {
                    transaction {
                        val customers = Customers.buildQuery {
                            if (searchField.text.isNotBlank()) {
                                if (filterNameItem.isSelected) or(it.name.matches(searchField.text, CASE_INSENSITIVE))
                                if (filterAddressItem.isSelected)
                                    or(it.address.matches(searchField.text, CASE_INSENSITIVE))
                                if (filterNoteItem.isSelected) or(it.note.matches(searchField.text, CASE_INSENSITIVE))
                            }
                        }
                        customerPagination.pageCount =
                            ceil(customers.count() / CUSTOMER_PAGINATION_ITEMS.toDouble()).toInt()
                        items = customers
                            .skip(CUSTOMER_PAGINATION_ITEMS * page)
                            .take(CUSTOMER_PAGINATION_ITEMS).toMutableObservableList()
                        val fullAccess = login.isAtLeast(MANAGER).toReadOnlyProperty()
                        editButton.disableProperty().bind(!selectedBinding or !fullAccess)
                        addContactButton.disableProperty().bind(!selectedBinding)
                        deleteContactButton.disableProperty().bind(!selectedBinding2 or !fullAccess)
                    }
                }
            }
            nameLabel.bindLabel { selected?.name.orEmpty() }
            idLabel.bindLabel { selected?.id?.toString().orEmpty() }
            sinceLabel.bindLabel { selected?.since?.toString(PATTERN_DATE).orEmpty() }
            addressLabel.bindLabel { selected?.address ?: "-" }
            noteLabel.bindLabel { selected?.note ?: "-" }
            contactTable.itemsProperty().bind(bindingOf(customerList.selectionModel.selectedItemProperty()) {
                selected?.contacts?.toObservableList() ?: emptyObservableList()
            })
            coverLabel.visibleProperty().bind(customerList.selectionModel.selectedItemProperty().isNull)
            customerList
        }
    })

    override val selectionModel: SelectionModel<Customer> get() = customerList.selectionModel

    override val selectionModel2: SelectionModel<Customer.Contact> get() = contactTable.selectionModel

    @FXML fun addCustomer() = UserDialog(this, R.string.add_customer, R.image.header_customer)
        .showAndWait()
        .ifPresent {
            transaction {
                when {
                    Customers { it.name.matches("^$it$", CASE_INSENSITIVE) }.isNotEmpty() ->
                        styledErrorAlert(getStyle(R.style.openpss), getString(R.string.name_taken)).show()
                    else -> Customer.new(it).let {
                        it.id = Customers.insert(it)
                        customerList.items.add(it)
                        customerList.selectionModel.select(customerList.items.lastIndex)
                    }
                }
            }
        }

    @FXML fun edit() = EditCustomerDialog(this, selected!!).showAndWait().ifPresent {
        transaction {
            Customers[selected!!.id]
                .projection { name + address + note }
                .update(it.name, it.address, it.note)
            reload()
        }
    }

    @FXML fun addContact() = AddContactDialog(this).showAndWait().ifPresent {
        transaction {
            Customers[selected!!].projection { contacts }.update(selected!!.contacts + it)
            reload()
        }
    }

    @FXML fun deleteContact() = yesNoAlert(R.string.delete_contact) {
        transaction {
            Customers[selected!!].projection { contacts }.update(selected!!.contacts - selected2!!)
            reload()
        }
    }

    private fun Label.bindLabel(target: () -> String) = textProperty()
        .bind(stringBindingOf(customerList.selectionModel.selectedItemProperty()) { target() })

    private fun SessionWrapper.reload() = customerList.run {
        items.indexOf(selected!!).let { index ->
            items[index] = Customers[selected!!].single()
            selectionModel.clearAndSelect(index)
        }
    }
}