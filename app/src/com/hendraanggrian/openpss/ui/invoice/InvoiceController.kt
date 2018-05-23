package com.hendraanggrian.openpss.ui.invoice

import com.hendraanggrian.openpss.App.Companion.STYLE_DEFAULT_BUTTON
import com.hendraanggrian.openpss.App.Companion.STYLE_SEARCH_TEXTFIELD
import com.hendraanggrian.openpss.R
import com.hendraanggrian.openpss.control.IntField
import com.hendraanggrian.openpss.control.PaginatedPane
import com.hendraanggrian.openpss.control.popover.ViewInvoicePopover
import com.hendraanggrian.openpss.control.stretchableButton
import com.hendraanggrian.openpss.control.styledIntField
import com.hendraanggrian.openpss.control.styledStretchableButton
import com.hendraanggrian.openpss.db.SessionWrapper
import com.hendraanggrian.openpss.db.schemas.Customer
import com.hendraanggrian.openpss.db.schemas.Customers
import com.hendraanggrian.openpss.db.schemas.Employee.Role.MANAGER
import com.hendraanggrian.openpss.db.schemas.Employees
import com.hendraanggrian.openpss.db.schemas.Invoice
import com.hendraanggrian.openpss.db.schemas.Invoices
import com.hendraanggrian.openpss.db.schemas.Payment
import com.hendraanggrian.openpss.db.schemas.Payments
import com.hendraanggrian.openpss.db.schemas.Payments.invoiceId
import com.hendraanggrian.openpss.db.transaction
import com.hendraanggrian.openpss.layout.DateBox
import com.hendraanggrian.openpss.ui.Refreshable
import com.hendraanggrian.openpss.ui.SegmentedController
import com.hendraanggrian.openpss.ui.Selectable
import com.hendraanggrian.openpss.ui.Selectable2
import com.hendraanggrian.openpss.util.PATTERN_DATETIME_EXTENDED
import com.hendraanggrian.openpss.util.currencyCell
import com.hendraanggrian.openpss.util.doneCell
import com.hendraanggrian.openpss.util.matches
import com.hendraanggrian.openpss.util.stringCell
import com.hendraanggrian.openpss.util.yesNoAlert
import javafx.beans.property.SimpleObjectProperty
import javafx.fxml.FXML
import javafx.geometry.Orientation.VERTICAL
import javafx.geometry.Pos.CENTER
import javafx.scene.Node
import javafx.scene.control.Button
import javafx.scene.control.MenuButton
import javafx.scene.control.MenuItem
import javafx.scene.control.RadioButton
import javafx.scene.control.RadioMenuItem
import javafx.scene.control.SelectionModel
import javafx.scene.control.SplitMenuButton
import javafx.scene.control.TableView
import javafx.scene.control.TableView.CONSTRAINED_RESIZE_POLICY
import javafx.scene.image.ImageView
import javafx.scene.layout.HBox
import javafx.scene.layout.Priority.ALWAYS
import javafx.util.Callback
import kotlinx.nosql.equal
import kotlinx.nosql.update
import ktfx.application.later
import ktfx.beans.binding.bindingOf
import ktfx.beans.binding.stringBindingOf
import ktfx.beans.binding.times
import ktfx.beans.property.toReadOnlyProperty
import ktfx.beans.value.neq
import ktfx.beans.value.or
import ktfx.collections.emptyObservableList
import ktfx.collections.toMutableObservableList
import ktfx.collections.toObservableList
import ktfx.coroutines.onAction
import ktfx.coroutines.onMouseClicked
import ktfx.layouts.columns
import ktfx.layouts.contextMenu
import ktfx.layouts.hbox
import ktfx.layouts.region
import ktfx.layouts.separator
import ktfx.layouts.splitPane
import ktfx.layouts.tableView
import ktfx.layouts.vbox
import ktfx.scene.input.isDoubleClick
import ktfx.scene.layout.updatePadding
import java.net.URL
import java.time.LocalDate
import java.util.ResourceBundle
import kotlin.math.ceil

class InvoiceController : SegmentedController(), Refreshable, Selectable<Invoice>, Selectable2<Payment> {

    @FXML lateinit var filterBox: HBox
    @FXML lateinit var allDateRadio: RadioButton
    @FXML lateinit var pickDateRadio: RadioButton
    @FXML lateinit var dateBox: DateBox
    @FXML lateinit var customerButton: SplitMenuButton
    @FXML lateinit var customerButtonItem: MenuItem
    @FXML lateinit var paymentButton: MenuButton
    @FXML lateinit var anyPaymentItem: RadioMenuItem
    @FXML lateinit var unpaidPaymentItem: RadioMenuItem
    @FXML lateinit var paidPaymentItem: RadioMenuItem
    @FXML lateinit var invoicePagination: PaginatedPane

