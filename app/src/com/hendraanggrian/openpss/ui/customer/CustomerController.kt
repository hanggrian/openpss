package com.hendraanggrian.openpss.ui.customer

import com.hendraanggrian.openpss.R
import com.hendraanggrian.openpss.collections.isNotEmpty
import com.hendraanggrian.openpss.db.schema.Customer
import com.hendraanggrian.openpss.db.schema.Customer.Contact
import com.hendraanggrian.openpss.db.schema.Customers
import com.hendraanggrian.openpss.db.transaction
import com.hendraanggrian.openpss.scene.control.CountBox
import com.hendraanggrian.openpss.time.PATTERN_DATE
import com.hendraanggrian.openpss.ui.AddUserDialog
import com.hendraanggrian.openpss.ui.Addable
import com.hendraanggrian.openpss.ui.Controller
import com.hendraanggrian.openpss.ui.Refreshable
import com.hendraanggrian.openpss.util.getResourceString
import com.hendraanggrian.openpss.util.tidy
import javafx.fxml.FXML
import javafx.scene.Node
import javafx.scene.control.ButtonType.OK
import javafx.scene.control.ChoiceBox
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
import kotlinx.nosql.id
import kotlinx.nosql.mongodb.MongoDBSession
import kotlinx.nosql.update
import ktfx.application.later
import ktfx.beans.binding.bindingOf
import ktfx.beans.binding.booleanBindingOf
import ktfx.beans.binding.or
import ktfx.beans.binding.stringBindingOf
import ktfx.beans.property.toProperty
import ktfx.collections.emptyObservableList
import ktfx.collections.toMutableObservableList
import ktfx.collections.toObservableList
import ktfx.coroutines.onAction
import ktfx.layouts.button
import ktfx.layouts.choiceBox
import ktfx.layouts.contextMenu
import ktfx.layouts.gridPane
import ktfx.layouts.label
import ktfx.layouts.listView
import ktfx.layouts.menuItem
import ktfx.layouts.textField
import ktfx.scene.control.cancelButton
import ktfx.scene.control.confirmAlert
import ktfx.scene.control.dialog
import ktfx.scene.control.errorAlert
import ktfx.scene.control.inputDialog
import ktfx.scene.control.okButton
import ktfx.scene.layout.gaps
import ktfx.scene.layout.sizeMax
import java.net.URL
import java.util.ResourceBundle
import kotlin.math.ceil
import kotlin.text.RegexOption.IGNORE_CASE

class CustomerController : Controller(), Refreshable, Addable {

    @FXML lateinit var customerField: TextField
    @FXML lateinit var countBox: CountBox
    @FXML lateinit var customerPagination: Pagination
    @FXML lateinit var nameLabel: Label
    @FXML lateinit var sinceLabel: Label
    @FXML lateinit var noteLabel: Label
    @FXML lateinit var contactTable: TableView<Customer.Contact>
    @FXML lateinit var typeColumn: TableColumn<Customer.Contact, String>
    @FXML lateinit var contactColumn: TableColumn<Customer.Contact, String>
    @FXML lateinit var coverLabel: Label

    private lateinit var customerList: ListView<Customer>
    private val noteLabelGraphic = button(graphic = ImageView(R.image.btn_edit)) {
        sizeMax = 24
        onAction {
            inputDialog(getString(R.string.edit_customer), ImageView(R.image.ic_user), customer!!.note) {
                contentText = getString(R.string.note)
            }.showAndWait().ifPresent { note ->
                transaction {
                    Customers.find { id.equal(customer!!.id) }.projection { this.note }.update(note)
                    reload(customer!!)
                }
            }
        }
    }

