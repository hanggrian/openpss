package com.wijayaprinting.controllers

import com.wijayaprinting.App.Companion.FULL_ACCESS
import com.wijayaprinting.PATTERN_DATE
import com.wijayaprinting.R
import com.wijayaprinting.dialogs.CustomerDialog
import com.wijayaprinting.nosql.Customer
import com.wijayaprinting.nosql.Customers
import com.wijayaprinting.nosql.transaction
import com.wijayaprinting.util.gap
import com.wijayaprinting.util.size
import com.wijayaprinting.util.with
import javafx.fxml.FXML
import javafx.scene.Node
import javafx.scene.control.*
import javafx.scene.control.ButtonType.CANCEL
import javafx.scene.control.ButtonType.OK
import javafx.scene.image.ImageView
import javafx.scene.text.Font.loadFont
import javafx.util.Callback
import kotfx.*
import kotlinx.nosql.equal
import kotlinx.nosql.id
import kotlinx.nosql.update

class CustomerController : Controller() {

    companion object {
        private const val ITEMS_PER_PAGE = 20
    }

    @FXML lateinit var customerField: TextField
    @FXML lateinit var customerPagination: Pagination
    @FXML lateinit var nameLabel: Label
    @FXML lateinit var sinceLabel: Label
    @FXML lateinit var noteLabel: Label
    @FXML lateinit var contactTable: TableView<Customer.Contact>
    @FXML lateinit var typeColumn: TableColumn<Customer.Contact, String>
    @FXML lateinit var contactColumn: TableColumn<Customer.Contact, String>
    @FXML lateinit var coverLabel: Label

    private lateinit var listView: ListView<Customer>
    private val nameLabelGraphic = button(null, ImageView(R.png.btn_edit)) {
        size(24)
        setOnAction {

        }
    }
    private val noteLabelGraphic = button(null, ImageView(R.png.btn_edit)) {
        size(24)
        setOnAction {

        }
    }

    @FXML
    fun initialize() {
        customerPagination.pageFactoryProperty() bind bindingOf(customerField.textProperty()) {
            Callback<Int, Node> { page ->
                listView = listView {
                    runFX {
                        transaction {
                            val customers = if (customerField.text.isBlank()) Customers.find() else Customers.find { name.matches(customerField.text.toPattern()) }
                            items = if (page == 0) customers.take(ITEMS_PER_PAGE).toMutableObservableList() else customers.skip(ITEMS_PER_PAGE * (page - 1)).take(ITEMS_PER_PAGE).toMutableObservableList()
                            customerPagination.pageCount = (customers.count() / ITEMS_PER_PAGE) + 1
                        }
                        nameLabel.textProperty() rebind stringBindingOf(selectionModel.selectedItemProperty()) { selectionModel.selectedItem?.name ?: "" }
                        sinceLabel.textProperty() rebind stringBindingOf(selectionModel.selectedItemProperty()) { selectionModel.selectedItem?.since?.toString(PATTERN_DATE) ?: "" }
                        noteLabel.textProperty() rebind stringBindingOf(selectionModel.selectedItemProperty()) { selectionModel.selectedItem?.note ?: "-" }
                        // contactTable.itemsProperty() rebind bindingOf(selectionModel.selectedItemProperty()) {}
                        coverLabel.visibleProperty() rebind selectionModel.selectedItemProperty().isNull
                    }
                }
                listView
            }
        }

        nameLabel.font = loadFont(latoBold, 24.0)
        nameLabel.graphicProperty() bind bindingOf<Node>(nameLabel.hoverProperty()) { if (nameLabel.isHover && FULL_ACCESS) nameLabelGraphic else null }
        sinceLabel.font = loadFont(latoRegular, 12.0)
        noteLabel.graphicProperty() bind bindingOf<Node>(noteLabel.hoverProperty()) { if (noteLabel.isHover) noteLabelGraphic else null }
        contactTable.contextMenu = contextMenu {
            menuItem(getString(R.string.add)) {
                setOnAction {
                    dialog<Customer.Contact>("Add contact", "Add contact", ImageView(R.png.ic_address)) {
                        lateinit var typeBox: ChoiceBox<String>
                        lateinit var contactField: TextField
                        content = gridPane {
                            gap(8)
                            label(getString(R.string.type)) col 0 row 0
                            typeBox = choiceBox(Customer.listAllTypes()) col 1 row 0
                            label(getString(R.string.contact)) col 0 row 1
                            contactField = textField { promptText = getString(R.string.contact) } col 1 row 1
                        }
                        button(CANCEL)
                        button(OK).disableProperty() bind (typeBox.selectionModel.selectedItemProperty().isNull or contactField.textProperty().isEmpty)
                        setResultConverter { if (it == OK) Customer.Contact(typeBox.value, contactField.text) else null }
                    }.showAndWait().ifPresent { contact ->
                        listView.selectionModel.selectedItem.let { customer ->
                            transaction { Customers.find { id.equal(customer.id) }.projection { contacts }.update(customer.contacts with contact) }
                            contactTable.items.add(contact)
                        }
                    }
                }
            }
            menuItem(getString(R.string.edit)) {
                disableProperty() bind contactTable.selectionModel.selectedItemProperty().isNull
                confirmAlert(R.string.are_you_sure).showAndWait().ifPresent {
                    // transaction { Customers.find { id.equal(listView.selectionModel.selectedItem) }.remove() }
                    // contactTable.items.remove()
                }
            }
        }
    }

    @FXML fun clearOnAction() {
        customerField.text = ""
    }

    @FXML
    fun addOnAction() = inputDialog {
        title = getString(R.string.add_customer)
        headerText = getString(R.string.add_customer)
        graphic = ImageView(R.png.ic_user)
        contentText = getString(R.string.name)
    }.showAndWait().ifPresent { name ->
        val customer = Customer(name)
        customer.id = transaction { Customers.insert(Customer(name)) }!!
        CustomerDialog(this, customer).showAndWait().ifPresent { customerField.text = name }
    }
}