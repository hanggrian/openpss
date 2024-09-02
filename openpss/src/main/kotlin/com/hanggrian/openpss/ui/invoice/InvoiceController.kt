package com.hanggrian.openpss.ui.invoice

import com.hanggrian.openpss.PATTERN_DATETIME_EXTENDED
import com.hanggrian.openpss.R
import com.hanggrian.openpss.control.DateBox
import com.hanggrian.openpss.control.IntField
import com.hanggrian.openpss.control.Toolbar
import com.hanggrian.openpss.db.ExtendedSession
import com.hanggrian.openpss.db.schemas.Customer
import com.hanggrian.openpss.db.schemas.Customers
import com.hanggrian.openpss.db.schemas.Employees
import com.hanggrian.openpss.db.schemas.Invoice
import com.hanggrian.openpss.db.schemas.Invoices
import com.hanggrian.openpss.db.schemas.Payment
import com.hanggrian.openpss.db.schemas.Payments
import com.hanggrian.openpss.db.schemas.Payments.invoiceId
import com.hanggrian.openpss.db.transaction
import com.hanggrian.openpss.popup.dialog.ConfirmDialog
import com.hanggrian.openpss.ui.ActionController
import com.hanggrian.openpss.ui.Refreshable
import com.hanggrian.openpss.util.currencyCell
import com.hanggrian.openpss.util.doneCell
import com.hanggrian.openpss.util.matches
import com.hanggrian.openpss.util.stringCell
import javafx.beans.property.SimpleObjectProperty
import javafx.fxml.FXML
import javafx.scene.control.Button
import javafx.scene.control.ComboBox
import javafx.scene.control.Pagination
import javafx.scene.control.RadioButton
import javafx.scene.control.TableView
import javafx.scene.control.TableView.CONSTRAINED_RESIZE_POLICY
import javafx.scene.control.TextField
import javafx.scene.image.ImageView
import javafx.scene.layout.HBox
import javafx.util.Callback
import kotlinx.nosql.equal
import kotlinx.nosql.update
import ktfx.bindings.and
import ktfx.bindings.bindingBy
import ktfx.bindings.bindingOf
import ktfx.bindings.eq
import ktfx.bindings.given
import ktfx.bindings.neq
import ktfx.bindings.otherwise
import ktfx.bindings.stringBindingBy
import ktfx.bindings.then
import ktfx.collections.emptyObservableList
import ktfx.collections.toMutableObservableList
import ktfx.collections.toObservableList
import ktfx.controls.SIDE_BOTTOM
import ktfx.controls.columns
import ktfx.controls.isSelected
import ktfx.controls.selectedBinding
import ktfx.controlsfx.layouts.masterDetailPane
import ktfx.coroutines.onAction
import ktfx.coroutines.onMouseClicked
import ktfx.inputs.isDoubleClick
import ktfx.jfoenix.layouts.leftItems
import ktfx.jfoenix.layouts.styledJfxButton
import ktfx.layouts.NodeContainer
import ktfx.layouts.contextMenu
import ktfx.layouts.separatorMenuItem
import ktfx.layouts.styledLabel
import ktfx.layouts.tableView
import ktfx.layouts.tooltip
import ktfx.layouts.vbox
import ktfx.runLater
import org.joda.time.LocalDate
import java.net.URL
import java.util.ResourceBundle

