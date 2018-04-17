package com.hendraanggrian.openpss.ui.customer

import com.hendraanggrian.openpss.R
import com.hendraanggrian.openpss.controls.UserDialog
import com.hendraanggrian.openpss.db.buildQuery
import com.hendraanggrian.openpss.db.schemas.Customer
import com.hendraanggrian.openpss.db.schemas.Customer.ContactType.PHONE
import com.hendraanggrian.openpss.db.schemas.Customers
import com.hendraanggrian.openpss.db.transaction
import com.hendraanggrian.openpss.io.properties.SettingsFile.CUSTOMER_PAGINATION_ITEMS
import com.hendraanggrian.openpss.time.PATTERN_DATE
import com.hendraanggrian.openpss.ui.Controller
import com.hendraanggrian.openpss.ui.Refreshable
import com.hendraanggrian.openpss.ui.Selectable
import com.hendraanggrian.openpss.ui.Selectable2
import com.hendraanggrian.openpss.utils.findByDoc
import com.hendraanggrian.openpss.utils.getFont
import com.hendraanggrian.openpss.utils.isNotEmpty
import com.hendraanggrian.openpss.utils.matches
import com.hendraanggrian.openpss.utils.stringCell
import com.hendraanggrian.openpss.utils.yesNoAlert
import javafx.fxml.FXML
import javafx.scene.Node
import javafx.scene.control.Button
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
import javafx.util.Callback
import kotlinx.nosql.mongodb.MongoDBSession
import kotlinx.nosql.update
import ktfx.application.later
import ktfx.beans.binding.bindingOf
import ktfx.beans.binding.stringBindingOf
import ktfx.beans.binding.times
import ktfx.beans.property.toReadOnlyProperty
import ktfx.beans.value.or
import ktfx.collections.toMutableObservableList
import ktfx.layouts.listView
import ktfx.scene.control.errorAlert
import ktfx.scene.control.inputDialog
import ktfx.scene.layout.paddingLeft
import java.net.URL
import java.util.ResourceBundle
import java.util.regex.Pattern.CASE_INSENSITIVE
import kotlin.math.ceil

class CustomerController : Controller(), Refreshable, Selectable<Customer>, Selectable2<Contact> {

    @FXML lateinit var editNameButton: Button
    @FXML lateinit var editAddressButton: Button
    @FXML lateinit var editNoteButton: Button
    @FXML lateinit var addContactButton: Button
    @FXML lateinit var deleteContactButton: Button
    @FXML lateinit var searchField: TextField
    @FXML lateinit var splitPane: SplitPane
    @FXML lateinit var customerPane: Pane
    @FXML lateinit var detailPane: Pane
    @FXML lateinit var customerPagination: Pagination
    @FXML lateinit var nameLabel: Label
    @FXML lateinit var sinceLabel1: Label
    @FXML lateinit var sinceLabel2: Label
    @FXML lateinit var addressLabel1: Label
    @FXML lateinit var addressLabel2: Label
    @FXML lateinit var noteLabel1: Label
    @FXML lateinit var noteLabel2: Label
    @FXML lateinit var contactLabel: Label
    @FXML lateinit var contactTable: TableView<Contact>
    @FXML lateinit var typeColumn: TableColumn<Contact, String>
    @FXML lateinit var valueColumn: TableColumn<Contact, String>
    @FXML lateinit var coverLabel: Label

    private lateinit var customerList: ListView<Customer>

    override fun initialize(location: URL, resources: ResourceBundle) {
        super.initialize(location, resources)
        customerPane.minWidthProperty().bind(splitPane.widthProperty() * 0.3)
        detailPane.minWidthProperty().bind(splitPane.widthProperty() * 0.3)
        nameLabel.font = getFont(R.font.opensans_bold, 24)
        sinceLabel1.font = getFont(R.font.opensans_bold)
        addressLabel1.font = getFont(R.font.opensans_bold)
        noteLabel1.font = getFont(R.font.opensans_bold)
        contactLabel.font = getFont(R.font.opensans_bold)
        typeColumn.stringCell { type.asString(this@CustomerController) }
        valueColumn.stringCell { value }
    }

