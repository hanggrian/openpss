package com.hendraanggrian.openpss.ui.invoice

import com.hendraanggrian.openpss.App
import com.hendraanggrian.openpss.App.Companion.STRETCH_POINT
import com.hendraanggrian.openpss.PATTERN_DATETIME_EXTENDED
import com.hendraanggrian.openpss.R
import com.hendraanggrian.openpss.control.DateBox
import com.hendraanggrian.openpss.control.IntField
import com.hendraanggrian.openpss.control.PaginatedPane
import com.hendraanggrian.openpss.control.currencyCell
import com.hendraanggrian.openpss.control.doneCell
import com.hendraanggrian.openpss.control.intField
import com.hendraanggrian.openpss.control.space
import com.hendraanggrian.openpss.control.stretchableButton
import com.hendraanggrian.openpss.control.stringCell
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
import com.hendraanggrian.openpss.popup.dialog.ConfirmDialog
import com.hendraanggrian.openpss.popup.popover.ViewInvoicePopover
import com.hendraanggrian.openpss.ui.ActionController
import com.hendraanggrian.openpss.ui.Refreshable
import com.hendraanggrian.openpss.ui.Selectable
import com.hendraanggrian.openpss.ui.Selectable2
import javafx.beans.property.SimpleObjectProperty
import javafx.fxml.FXML
import javafx.geometry.Pos.CENTER
import javafx.geometry.Side.BOTTOM
import javafx.scene.Node
import javafx.scene.control.Button
import javafx.scene.control.ComboBox
import javafx.scene.control.RadioButton
import javafx.scene.control.SelectionModel
import javafx.scene.control.TableView
import javafx.scene.control.TableView.CONSTRAINED_RESIZE_POLICY
import javafx.scene.control.TextField
import javafx.scene.image.ImageView
import javafx.scene.layout.HBox
import javafx.scene.layout.Priority.ALWAYS
import javafx.util.Callback
import kotlinx.nosql.equal
import kotlinx.nosql.update
import ktfx.NodeManager
import ktfx.application.later
import ktfx.beans.binding.bindingOf
import ktfx.beans.binding.stringBindingOf
import ktfx.beans.value.and
import ktfx.beans.value.eq
import ktfx.beans.value.neq
import ktfx.beans.value.or
import ktfx.collections.emptyObservableList
import ktfx.collections.toMutableObservableList
import ktfx.collections.toObservableList
import ktfx.controlsfx.masterDetailPane
import ktfx.coroutines.onAction
import ktfx.coroutines.onHidden
import ktfx.coroutines.onMouseClicked
import ktfx.layouts.columns
import ktfx.layouts.contextMenu
import ktfx.layouts.hbox
import ktfx.layouts.region
import ktfx.layouts.separatorMenuItem
import ktfx.layouts.tableView
import ktfx.scene.input.isDoubleClick
import ktfx.scene.layout.paddingAll
import org.joda.time.LocalDate
import java.net.URL
import java.util.ResourceBundle
import kotlin.math.ceil

class InvoiceController : ActionController(), Refreshable, Selectable<Invoice>, Selectable2<Payment> {

    @FXML lateinit var filterBox: HBox
    @FXML lateinit var allDateRadio: RadioButton
    @FXML lateinit var pickDateRadio: RadioButton
    @FXML lateinit var dateBox: DateBox
    @FXML lateinit var customerField: TextField
    @FXML lateinit var paymentCombo: ComboBox<String>
    @FXML lateinit var invoicePagination: PaginatedPane
    @FXML lateinit var clearFiltersButton: Button

    private lateinit var refreshButton: Button
    private lateinit var addButton: Button
    private lateinit var searchField: IntField

    private val customerProperty = SimpleObjectProperty<Customer>()
    private lateinit var invoiceTable: TableView<Invoice>
    private lateinit var paymentTable: TableView<Payment>
    private lateinit var addPaymentButton: Button

    override val selectionModel: SelectionModel<Invoice> get() = invoiceTable.selectionModel
    override val selectionModel2: SelectionModel<Payment> get() = paymentTable.selectionModel

