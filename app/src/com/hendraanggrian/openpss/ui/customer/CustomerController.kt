package com.hendraanggrian.openpss.ui.customer

import com.hendraanggrian.openpss.R
import com.hendraanggrian.openpss.db.buildQuery
import com.hendraanggrian.openpss.db.schemas.Customer
import com.hendraanggrian.openpss.db.schemas.Customer.Contact
import com.hendraanggrian.openpss.db.schemas.Customers
import com.hendraanggrian.openpss.db.transaction
import com.hendraanggrian.openpss.scene.control.CountBox
import com.hendraanggrian.openpss.time.PATTERN_DATE
import com.hendraanggrian.openpss.ui.Controller
import com.hendraanggrian.openpss.ui.Refreshable
import com.hendraanggrian.openpss.ui.UserDialog
import com.hendraanggrian.openpss.utils.findByDoc
import com.hendraanggrian.openpss.utils.getFont
import com.hendraanggrian.openpss.utils.isNotEmpty
import com.hendraanggrian.openpss.utils.matches
import com.hendraanggrian.openpss.utils.stringCell
import com.hendraanggrian.openpss.utils.yesNoAlert
import javafx.beans.binding.BooleanBinding
import javafx.fxml.FXML
import javafx.scene.Node
import javafx.scene.control.Button
import javafx.scene.control.Label
import javafx.scene.control.ListView
import javafx.scene.control.Pagination
import javafx.scene.control.SplitPane
import javafx.scene.control.TableColumn
import javafx.scene.control.TableView
import javafx.scene.control.TextField
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
import ktfx.collections.emptyObservableList
import ktfx.collections.toMutableObservableList
import ktfx.collections.toObservableList
import ktfx.layouts.listView
import ktfx.scene.control.errorAlert
import java.net.URL
import java.util.ResourceBundle
import java.util.regex.Pattern.CASE_INSENSITIVE
import kotlin.math.ceil

class CustomerController : Controller(), Refreshable {

    @FXML lateinit var editNameButton: Button
    @FXML lateinit var editNoteButton: Button
    @FXML lateinit var addContactButton: Button
    @FXML lateinit var deleteContactButton: Button
    @FXML lateinit var customerField: TextField
    @FXML lateinit var countBox: CountBox
    @FXML lateinit var splitPane: SplitPane
    @FXML lateinit var customerPane: Pane
    @FXML lateinit var detailPane: Pane
    @FXML lateinit var customerPagination: Pagination
    @FXML lateinit var nameLabel: Label
    @FXML lateinit var sinceLabel: Label
    @FXML lateinit var noteLabel: Label
    @FXML lateinit var contactTable: TableView<Contact>
    @FXML lateinit var typeColumn: TableColumn<Contact, String>
    @FXML lateinit var contactColumn: TableColumn<Contact, String>
    @FXML lateinit var coverLabel: Label

    private lateinit var customerList: ListView<Customer>

    override fun initialize(location: URL, resources: ResourceBundle) {
        super.initialize(location, resources)
        customerPane.minWidthProperty().bind(splitPane.widthProperty() * 0.3)
        detailPane.minWidthProperty().bind(splitPane.widthProperty() * 0.3)

        countBox.desc = getString(R.string.items)
        nameLabel.font = getFont(R.font.opensans_bold, 24)
        sinceLabel.font = getFont(R.font.opensans_regular, 12)
        typeColumn.stringCell { type }
        contactColumn.stringCell { value }
    }

    override fun refresh() = customerPagination.pageFactoryProperty()
        .bind(bindingOf(customerField.textProperty(), countBox.countProperty) {
            Callback<Int, Node> { page ->
                customerList = listView {
                    later {
                        transaction {
                            val customers = Customers.find {
                                buildQuery {
                                    if (customerField.text.isNotBlank())
                                        and(name.matches(customerField.text, CASE_INSENSITIVE))
                                }
                            }
                            customerPagination.pageCount = ceil(customers.count() / countBox.count.toDouble()).toInt()
                            items = customers.skip(countBox.count * page).take(countBox.count).toMutableObservableList()
                        }
                    }
                }
                later {
                    editNameButton.disableProperty().bind(customerSelectedBinding or !isFullAccess.toReadOnlyProperty())
                    editNoteButton.disableProperty().bind(customerSelectedBinding)
                    addContactButton.disableProperty().bind(customerSelectedBinding)
                    deleteContactButton.disableProperty().bind(contactSelectedBinding or
                        !isFullAccess.toReadOnlyProperty())
                }
                nameLabel.bindLabel { customer?.name.orEmpty() }
                sinceLabel.bindLabel { customer?.since?.toString(PATTERN_DATE).orEmpty() }
                noteLabel.bindLabel { customer?.note.orEmpty() }
                contactTable.itemsProperty().bind(bindingOf(customerList.selectionModel.selectedItemProperty()) {
                    customer?.contacts?.toObservableList() ?: emptyObservableList()
                })
                coverLabel.visibleProperty().bind(customerList.selectionModel.selectedItemProperty().isNull)
                customerList
            }
        })

    @FXML fun addCustomer() = UserDialog(this, R.string.add_customer, R.image.ic_customer)
        .showAndWait()
        .ifPresent {
            transaction {
                when {
                    Customers.find { name.matches("^$it$", CASE_INSENSITIVE) }.isNotEmpty() ->
                        errorAlert(getString(R.string.name_taken)).show()
                    else -> Customer.new(it).let {
                        it.id = Customers.insert(it)
                        customerList.items.add(it)
                        customerList.selectionModel.selectFirst()
                    }
                }
            }
        }

    @FXML fun editName() = UserDialog(this, R.string.edit_name, R.image.ic_customer, customer!!.name)
        .showAndWait()
        .ifPresent {
            transaction {
                findByDoc(Customers, customer!!).projection { name }.update(it)
                reload(customer!!)
            }
        }

    @FXML fun editNote() = EditNoteDialog(this, customer!!.note).showAndWait().ifPresent {
        transaction {
            findByDoc(Customers, customer!!).projection { note }.update(it)
            reload(customer!!)
        }
    }

    @FXML fun addContact() = AddContactDialog(this).showAndWait().ifPresent {
        transaction {
            findByDoc(Customers, customer!!).projection { contacts }.update(customer!!.contacts + it)
            reload(customer!!)
        }
    }

    @FXML fun deleteContact() = yesNoAlert(R.string.delete_contact) {
        transaction {
            findByDoc(Customers, customer!!).projection { contacts }.update(customer!!.contacts - contact!!)
            reload(customer!!)
        }
    }

    private inline val customer: Customer? get() = customerList.selectionModel.selectedItem

    private inline val customerSelectedBinding: BooleanBinding
        get() = customerList.selectionModel.selectedItemProperty().isNull

    private inline val contact: Contact? get() = contactTable.selectionModel.selectedItem

    private inline val contactSelectedBinding: BooleanBinding
        get() = contactTable.selectionModel.selectedItemProperty().isNull

    private fun Label.bindLabel(target: () -> String) = textProperty()
        .bind(stringBindingOf(customerList.selectionModel.selectedItemProperty()) { target() })

    private fun MongoDBSession.reload(customer: Customer) = customerList.run {
        items.indexOf(customer).let { index ->
            items[index] = findByDoc(Customers, customer).single()
            selectionModel.select(index)
        }
    }
}