    override fun refresh() = customerPagination.pageFactoryProperty().bind(bindingOf(searchField.textProperty()) {
        Callback<Int, Node> { page ->
            customerList = listView {
                later {
                    transaction {
                        val customers = Customers.find {
                            buildQuery {
                                if (searchField.text.isNotBlank())
                                    and(name.matches(searchField.text, CASE_INSENSITIVE))
                            }
                        }
                        customerPagination.pageCount =
                            ceil(customers.count() / CUSTOMER_PAGINATION_ITEMS.toDouble()).toInt()
                        items = customers
                            .skip(CUSTOMER_PAGINATION_ITEMS * page)
                            .take(CUSTOMER_PAGINATION_ITEMS).toMutableObservableList()
                    }
                }
            }
            later {
                editNameButton.disableProperty().bind(!selectedBinding or !isFullAccess.toReadOnlyProperty())
                editAddressButton.disableProperty().bind(!selectedBinding)
                editNoteButton.disableProperty().bind(!selectedBinding)
                addContactButton.disableProperty().bind(!selectedBinding)
                deleteContactButton.disableProperty().bind(!selectedBinding2 or !isFullAccess.toReadOnlyProperty())
            }
            nameLabel.bindLabel { selected?.name.orEmpty() }
            sinceLabel2.bindLabel { selected?.since?.toString(PATTERN_DATE).orEmpty() }
            addressLabel2.bindLabel { selected?.address.orEmpty() }
            noteLabel2.bindLabel { selected?.note.orEmpty() }
            contactTable.itemsProperty().bind(bindingOf(customerList.selectionModel.selectedItemProperty()) {
                Contact.listAll(selected)
            })
            coverLabel.visibleProperty().bind(customerList.selectionModel.selectedItemProperty().isNull)
            customerList
        }
    })

    override val selectionModel: SelectionModel<Customer> get() = customerList.selectionModel

    override val selectionModel2: SelectionModel<Contact> get() = contactTable.selectionModel

    @FXML fun addCustomer() = UserDialog(this, R.string.add_customer, R.image.ic_customer)
        .showAndWait()
        .ifPresent {
            transaction {
                when {
                    Customers.find { name.matches("^$it$", CASE_INSENSITIVE) }.isNotEmpty() ->
                        errorAlert(getString(R.string.name_taken)).show()
                    else -> Customers.new(it).let {
                        it.id = Customers.insert(it)
                        customerList.items.add(it)
                        customerList.selectionModel.select(customerList.items.lastIndex)
                    }
                }
            }
        }

    @FXML fun editName() = UserDialog(this, R.string.edit_name, R.image.ic_customer, selected!!.name)
        .showAndWait()
        .ifPresent {
            transaction {
                findByDoc(Customers, selected!!).projection { name }.update(it)
                reload(selected!!)
            }
        }

    @FXML fun editAddress() = inputDialog(getString(R.string.edit_address), ImageView(R.image.ic_customer)) {
        contentText = getString(R.string.address)
    }.showAndWait().ifPresent {
        transaction {
            findByDoc(Customers, selected!!).projection { address }.update(it)
            reload(selected!!)
        }
    }

    @FXML fun editNote() = inputDialog(getString(R.string.edit_note), ImageView(R.image.ic_customer)) {
        contentText = getString(R.string.note)
    }.showAndWait().ifPresent {
        transaction {
            findByDoc(Customers, selected!!).projection { note }.update(it)
            reload(selected!!)
        }
    }

    @FXML fun addContact() = AddContactDialog(this).showAndWait().ifPresent {
        transaction {
            val (type, value) = it
            findByDoc(Customers, selected!!).projection {
                when (type) {
                    PHONE -> phones
                    else -> emails
                }
            }.update(when (type) {
                PHONE -> selected!!.phones
                else -> selected!!.emails
            } + value)
            reload(selected!!)
        }
    }

    @FXML fun deleteContact() = yesNoAlert(R.string.delete_contact) {
        transaction {
            findByDoc(Customers, selected!!).projection {
                when (selected2!!.type) {
                    PHONE -> phones
                    else -> emails
                }
            }.update(when (selected2!!.type) {
                PHONE -> selected!!.phones
                else -> selected!!.emails
            } - selected2!!.value)
            reload(selected!!)
        }
    }

    private fun Label.bindLabel(target: () -> String) = textProperty()
        .bind(stringBindingOf(customerList.selectionModel.selectedItemProperty()) { target() })

    private fun MongoDBSession.reload(customer: Customer) = customerList.run {
        items.indexOf(customer).let { index ->
            items[index] = findByDoc(Customers, customer).single()
            selectionModel.select(index)
        }
    }
}