    private lateinit var refreshButton: Button
    private lateinit var addButton: Button
    private lateinit var editButton: Button
    private lateinit var deleteButton: Button
    override val leftButtons: List<Node>
        get() = listOf(refreshButton, separator(VERTICAL), addButton, editButton, deleteButton)

    private lateinit var searchField: IntField
    private lateinit var resetFiltersButton: Button
    override val rightButtons: List<Node> get() = listOf(searchField, resetFiltersButton)

    private val customerProperty = SimpleObjectProperty<Customer>()
    private lateinit var invoiceTable: TableView<Invoice>
    private lateinit var paymentTable: TableView<Payment>
    private lateinit var deletePaymentButton: Button

    override val selectionModel: SelectionModel<Invoice> get() = invoiceTable.selectionModel
    override val selectionModel2: SelectionModel<Payment> get() = paymentTable.selectionModel

    override fun initialize(location: URL, resources: ResourceBundle) {
        super.initialize(location, resources)
        refreshButton = stretchableButton(getString(R.string.refresh), ImageView(R.image.btn_refresh_light)) {
            onAction { refresh() }
        }
        addButton = styledStretchableButton(STYLE_DEFAULT_BUTTON, getString(R.string.add),
            ImageView(R.image.btn_add_dark)) {
            onAction { addInvoice() }
        }
        editButton = stretchableButton(getString(R.string.edit), ImageView(R.image.btn_edit_light)) {
            onAction { editInvoice() }
        }
        deleteButton = stretchableButton(getString(R.string.delete), ImageView(R.image.btn_delete_light)) {
            onAction { deleteInvoice() }
        }
        searchField = styledIntField(STYLE_SEARCH_TEXTFIELD) {
            filterBox.disableProperty().bind(valueProperty() neq 0)
            later { prefWidthProperty().bind(invoicePagination.scene.widthProperty() * 0.12) }
            promptText = getString(R.string.search_no)
        }
        resetFiltersButton = stretchableButton(getString(R.string.clear_filters),
            ImageView(R.image.btn_clear_inactive)) {
            onAction {
                searchField.value = 0
                customerProperty.set(null)
                pickDateRadio.isSelected = true
                dateBox.picker.value = LocalDate.now()
                anyPaymentItem.isSelected = true
            }
        }

        customerButton.textProperty().bind(stringBindingOf(customerProperty) {
            customerProperty.value?.toString() ?: getString(R.string.search_customer)
        })
        customerButtonItem.disableProperty().bind(customerProperty.isNull)
        paymentButton.textProperty().bind(stringBindingOf(
            anyPaymentItem.selectedProperty(),
            unpaidPaymentItem.selectedProperty(),
            paidPaymentItem.selectedProperty()) {
            getString(when {
                unpaidPaymentItem.isSelected -> R.string.unpaid
                paidPaymentItem.isSelected -> R.string.paid
                else -> R.string.any
            })
        })
        pickDateRadio.graphic.visibleProperty().bind(pickDateRadio.selectedProperty())
    }

