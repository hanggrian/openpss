package com.hendraanggrian.openpss.ui.customer

import com.hendraanggrian.openpss.R
import com.hendraanggrian.openpss.collections.isNotEmpty
import com.hendraanggrian.openpss.db.buildQuery
import com.hendraanggrian.openpss.db.findByDoc
import com.hendraanggrian.openpss.db.schema.Contact
import com.hendraanggrian.openpss.db.schema.Customer
import com.hendraanggrian.openpss.db.schema.Customers
import com.hendraanggrian.openpss.db.transaction
import com.hendraanggrian.openpss.scene.control.CountBox
import com.hendraanggrian.openpss.time.PATTERN_DATE
import com.hendraanggrian.openpss.ui.AddUserDialog
import com.hendraanggrian.openpss.ui.Controller
import com.hendraanggrian.openpss.ui.Refreshable
import com.hendraanggrian.openpss.utils.getResourceString
import com.hendraanggrian.openpss.utils.stringCell
import com.hendraanggrian.openpss.utils.yesNoAlert
import javafx.fxml.FXML
import javafx.scene.Node
import javafx.scene.control.Button
import javafx.scene.control.Label
import javafx.scene.control.ListView
import javafx.scene.control.Pagination
import javafx.scene.control.TableColumn
import javafx.scene.control.TableView
import javafx.scene.control.TextField
import javafx.scene.image.ImageView
import javafx.scene.text.Font.loadFont
import javafx.util.Callback
import kotlinx.nosql.equal
import kotlinx.nosql.mongodb.MongoDBSession
import kotlinx.nosql.update
import ktfx.application.later
import ktfx.beans.binding.bindingOf
import ktfx.beans.binding.booleanBindingOf
import ktfx.beans.binding.stringBindingOf
import ktfx.collections.emptyObservableList
import ktfx.collections.toMutableObservableList
import ktfx.collections.toObservableList
import ktfx.coroutines.onAction
import ktfx.layouts.button
import ktfx.layouts.contextMenu
import ktfx.layouts.listView
import ktfx.scene.control.errorAlert
import ktfx.scene.control.inputDialog
import ktfx.scene.layout.maxSize
import java.net.URL
import java.util.ResourceBundle
import java.util.regex.Pattern.CASE_INSENSITIVE
import kotlin.math.ceil

class CustomerController : Controller(), Refreshable {

    @FXML lateinit var addContactButton: Button
    @FXML lateinit var customerField: TextField
    @FXML lateinit var countBox: CountBox
    @FXML lateinit var customerPagination: Pagination
    @FXML lateinit var nameLabel: Label
    @FXML lateinit var sinceLabel: Label
    @FXML lateinit var noteLabel: Label
    @FXML lateinit var contactTable: TableView<Contact>
    @FXML lateinit var typeColumn: TableColumn<Contact, String>
    @FXML lateinit var contactColumn: TableColumn<Contact, String>
    @FXML lateinit var coverLabel: Label

    private lateinit var customerList: ListView<Customer>
    private val noteLabelGraphic = button(graphic = ImageView(R.image.btn_edit)) {
        maxSize = 24.0
        onAction {
            inputDialog(getString(R.string.edit_customer), ImageView(R.image.ic_user), customer!!.note) {
                contentText = getString(R.string.note)
            }.showAndWait().ifPresent { note ->
                transaction {
                    findByDoc(Customers, customer!!).projection { this.note }.update(note)
                    reload(customer!!)
                }
            }
        }
    }

    override fun initialize(location: URL, resources: ResourceBundle) {
        super.initialize(location, resources)

        countBox.desc = getString(R.string.items)
        nameLabel.font = loadFont(getResourceString(R.font.opensans_bold), 24.0)
        sinceLabel.font = loadFont(getResourceString(R.font.opensans_regular), 12.0)
        noteLabel.graphicProperty().bind(bindingOf<Node>(noteLabel.hoverProperty()) {
            if (noteLabel.isHover) noteLabelGraphic else null
        })
        contactTable.contextMenu {
            (getString(R.string.add)) { onAction { addContact() } }
            (getString(R.string.delete)) {
                later {
                    disableProperty().bind(booleanBindingOf(contactTable.selectionModel.selectedItemProperty()) {
                        contact == null || !isFullAccess
                    })
                }
                onAction {
                    yesNoAlert(getString(R.string.delete_contact)) {
                        transaction {
                            findByDoc(Customers, customer!!).projection { contacts }
                                .update(customer!!.contacts - contact!!)
                            reload(customer!!)
                        }
                    }
                }
            }
        }
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
                                        and(name.matches(customerField.text.toPattern(CASE_INSENSITIVE)))
                                }
                            }
                            customerPagination.pageCount = ceil(customers.count() / countBox.count.toDouble()).toInt()
                            items = customers.skip(countBox.count * page).take(countBox.count).toMutableObservableList()
                        }
                    }
                }
                addContactButton.disableProperty().bind(customerList.selectionModel.selectedItemProperty().isNull)
                nameLabel.bindLabel { customer?.name ?: "" }
                sinceLabel.bindLabel { customer?.since?.toString(PATTERN_DATE) ?: "" }
                noteLabel.bindLabel { customer?.note ?: "" }
                contactTable.itemsProperty().bind(bindingOf(customerList.selectionModel.selectedItemProperty()) {
                    customer?.contacts?.toObservableList() ?: emptyObservableList()
                })
                coverLabel.visibleProperty().bind(customerList.selectionModel.selectedItemProperty().isNull)
                customerList
            }
        })

    @FXML fun addCustomer() = AddUserDialog(this, getString(R.string.add_customer)).showAndWait().ifPresent {
        transaction {
            when {
                Customers.find { name.equal(it) }.isNotEmpty() ->
                    errorAlert(getString(R.string.name_taken)).showAndWait()
                else -> Customer.new(it).let {
                    it.id = Customers.insert(it)
                    customerList.items.add(it)
                    customerList.selectionModel.selectFirst()
                }
            }
        }
    }

    @FXML fun addContact() = AddContactDialog(this).showAndWait().ifPresent {
        transaction {
            findByDoc(Customers, customer!!).projection { contacts }.update(customer!!.contacts + it)
            reload(customer!!)
        }
    }

    private inline val customer: Customer? get() = customerList.selectionModel.selectedItem

    private inline val contact: Contact? get() = contactTable.selectionModel.selectedItem

    private fun Label.bindLabel(target: () -> String) = textProperty()
        .bind(stringBindingOf(customerList.selectionModel.selectedItemProperty()) { target() })

    private fun MongoDBSession.reload(customer: Customer) = customerList.run {
        items.indexOf(customer).let { index ->
            items[items.indexOf(customer)] = findByDoc(Customers, customer).single()
            selectionModel.select(index)
        }
    }
}