package com.wijayaprinting.dialogs

import com.wijayaprinting.R
import com.wijayaprinting.core.Resourced
import com.wijayaprinting.nosql.Customer
import com.wijayaprinting.nosql.Customers
import com.wijayaprinting.nosql.transaction
import com.wijayaprinting.util.gap
import com.wijayaprinting.util.with
import javafx.event.ActionEvent.ACTION
import javafx.scene.control.*
import javafx.scene.control.ButtonBar.ButtonData.HELP
import javafx.scene.control.ButtonBar.ButtonData.HELP_2
import javafx.scene.control.ButtonType.CANCEL
import javafx.scene.control.ButtonType.OK
import javafx.scene.image.ImageView
import kotfx.*
import kotlinx.nosql.equal
import kotlinx.nosql.id
import kotlinx.nosql.update

class CustomerDialog(val resourced: Resourced, var prefill: Customer) : Dialog<Customer>(), Resourced by resourced {

    private lateinit var nameField: TextField
    private lateinit var noteArea: TextArea
    private lateinit var tableView: TableView<Customer.Contact>

    init {
        title = prefill.name
        headerText = prefill.name
        graphic = ImageView(R.png.ic_user)
        content = gridPane {
            gap(8)
            label(getString(R.string.name)) col 0 row 0
            nameField = textField(prefill.name) { promptText = getString(R.string.name) } col 1 row 0
            label(getString(R.string.note)) col 0 row 1
            noteArea = textArea(prefill.note) { promptText = getString(R.string.note) } col 1 row 1
            label(getString(R.string.contact)) col 0 row 2
            tableView = tableView<Customer.Contact> {
                prefHeight = 240.0
                columns.addAll(
                        TableColumn<Customer.Contact, String>(R.string.type).apply { setCellValueFactory { it.value.type.asProperty() } },
                        TableColumn<Customer.Contact, String>(R.string.contact).apply { setCellValueFactory { it.value.value.asProperty() } }
                )
                items.addAll(prefill.contacts)
            } col 1 row 2
        }
        button(getString(R.string.add), HELP).addEventFilter(ACTION) {
            it.consume()
            dialog<Customer.Contact>("Add contact", "Add contact", ImageView(R.png.ic_address)) {
                lateinit var typeBox: ChoiceBox<String>
                lateinit var contactField: TextField
                content = gridPane {
                    gap(8)
                    label(getString(R.string.type)) col 0 row 0
                    typeBox = choiceBox(Customer.listAllTypes()) col 1 row 0
                    label(getString(R.string.contact)) col 0 row 1
                    contactField = textField { } col 1 row 1
                }
                button(CANCEL)
                button(OK).disableProperty() bind contactField.textProperty().isEmpty
                setResultConverter { if (it == OK) Customer.Contact(typeBox.value, contactField.text) else null }
            }.showAndWait().ifPresent { contact ->
                transaction { Customers.find { id.equal(prefill.id) }.projection { contacts }.update(prefill.contacts with contact) }
                tableView.items.add(contact)
            }
        }
        button(getString(R.string.delete), HELP_2).apply {
            disableProperty() bind tableView.selectionModel.selectedItemProperty().isNull
            addEventFilter(ACTION) {
                it.consume()
                tableView.items.remove(tableView.selectionModel.selectedItem)
            }
        }
        button(CANCEL)
        button(OK).disableProperty() bind nameField.textProperty().isEmpty
        runFX { nameField.requestFocus() }
        setResultConverter {
            null
            /*when {
                it == CANCEL -> null
                isAdd -> expose {
                    Customer.new {
                        since = now()
                        name = nameField.text
                        email = emailField.text
                        phone1 = phone1Field.text
                        phone2 = phone2Field.text
                        note = noteArea.text
                    }
                }
                else -> expose {
                    prefill!!.apply {
                        name = nameField.text
                        email = emailField.text
                        phone1 = phone1Field.text
                        phone2 = phone2Field.text
                        note = noteArea.text
                    }
                }
            }*/
        }
    }
}