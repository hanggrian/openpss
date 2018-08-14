package com.hendraanggrian.openpss.ui.customer

import com.hendraanggrian.openpss.App.Companion.STYLE_DEFAULT_BUTTON
import com.hendraanggrian.openpss.App.Companion.STYLE_SEARCH_TEXTFIELD
import com.hendraanggrian.openpss.R
import com.hendraanggrian.openpss.control.PaginatedPane
import com.hendraanggrian.openpss.control.popover.InputUserPopover
import com.hendraanggrian.openpss.control.stretchableButton
import com.hendraanggrian.openpss.control.styledStretchableButton
import com.hendraanggrian.openpss.db.schemas.Customer
import com.hendraanggrian.openpss.db.schemas.Customers
import com.hendraanggrian.openpss.db.transaction
import com.hendraanggrian.openpss.layout.SegmentedTabPane.Companion.STRETCH_POINT
import com.hendraanggrian.openpss.ui.Refreshable
import com.hendraanggrian.openpss.ui.SegmentedController
import com.hendraanggrian.openpss.ui.Selectable
import com.hendraanggrian.openpss.ui.Selectable2
import com.hendraanggrian.openpss.util.PATTERN_DATE
import com.hendraanggrian.openpss.util.getStyle
import com.hendraanggrian.openpss.util.isNotEmpty
import com.hendraanggrian.openpss.util.matches
import com.hendraanggrian.openpss.util.stringCell
import com.hendraanggrian.openpss.util.yesNoAlert
import javafx.fxml.FXML
import javafx.geometry.Orientation.VERTICAL
import javafx.scene.Node
import javafx.scene.control.Button
import javafx.scene.control.CheckMenuItem
import javafx.scene.control.Label
import javafx.scene.control.ListView
import javafx.scene.control.MenuButton
import javafx.scene.control.MenuItem
import javafx.scene.control.SelectionModel
import javafx.scene.control.TableColumn
import javafx.scene.control.TableView
import javafx.scene.control.TextField
import javafx.scene.image.ImageView
import javafx.util.Callback
import javafxx.application.later
import javafxx.beans.binding.bindingOf
import javafxx.beans.binding.stringBindingOf
import javafxx.beans.binding.times
import javafxx.beans.property.toProperty
import javafxx.beans.value.or
import javafxx.collections.emptyObservableList
import javafxx.collections.toMutableObservableList
import javafxx.collections.toObservableList
import javafxx.coroutines.FX
import javafxx.coroutines.onAction
import javafxx.layouts.checkMenuItem
import javafxx.layouts.listView
import javafxx.layouts.menuButton
import javafxx.layouts.separator
import javafxx.layouts.styledTextField
import javafxx.layouts.tooltip
import javafxx.scene.control.styledErrorAlert
import kotlinx.coroutines.experimental.delay
import kotlinx.coroutines.experimental.launch
import kotlinx.nosql.update
import org.controlsfx.control.MasterDetailPane
import java.net.URL
import java.util.ResourceBundle
import java.util.regex.Pattern.CASE_INSENSITIVE
import kotlin.math.ceil

class CustomerController : SegmentedController(), Refreshable, Selectable<Customer>, Selectable2<Customer.Contact> {

    @FXML lateinit var masterDetailPane: MasterDetailPane
    @FXML lateinit var customerPagination: PaginatedPane
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
    @FXML lateinit var addContactItem: MenuItem
    @FXML lateinit var deleteContactItem: MenuItem

    private lateinit var refreshButton: Button
    private lateinit var addButton: Button
    private lateinit var editButton: Button
    override val leftButtons: List<Node> get() = listOf(refreshButton, separator(VERTICAL), addButton, editButton)

    private lateinit var searchField: TextField
    private lateinit var filterMenu: MenuButton
    private lateinit var filterNameItem: CheckMenuItem
    private lateinit var filterAddressItem: CheckMenuItem
    private lateinit var filterNoteItem: CheckMenuItem
    override val rightButtons: List<Node> get() = listOf(searchField, filterMenu)

    private lateinit var customerList: ListView<Customer>

    override val selectionModel: SelectionModel<Customer> get() = customerList.selectionModel
    override val selectionModel2: SelectionModel<Customer.Contact> get() = contactTable.selectionModel