class InvoiceController :
    ActionController(),
    Refreshable {
    @FXML
    lateinit var filterBox: HBox

    @FXML
    lateinit var allDateRadio: RadioButton

    @FXML
    lateinit var pickDateRadio: RadioButton

    @FXML
    lateinit var dateBox: DateBox

    @FXML
    lateinit var customerField: TextField

    @FXML
    lateinit var paymentCombo: ComboBox<String>

    @FXML
    lateinit var invoicePagination: Pagination

    private lateinit var refreshButton: Button
    private lateinit var addButton: Button
    private lateinit var clearFiltersButton: Button
    private lateinit var searchField: IntField

    private val customerProperty = SimpleObjectProperty<Customer>()
    private lateinit var invoiceTable: TableView<Invoice>
    private lateinit var paymentTable: TableView<Payment>

    override fun NodeContainer.onCreateActions() {
        refreshButton =
            styledJfxButton(null, ImageView(R.image_act_refresh), R.style_flat) {
                tooltip(getString(R.string_refresh))
                onAction { refresh() }
            }
        addButton =
            styledJfxButton(null, ImageView(R.image_act_add), R.style_flat) {
                tooltip(getString(R.string_add))
                onAction { addInvoice() }
            }
        clearFiltersButton =
            styledJfxButton(null, ImageView(R.image_act_clear_filters), R.style_flat) {
                tooltip(getString(R.string_clear_filters))
                onAction { clearFilters() }
            }
        searchField =
            addChild(
                IntField().apply {
                    filterBox.disableProperty().bind(valueProperty neq 0)
                    promptText = getString(R.string_search_no)
                },
            )
    }

    override fun initialize(location: URL, resources: ResourceBundle) {
        super.initialize(location, resources)
        paymentCombo.run {
            items =
                listOf(R.string_paid_and_unpaid, R.string_paid, R.string_unpaid)
                    .map { getString(it) }
                    .toObservableList()
            selectionModel.selectFirst()
        }
        customerField.textProperty().bind(
            customerProperty.stringBindingBy {
                it?.toString() ?: getString(R.string_search_customer)
            },
        )
        dateBox.disableProperty().bind(!pickDateRadio.selectedProperty())
        clearFiltersButton.disableProperty().bind(
            pickDateRadio.selectedProperty() and
                (dateBox.valueProperty eq LocalDate.now()) and
                customerProperty.isNull and
                (paymentCombo.selectionModel.selectedIndexProperty() eq 0),
        )
    }

    override fun refresh() =
        runLater {
            invoicePagination.maxPageIndicatorCountProperty().bind(
                given(allDateRadio.selectedProperty()) then
                    invoicePagination.pageCount otherwise
                    10,
            )
            invoicePagination.pageFactoryProperty().bind(
                bindingOf(
                    searchField.valueProperty,
                    customerProperty,
                    paymentCombo.valueProperty(),
                    allDateRadio.selectedProperty(),
                    pickDateRadio.selectedProperty(),
                    dateBox.valueProperty,
                ) {
                    Callback { page ->
                        masterDetailPane(SIDE_BOTTOM) {
                            invoiceTable =
                                tableView {
                                    columnResizePolicy = CONSTRAINED_RESIZE_POLICY
                                    columns {
                                        getString(R.string_id).invoke {
                                            stringCell { no.toString() }
                                        }
                                        getString(R.string_date).invoke {
                                            stringCell {
                                                dateTime.toString(PATTERN_DATETIME_EXTENDED)
                                            }
                                        }
                                        getString(R.string_employee).invoke {
                                            stringCell {
                                                transaction {
                                                    Employees[employeeId]
                                                        .singleOrNull()
                                                        ?.toString()
                                                        .orEmpty()
                                                }
                                            }
                                        }
                                        getString(R.string_customer).invoke {
                                            stringCell {
                                                transaction {
                                                    Customers[customerId].single().toString()
                                                }
                                            }
                                        }
                                        getString(R.string_total).invoke {
                                            currencyCell(this@InvoiceController) { total }
                                        }
                                        getString(R.string_print).invoke {
                                            doneCell { isPrinted }
                                        }
                                        getString(R.string_paid).invoke {
                                            doneCell { isPaid }
                                        }
                                        getString(R.string_done).invoke {
                                            doneCell { isDone }
                                        }
                                    }
                                    onMouseClicked {
                                        if (it.isDoubleClick() &&
                                            invoiceTable.selectionModel.isSelected()
                                        ) {
                                            viewInvoice()
                                        }
                                    }
                                    titleProperty.bind(
                                        selectionModel
                                            .selectedItemProperty()
                                            .stringBindingBy {
                                                Invoice.no(
                                                    this@InvoiceController,
                                                    it?.no,
                                                )
                                            },
                                    )
                                }
                            showDetailNodeProperty()
                                .bind(invoiceTable.selectionModel.selectedBinding)
                            masterNode = invoiceTable
                            detailNode =
                                vbox {
                                    addChild(
                                        Toolbar().apply {
                                            leftItems {
                                                styledLabel(
                                                    getString(R.string_payment),
                                                    null,
                                                    R.style_bold,
                                                    R.style_accent,
                                                )
                                            }
                                        },
                                    )
                                    paymentTable =
                                        tableView {
                                            columnResizePolicy = CONSTRAINED_RESIZE_POLICY
                                            columns {
                                                getString(R.string_date).invoke {
                                                    stringCell {
                                                        dateTime.toString(PATTERN_DATETIME_EXTENDED)
                                                    }
                                                }
                                                getString(R.string_employee).invoke {
                                                    stringCell {
                                                        transaction {
                                                            Employees[employeeId]
                                                                .singleOrNull()
                                                                ?.toString()
                                                                .orEmpty()
                                                        }
                                                    }
                                                }
                                                getString(R.string_value).invoke {
                                                    currencyCell(this@InvoiceController) { value }
                                                }
                                                getString(R.string_cash).invoke {
                                                    doneCell { isCash() }
                                                }
                                                getString(R.string_reference).invoke {
                                                    stringCell { reference }
                                                }
                                            }
                                            itemsProperty().bind(
                                                invoiceTable.selectionModel
                                                    .selectedItemProperty()
                                                    .bindingBy {
                                                        when (it) {
                                                            null -> emptyObservableList()
                                                            else ->
                                                                transaction {
                                                                    Payments {
                                                                        invoiceId.equal(
                                                                            invoiceTable
                                                                                .selectionModel
                                                                                .selectedItem
                                                                                .id,
                                                                        )
                                                                    }.toObservableList()
                                                                }
                                                        }
                                                    },
                                            )
                                            contextMenu {
                                                getString(R.string_add)(
                                                    ImageView(R.image_menu_add),
                                                ) {
                                                    disableProperty()
                                                        .bind(
                                                            invoiceTable.selectionModel
                                                                .selectedItemProperty()
                                                                .isNull,
                                                        )
                                                    onAction { addPayment() }
                                                }
                                                getString(R.string_delete)(
                                                    ImageView(R.image_menu_delete),
                                                ) {
                                                    disableProperty()
                                                        .bind(
                                                            !this@tableView
                                                                .selectionModel
                                                                .selectedItemProperty()
                                                                .isNotNull,
                                                        )
                                                    onAction { deletePayment() }
                                                }
                                            }
                                        }
                                }
                            dividerPosition = 0.6
                            runLater {
                                transaction {
                                    val invoices =
                                        Invoices.buildQuery {
                                            when {
                                                searchField.value != 0 ->
                                                    and(it.no.equal(searchField.value))
                                                else -> {
                                                    if (customerProperty.value != null) {
                                                        and(
                                                            it.customerId
                                                                .equal(customerProperty.value.id),
                                                        )
                                                    }
                                                    when (
                                                        paymentCombo
                                                            .selectionModel
                                                            .selectedIndex
                                                    ) {
                                                        1 -> and(it.isPaid.equal(true))
                                                        2 -> and(it.isPaid.equal(false))
                                                    }
                                                    if (pickDateRadio.isSelected) {
                                                        and(it.dateTime.matches(dateBox.value!!))
                                                    }
                                                }
                                            }
                                        }
                                    invoiceTable.items =
                                        invoices
                                            .skip(invoicePagination.pageCount * page)
                                            .take(invoicePagination.pageCount)
                                            .toMutableObservableList()
                                    invoiceTable.contextMenu {
                                        getString(R.string_view)(ImageView(R.image_menu_invoice)) {
                                            runLater {
                                                disableProperty()
                                                    .bind(
                                                        invoiceTable.selectionModel
                                                            .selectedItemProperty()
                                                            .isNull,
                                                    )
                                            }
                                            onAction { viewInvoice() }
                                        }
                                        getString(R.string_done)(ImageView(R.image_menu_done)) {
                                            runLater {
                                                disableProperty().bind(
                                                    invoiceTable.selectionModel
                                                        .selectedItemProperty()
                                                        .bindingBy {
                                                            when {
                                                                it != null && !it.isDone -> false
                                                                else -> true
                                                            }
                                                        },
                                                )
                                            }
                                            onAction {
                                                transaction {
                                                    invoiceTable.selectionModel.selectedItem
                                                        .done(this@InvoiceController)
                                                }
                                                refreshButton.fire()
                                            }
                                        }
                                        separatorMenuItem()
                                        getString(R.string_delete)(ImageView(R.image_menu_delete)) {
                                            disableProperty().bind(
                                                invoiceTable.selectionModel
                                                    .selectedItemProperty()
                                                    .isNull,
                                            )
                                            onAction {
                                                DeleteInvoiceAction(
                                                    this@InvoiceController,
                                                    invoiceTable.selectionModel.selectedItem,
                                                ).invoke {
                                                    invoiceTable.items.remove(
                                                        invoiceTable.selectionModel.selectedItem,
                                                    )
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                },
            )
        }

    fun addInvoice() =
        AddInvoiceDialog(this).show { result ->
            (AddInvoiceAction(this@InvoiceController, result!!)) {
                invoiceTable.items.add(it)
                invoiceTable.selectionModel.selectFirst()
            }
        }

    private fun clearFilters() {
        customerProperty.set(null)
        pickDateRadio.isSelected = true
        dateBox.picker.value = java.time.LocalDate.now()
        paymentCombo.selectionModel.selectFirst()
    }

    @FXML
    fun selectCustomer() =
        SearchCustomerPopover(this).show(customerField) { customerProperty.set(it) }

    private fun viewInvoice() =
        ViewInvoicePopover(this, invoiceTable.selectionModel.selectedItem)
            .apply {
                setOnHidden {
                    transaction {
                        reload(invoiceTable.selectionModel.selectedItem)
                    }
                }
            }.show(invoiceTable)

    private fun addPayment() =
        AddPaymentPopover(this, invoiceTable.selectionModel.selectedItem).show(paymentTable) {
            (AddPaymentAction(this@InvoiceController, it!!)) {
                updatePaymentStatus()
                reload(invoiceTable.selectionModel.selectedItem)
            }
        }

    private fun deletePayment() {
        val payment = paymentTable.selectionModel.selectedItem
        ConfirmDialog(this, getString(R.string__delete_payment, payment.value)).show {
            DeletePaymentAction(this@InvoiceController, paymentTable.selectionModel.selectedItem)
                .invoke {
                    updatePaymentStatus()
                    reload(invoiceTable.selectionModel.selectedItem)
                }
        }
    }

    private fun ExtendedSession.updatePaymentStatus() =
        Invoices[invoiceTable.selectionModel.selectedItem]
            .projection { isPaid }
            .update(invoiceTable.selectionModel.selectedItem.calculateDue() <= 0.0)

    private fun ExtendedSession.reload(invoice: Invoice) =
        invoiceTable.run {
            items.indexOf(invoice).let { index ->
                items[index] = Invoices[invoice].single()
                selectionModel.select(index)
            }
        }
}
