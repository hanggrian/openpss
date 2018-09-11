package com.hendraanggrian.openpss.ui.invoice

import com.hendraanggrian.openpss.App.Companion.STYLE_DEFAULT_BUTTON
import com.hendraanggrian.openpss.App.Companion.STYLE_SEARCH_TEXTFIELD
import com.hendraanggrian.openpss.PATTERN_DATETIME_EXTENDED
import com.hendraanggrian.openpss.R
import com.hendraanggrian.openpss.control.IntField
import com.hendraanggrian.openpss.control.PaginatedPane
import com.hendraanggrian.openpss.control.currencyCell
import com.hendraanggrian.openpss.control.doneCell
import com.hendraanggrian.openpss.control.popover.ViewInvoicePopover
import com.hendraanggrian.openpss.control.stretchableButton
import com.hendraanggrian.openpss.control.stringCell
import com.hendraanggrian.openpss.control.styledIntField
import com.hendraanggrian.openpss.control.styledStretchableButton
import com.hendraanggrian.openpss.control.yesNoAlert
import com.hendraanggrian.openpss.db.SessionWrapper
import com.hendraanggrian.openpss.db.matches
import com.hendraanggrian.openpss.db.schemas.Customer
import com.hendraanggrian.openpss.db.schemas.Customers
import com.hendraanggrian.openpss.db.schemas.Employees
import com.hendraanggrian.openpss.db.schemas.Invoice
import com.hendraanggrian.openpss.db.schemas.Invoices
import com.hendraanggrian.openpss.db.schemas.Payment
import com.hendraanggrian.openpss.db.schemas.Payments
import com.hendraanggrian.openpss.db.schemas.Payments.invoiceId
import com.hendraanggrian.openpss.db.transaction
import com.hendraanggrian.openpss.layout.DateBox
import com.hendraanggrian.openpss.layout.SegmentedTabPane.Companion.STRETCH_POINT
import com.hendraanggrian.openpss.ui.Refreshable
import com.hendraanggrian.openpss.ui.SegmentedController
import com.hendraanggrian.openpss.ui.Selectable
import com.hendraanggrian.openpss.ui.Selectable2
import javafx.beans.property.SimpleObjectProperty
import javafx.fxml.FXML
import javafx.geometry.Pos.CENTER
import javafx.geometry.Side.BOTTOM
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
import javafxx.application.later
import javafxx.beans.binding.bindingOf
import javafxx.beans.binding.stringBindingOf
import javafxx.beans.property.toProperty
import javafxx.beans.value.and
import javafxx.beans.value.eq
import javafxx.beans.value.neq
import javafxx.beans.value.or
import javafxx.collections.emptyObservableList
import javafxx.collections.toMutableObservableList
import javafxx.collections.toObservableList
import javafxx.coroutines.onAction
import javafxx.coroutines.onMouseClicked
import javafxx.layouts.LayoutManager
import javafxx.layouts.columns
import javafxx.layouts.contextMenu
import javafxx.layouts.controlsfx.masterDetailPane
import javafxx.layouts.hbox
import javafxx.layouts.region
import javafxx.layouts.separatorMenuItem
import javafxx.layouts.tableView
import javafxx.scene.input.isDoubleClick
import javafxx.scene.layout.paddingAll
import kotlinx.nosql.equal
import kotlinx.nosql.update
import org.joda.time.LocalDate
import java.net.URL
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
    @FXML lateinit var clearFiltersButton: Button
    @FXML lateinit var anyPaymentItem: RadioMenuItem
    @FXML lateinit var unpaidPaymentItem: RadioMenuItem
    @FXML lateinit var paidPaymentItem: RadioMenuItem
    @FXML lateinit var invoicePagination: PaginatedPane

    private lateinit var refreshButton: Button
    private lateinit var addButton: Button
    private lateinit var searchField: IntField

    private val customerProperty = SimpleObjectProperty<Customer>()
    private lateinit var invoiceTable: TableView<Invoice>
    private lateinit var paymentTable: TableView<Payment>
    private lateinit var addPaymentButton: Button

    override val selectionModel: SelectionModel<Invoice> get() = invoiceTable.selectionModel
    override val selectionModel2: SelectionModel<Payment> get() = paymentTable.selectionModel

    override fun LayoutManager<Node>.leftActions() {
        refreshButton =
            stretchableButton(STRETCH_POINT, getString(R.string.refresh), ImageView(R.image.btn_refresh_light)) {
                onAction { refresh() }
            }
        addButton = styledStretchableButton(
            STYLE_DEFAULT_BUTTON,
            STRETCH_POINT,
            getString(R.string.add),
            ImageView(R.image.btn_add_dark)
        ) {
            onAction { addInvoice() }
        }
    }

    override fun LayoutManager<Node>.rightActions() {
        searchField = styledIntField(STYLE_SEARCH_TEXTFIELD) {
            filterBox.disableProperty().bind(valueProperty() neq 0)
            promptText = getString(R.string.search_no)
        }
    }

    override fun initialize(location: URL, resources: ResourceBundle) {
        super.initialize(location, resources)
        customerButton.textProperty().bind(stringBindingOf(customerProperty) {
            customerProperty.value?.toString() ?: getString(R.string.search)
        })
        customerButtonItem.disableProperty().bind(customerProperty.isNull)
        paymentButton.textProperty().bind(stringBindingOf(
            anyPaymentItem.selectedProperty(),
            unpaidPaymentItem.selectedProperty(),
            paidPaymentItem.selectedProperty()
        ) {
            getString(
                when {
                    unpaidPaymentItem.isSelected -> R.string.unpaid
                    paidPaymentItem.isSelected -> R.string.paid
                    else -> R.string.any
                }
            )
        })
        pickDateRadio.graphic.disableProperty().bind(!pickDateRadio.selectedProperty())
        clearFiltersButton.disableProperty().bind(
            pickDateRadio.selectedProperty() and
                (dateBox.valueProperty() eq LocalDate.now()) and
                customerProperty.isNull and
                (paymentButton.textProperty() eq getString(R.string.any))
        )
    }

    override fun refresh() = later {
        invoicePagination.contentFactoryProperty().bind(bindingOf(
            searchField.valueProperty(),
            customerProperty,
            anyPaymentItem.selectedProperty(),
            unpaidPaymentItem.selectedProperty(),
            paidPaymentItem.selectedProperty(),
            allDateRadio.selectedProperty(),
            pickDateRadio.selectedProperty(),
            dateBox.valueProperty()
        ) {
            Callback<Pair<Int, Int>, Node> { (page, count) ->
                masterDetailPane(BOTTOM) {
                    invoiceTable = javafxx.layouts.tableView {
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
                    }
                    showDetailNodeProperty().bind(selectedBinding)
                    masterNode = invoiceTable
                    detailNode = javafxx.layouts.vbox {
                        hbox(R.dimen.padding_small.toDouble()) {
                            alignment = CENTER
                            paddingAll = R.dimen.padding_small.toDouble()
                            region() hpriority ALWAYS
                            addPaymentButton = styledStretchableButton(
                                STYLE_DEFAULT_BUTTON, STRETCH_POINT,
                                getString(R.string.add_payment), ImageView(R.image.btn_add_dark)
                            ) {
                                disableProperty().bind(!selectedBinding)
                                onAction { addPayment() }
                            }
                        }
                        paymentTable = tableView {
                            columnResizePolicy = CONSTRAINED_RESIZE_POLICY
                            columns {
                                getString(R.string.date)<String> {
                                    stringCell { dateTime.toString(PATTERN_DATETIME_EXTENDED) }
                                }
                                getString(R.string.employee)<String> {
                                    stringCell { transaction { Employees[employeeId].single().toString() } }
                                }
                                getString(R.string.value)<String> {
                                    currencyCell { value }
                                }
                                getString(R.string.cash)<Boolean> {
                                    doneCell { isCash() }
                                }
                                getString(R.string.reference)<String> {
                                    stringCell { reference }
                                }
                            }
                            itemsProperty().bind(bindingOf(invoiceTable.selectionModel.selectedItemProperty()) {
                                when (selected) {
                                    null -> emptyObservableList()
                                    else -> transaction { Payments { invoiceId.equal(selected!!.id) }.toObservableList() }
                                }
                            })
                            contextMenu {
                                getString(R.string.delete)(ImageView(R.image.btn_delete_light)) {
                                    disableProperty().bind(!this@tableView.selectionModel.selectedItemProperty().isNotNull)
                                    onAction { deletePayment() }
                                }
                            }
                        }
                    }
                    dividerPosition = 0.6
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
                            val fullAccess = employee.isAdmin().toProperty()
                            invoiceTable.contextMenu {
                                getString(R.string.view)(ImageView(R.image.btn_invoice_light)) {
                                    later { disableProperty().bind(!selectedBinding) }
                                    onAction { viewInvoice() }
                                }
                                separatorMenuItem()
                                getString(R.string.edit)(ImageView(R.image.btn_edit_light)) {
                                    disableProperty().bind(!selectedBinding or !fullAccess)
                                    onAction { editInvoice() }
                                }
                                getString(R.string.delete)(ImageView(R.image.btn_delete_light)) {
                                    disableProperty().bind(!selectedBinding or !fullAccess)
                                    onAction { deleteInvoice() }
                                }
                            }
                        }
                    }
                }
            }
        })
    }

    fun addInvoice() = InvoiceDialog(this, employee = employee).showAndWait().ifPresent {
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

    @FXML fun clearFilters() {
        customerProperty.set(null)
        pickDateRadio.isSelected = true
        dateBox.picker.value = java.time.LocalDate.now()
        anyPaymentItem.isSelected = true
    }

    @FXML fun selectCustomer() = SearchCustomerPopover(this).showAt(customerButton) { customerProperty.set(it) }

    @FXML fun clearCustomer() = customerProperty.set(null)

    private fun viewInvoice() = ViewInvoicePopover(selected!!).showAt(invoiceTable)

    private fun addPayment() = AddPaymentPopover(this, employee, selected!!).showAt(addPaymentButton) {
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