    override fun refresh() = later {
        invoicePagination.contentFactoryProperty().bind(bindingOf(searchField.valueProperty(), customerProperty,
            anyPaymentItem.selectedProperty(), unpaidPaymentItem.selectedProperty(), paidPaymentItem.selectedProperty(),
            allDateRadio.selectedProperty(), pickDateRadio.selectedProperty(), dateBox.valueProperty()) {
            Callback<Pair<Int, Int>, Node> { (page, count) ->
                splitPane {
                    orientation = VERTICAL
                    invoiceTable = tableView {
                        columnResizePolicy = CONSTRAINED_RESIZE_POLICY
                        columns {
                            getString(R.string.id)<String> { stringCell { no.toString() } }
                            getString(R.string.date)<String> {
                                stringCell { dateTime.toString(PATTERN_DATETIME_EXTENDED) }
                            }
                            getString(R.string.employee)<String> {
                                stringCell { transaction { Employees[employeeId].single().toString() } }
                            }
                            getString(R.string.customer)<String> {
                                stringCell { transaction { Customers[customerId].single().toString() } }
                            }
                            getString(R.string.total)<String> { currencyCell { total } }
                            getString(R.string.print)<Boolean> { doneCell { printed } }
                            getString(R.string.paid)<Boolean> { doneCell { paid } }
                            getString(R.string.done)<Boolean> { doneCell { done } }
                        }
                        onMouseClicked { if (it.isDoubleClick() && selected != null) viewInvoice() }
                        contextMenu {
                            getString(R.string.view)(ImageView(R.image.btn_invoice_light)) {
                                later { disableProperty().bind(!selectedBinding) }
                                onAction { viewInvoice() }
                            }
                        }
                    }
                    vbox {
                        hbox {
                            alignment = CENTER
                            spacing = 8.0
                            updatePadding(8.0, 16.0, 8.0, 16.0)
                            region() hpriority ALWAYS
                            styledStretchableButton(STYLE_DEFAULT_BUTTON, getString(R.string.add_payment),
                                ImageView(R.image.btn_add_dark)) {
                                disableProperty().bind(!selectedBinding)
                                onAction { addPayment() }
                            }
                            deletePaymentButton = stretchableButton(getString(R.string.delete_payment),
                                ImageView(R.image.btn_delete_light)) {
                                later { disableProperty().bind(!selectedBinding2) }
                                onAction { deletePayment() }
                            }
                        }
                        paymentTable = tableView {
                            minHeightProperty().bind(this@splitPane.heightProperty() * 0.25)
                            columnResizePolicy = CONSTRAINED_RESIZE_POLICY
                            columns {
                                getString(R.string.date)<String> {
                                    stringCell { dateTime.toString(PATTERN_DATETIME_EXTENDED) }
                                }
                                getString(R.string.employee)<String> {
                                    stringCell { transaction { Employees[employeeId].single().toString() } }
                                }
                                getString(R.string.value)<String> {
                                    stringCell { value.toString() }
                                }
                                getString(R.string.payment_method)<String> {
                                    stringCell { typedMethod.toString(this@InvoiceController) }
                                }
                                getString(R.string.reference)<String> {
                                    stringCell { reference }
                                }
                            }
                            itemsProperty().bind(bindingOf(invoiceTable.selectionModel.selectedItemProperty()) {
                                if (selected == null) emptyObservableList()
                                else transaction { Payments { invoiceId.equal(selected!!.id) }.toObservableList() }
                            })
                        }
                    }
                    setDividerPosition(0, 0.75)
                    later {
                        transaction {
                            val invoices = Invoices.buildQuery {
                                when {
                                    searchField.value != 0 -> and(it.no.equal(searchField.value))
                                    else -> {
                                        if (customerProperty.value != null)
                                            and(it.customerId.equal(customerProperty.value.id))
                                        when {
                                            unpaidPaymentItem.isSelected -> and(it.paid.equal(false))
                                            paidPaymentItem.isSelected -> and(it.paid.equal(true))
                                        }
                                        if (pickDateRadio.isSelected) and(it.dateTime.matches(dateBox.value))
                                    }
                                }
                            }
                            invoicePagination.pageCount = ceil(invoices.count() / count.toDouble()).toInt()
                            invoiceTable.items = invoices
                                .skip(count * page)
                                .take(count).toMutableObservableList()
                            val fullAccess = login.isAtLeast(MANAGER).toReadOnlyProperty()
                            editButton.disableProperty().bind(!selectedBinding or !fullAccess)
                            deleteButton.disableProperty().bind(!selectedBinding or !fullAccess)
                        }
                    }
                }
            }
        })
    }

    fun addInvoice() = InvoiceDialog(this, employee = login).showAndWait().ifPresent {
        transaction {
            it.id = Invoices.insert(it)
            invoiceTable.items.add(it)
            invoiceTable.selectionModel.selectFirst()
        }
    }

    private fun editInvoice() = InvoiceDialog(this@InvoiceController, selected!!)
        .showAndWait()
        .ifPresent {
            transaction {
                Invoices[it]
                    .projection { plates + offsets + others + note + paid }
                    .update(it.plates, it.offsets, it.others, it.note, it.calculateDue() <= 0.0)
                reload(it)
            }
        }

    private fun deleteInvoice() = yesNoAlert {
        transaction {
            Invoices -= selected!!
            Payments { invoiceId.equal(selected!!.id) }.remove()
        }
        invoiceTable.items.remove(selected)
    }

    @FXML fun selectCustomer() = SearchCustomerPopover(this).showAt(customerButton) { customerProperty.set(it) }

    @FXML fun clearCustomer() = customerProperty.set(null)

    fun viewInvoice() = ViewInvoicePopover(selected!!).showAt(invoiceTable)

    private fun addPayment() = AddPaymentPopover(this, login, selected!!).showAt(deletePaymentButton) {
        transaction {
            Payments += it
            updatePaymentStatus()
            reload(selected!!)
        }
    }

    private fun deletePayment() = yesNoAlert {
        transaction {
            Payments -= selected2!!
            updatePaymentStatus()
            reload(selected!!)
        }
    }

    private fun SessionWrapper.updatePaymentStatus() = Invoices[selected!!]
        .projection { Invoices.paid }
        .update(selected!!.calculateDue() <= 0.0)

    private fun SessionWrapper.reload(invoice: Invoice) = invoiceTable.run {
        items.indexOf(invoice).let { index ->
            items[index] = Invoices[invoice].single()
            selectionModel.select(index)
        }
    }
}