    override fun initialize(location: URL, resources: ResourceBundle) {
        super.initialize(location, resources)
        refreshButton = stretchableButton(STRETCH_POINT, getString(R.string.refresh),
            ImageView(R.image.btn_refresh_light)) {
            onAction { refresh() }
        }
        addButton = styledStretchableButton(STYLE_DEFAULT_BUTTON, STRETCH_POINT, getString(R.string.add),
            ImageView(R.image.btn_add_dark)) {
            onAction { add() }
        }
        editButton = stretchableButton(STRETCH_POINT, getString(R.string.edit), ImageView(R.image.btn_edit_light)) {
            onAction { edit() }
        }
        searchField = styledTextField(STYLE_SEARCH_TEXTFIELD) {
            later { prefWidthProperty().bind(customerPagination.scene.widthProperty() * 0.12) }
            promptText = getString(R.string.search)
        }
        filterMenu = menuButton(graphic = ImageView(R.image.btn_filter_light)) {
            tooltip(getString(R.string.filter))
            filterNameItem = checkMenuItem(getString(R.string.name)) { isSelected = true }
            filterAddressItem = checkMenuItem(getString(R.string.address))
            filterNoteItem = checkMenuItem(getString(R.string.note))
        }
        idImage.tooltip(getString(R.string.id))
        sinceImage.tooltip(getString(R.string.since))
        addressImage.tooltip(getString(R.string.address))
        noteImage.tooltip(getString(R.string.note))
        contactImage.tooltip(getString(R.string.contact))
        typeColumn.stringCell { typedType.toString(this@CustomerController) }
        valueColumn.stringCell { value }
    }

    override fun refresh() = later {
        customerPagination.contentFactoryProperty().bind(bindingOf(
            searchField.textProperty(),
            filterNameItem.selectedProperty(),
            filterAddressItem.selectedProperty(),
            filterNoteItem.selectedProperty()
        ) {
            Callback<Pair<Int, Int>, Node> { (page, count) ->
                customerList = listView {
                    later {
                        transaction {
                            val customers = Customers.buildQuery {
                                if (searchField.text.isNotBlank()) {
                                    if (filterNameItem.isSelected)
                                        or(it.name.matches(searchField.text, CASE_INSENSITIVE))
                                    if (filterAddressItem.isSelected)
                                        or(it.address.matches(searchField.text, CASE_INSENSITIVE))
                                    if (filterNoteItem.isSelected)
                                        or(it.note.matches(searchField.text, CASE_INSENSITIVE))
                                }
                            }
                            customerPagination.pageCount = ceil(customers.count() / count.toDouble()).toInt()
                            items = customers
                                .skip(count * page)
                                .take(count).toMutableObservableList()
                            val fullAccess = employee.isAdmin().toProperty()
                            editButton.disableProperty().bind(!selectedBinding or !fullAccess)
                            addContactItem.disableProperty().bind(!selectedBinding)
                            deleteContactItem.disableProperty().bind(!selectedBinding2 or !fullAccess)
                        }
                    }
                }
                nameLabel.bindLabel { selected?.name.orEmpty() }
                idLabel.bindLabel { selected?.id?.toString().orEmpty() }
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

    fun add() = InputUserPopover(this, R.string.add_customer).showAt(addButton) { name ->
        transaction {
            when {
                Customers { it.name.matches("^$name$", CASE_INSENSITIVE) }.isNotEmpty() ->
                    styledErrorAlert(getStyle(R.style.openpss), getString(R.string.name_taken)).show()
                else -> {
                    Customer.new(name).let {
                        it.id = Customers.insert(it)
                        customerList.items.add(it)
                        customerList.selectionModel.select(customerList.items.lastIndex)
                    }
                }
            }
        }
    }

    private fun edit() = EditCustomerPopover(this, selected!!).showAt(editButton) {
        transaction {
            Customers[selected!!].projection { name + address + note }.update(it.name, it.address, it.note)
        }
        reload()
    }

    @FXML fun addContact() = AddContactPopover(this).showAt(contactTable) {
        transaction {
            Customers[selected!!].projection { contacts }.update(selected!!.contacts + it)
        }
        reload()
    }

    @FXML fun deleteContact() = yesNoAlert(R.string.delete_contact) {
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
        launch(FX) {
            delay(250)
            reselect(index)
        }
    }
}