    override fun NodeManager.onCreateActions() {
        refreshButton = stretchableButton(STRETCH_POINT, getString(R.string.refresh), ImageView(R.image.act_refresh)) {
            onAction { refresh() }
        }
        addButton = stretchableButton(STRETCH_POINT, getString(R.string.add), ImageView(R.image.act_add)) {
            onAction { addInvoice() }
        }
        space(R.dimen.padding_large.toDouble())
        searchField = intField {
            filterBox.disableProperty().bind(valueProperty() neq 0)
            promptText = getString(R.string.search_no)
        }
    }

    override fun initialize(location: URL, resources: ResourceBundle) {
        super.initialize(location, resources)
        paymentCombo.run {
            items = listOf(R.string.any, R.string.paid, R.string.unpaid)
                .map { getString(it) }
                .toObservableList()
            selectionModel.selectFirst()
        }
        customerField.textProperty().bind(stringBindingOf(customerProperty) {
            customerProperty.value?.toString() ?: getString(R.string.search)
        })
        dateBox.disableProperty().bind(!pickDateRadio.selectedProperty())
        clearFiltersButton.disableProperty().bind(
            pickDateRadio.selectedProperty() and
                (dateBox.valueProperty() eq LocalDate.now()) and
                customerProperty.isNull and
                (paymentCombo.selectionModel.selectedIndexProperty() eq 0)
        )
    }

    override fun refresh() = later {
        invoicePagination.contentFactoryProperty().bind(bindingOf(
            searchField.valueProperty(),
            customerProperty,
            paymentCombo.valueProperty(),
            allDateRadio.selectedProperty(),
            pickDateRadio.selectedProperty(),
            dateBox.valueProperty()
        ) {
            Callback<Pair<Int, Int>, Node> { (page, count) ->
                masterDetailPane(BOTTOM) {
                    invoiceTable = ktfx.layouts.tableView {
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
                    detailNode = ktfx.layouts.vbox {
                        hbox(R.dimen.padding_medium.toDouble()) {
                            alignment = CENTER
                            paddingAll = R.dimen.padding_medium.toDouble()
                            region() hpriority ALWAYS
                            addPaymentButton = stretchableButton(
                                STRETCH_POINT,
                                getString(R.string.add_payment),
                                ImageView(R.image.act_add)
                            ) {
                                styleClass += App.STYLE_BUTTON_RAISED
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
                                getString(R.string.delete)(ImageView(R.image.menu_delete)) {
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
                                        when (paymentCombo.selectionModel.selectedIndex) {
                                            1 -> and(it.paid.equal(true))
                                            2 -> and(it.paid.equal(false))
                                        }
                                        if (pickDateRadio.isSelected) and(it.dateTime.matches(dateBox.value))
                                    }
                                }
                            }
                            invoicePagination.pageCount = ceil(invoices.count() / count.toDouble()).toInt()
                            invoiceTable.items = invoices
                                .skip(count * page)
                                .take(count).toMutableObservableList()
                            invoiceTable.contextMenu {
                                getString(R.string.view)(ImageView(R.image.menu_invoice)) {
                                    later { disableProperty().bind(!selectedBinding) }
                                    onAction { viewInvoice() }
                                }
                                separatorMenuItem()
                                getString(R.string.delete)(ImageView(R.image.menu_delete)) {
                                    disableProperty().bind(!selectedBinding or !isAdminProperty())
                                    onAction { deleteInvoice() }
                                }
                            }
                        }
                    }
                }
            }
        })
    }

    fun addInvoice() = AddInvoiceDialog(this).show {
        transaction {
            it!!.id = Invoices.insert(it)
            invoiceTable.items.add(it)
            invoiceTable.selectionModel.selectFirst()
        }
    }

    private fun deleteInvoice() = ConfirmDialog(this).show { _ ->
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
        paymentCombo.selectionModel.selectFirst()
    }

    @FXML fun selectCustomer() = SearchCustomerPopover(this).show(customerField) { customerProperty.set(it) }

    private fun viewInvoice() = ViewInvoicePopover(this, selected!!).apply {
        onHidden {
            transaction {
                reload(selected!!)
            }
        }
    }.show(invoiceTable)

    private fun addPayment() = AddPaymentPopover(this, selected!!).show(addPaymentButton) {
        transaction {
            Payments += it
            updatePaymentStatus()
            reload(selected!!)
        }
    }

    private fun deletePayment() = ConfirmDialog(this).show {
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