    override fun initialize(location: URL, resources: ResourceBundle) {
        super.initialize(location, resources)
        refresh()

        countBox.desc = getString(R.string.items)
        nameLabel.font = loadFont(getResourceString(R.font.opensans_bold), 24.0)
        sinceLabel.font = loadFont(getResourceString(R.font.opensans_regular), 12.0)
        noteLabel.graphicProperty().bind(bindingOf<Node>(noteLabel.hoverProperty()) {
            if (noteLabel.isHover) noteLabelGraphic else null
        })
        contactTable.contextMenu {
            menuItem(getString(R.string.add)) {
                onAction {
                    dialog<Customer.Contact>(getString(R.string.add_contact), ImageView(R.image.ic_address)) {
                        lateinit var typeChoice: ChoiceBox<String>
                        lateinit var contactField: TextField
                        dialogPane.content = gridPane {
                            gaps = 8
                            label(getString(R.string.type)) col 0 row 0
                            typeChoice = choiceBox(Customer.Contact.listTypes()) col 1 row 0
                            label(getString(R.string.contact)) col 0 row 1
                            contactField = textField { promptText = getString(R.string.contact) } col 1 row 1
                        }
                        cancelButton()
                        okButton {
                            disableProperty().bind(typeChoice.valueProperty().isNull or
                                contactField.textProperty().isEmpty)
                        }
                        setResultConverter { if (it == OK) Contact(typeChoice.value, contactField.text) else null }
                    }.showAndWait().ifPresent { contact ->
                        transaction {
                            Customers.find { id.equal(customer!!.id) }.projection { contacts }
                                .update(customer!!.contacts + contact)
                            reload(customer!!)
                        }
                    }
                }
            }
            menuItem(getString(R.string.delete)) {
                later {
                    disableProperty().bind(booleanBindingOf(contactTable.selectionModel.selectedItemProperty()) {
                        contact == null || !isFullAccess
                    })
                }
                onAction {
                    confirmAlert(getString(R.string.delete_contact)).showAndWait().ifPresent {
                        transaction {
                            Customers.find { id.equal(customer!!.id) }.projection { contacts }
                                .update(customer!!.contacts - contact!!)
                            reload(customer!!)
                        }
                    }
                }
            }
        }
        typeColumn.setCellValueFactory { it.value.type.toProperty() }
        contactColumn.setCellValueFactory { it.value.value.toProperty() }
    }

    override fun refresh() = customerPagination.pageFactoryProperty()
        .bind(bindingOf(customerField.textProperty(), countBox.countProperty) {
            Callback<Int, Node> { page ->
                customerList = listView {
                    later {
                        transaction {
                            val customers = when {
                                customerField.text.isBlank() -> Customers.find()
                                else -> Customers.find {
                                    name.matches(customerField.text.toRegex(IGNORE_CASE).toPattern())
                                }
                            }
                            customerPagination.pageCount = ceil(customers.count() / countBox.count.toDouble()).toInt()
                            items = customers.skip(countBox.count * page).take(countBox.count).toMutableObservableList()
                        }
                    }
                }
                nameLabel.textProperty().bind(stringBindingOf(customerList.selectionModel.selectedItemProperty()) {
                    customer?.name ?: ""
                })
                sinceLabel.textProperty().bind(stringBindingOf(customerList.selectionModel.selectedItemProperty()) {
                    customer?.since?.toString(PATTERN_DATE) ?: ""
                })
                noteLabel.textProperty().bind(stringBindingOf(customerList.selectionModel.selectedItemProperty()) {
                    customer?.note ?: ""
                })
                contactTable.itemsProperty().bind(bindingOf(customerList.selectionModel.selectedItemProperty()) {
                    customer?.contacts?.toObservableList() ?: emptyObservableList()
                })
                coverLabel.visibleProperty().bind(customerList.selectionModel.selectedItemProperty().isNull)
                customerList
            }
        })

    override fun add() = AddUserDialog(this, getString(R.string.add_customer)).showAndWait().ifPresent { name ->
        transaction {
            when {
                Customers.find { this.name.equal(name) }.isNotEmpty() ->
                    errorAlert(getString(R.string.name_taken)).showAndWait()
                else -> {
                    val customer = Customer(name.tidy())
                    customer.id = Customers.insert(customer)
                    customerList.items.add(0, customer)
                    customerList.selectionModel.selectFirst()
                }
            }
        }
    }

    private inline val customer: Customer? get() = customerList.selectionModel.selectedItem

    private inline val contact: Customer.Contact? get() = contactTable.selectionModel.selectedItem

    private fun MongoDBSession.reload(customer: Customer) = customerList.items.indexOf(customer).let { index ->
        customerList.items[customerList.items.indexOf(customer)] = Customers.find { id.equal(customer.id) }.single()
        customerList.selectionModel.select(index)
    }
}