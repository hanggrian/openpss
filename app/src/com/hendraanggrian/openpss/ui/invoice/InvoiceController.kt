package com.hendraanggrian.openpss.ui.invoice

import com.hendraanggrian.openpss.R
import com.hendraanggrian.openpss.controls.ViewInvoiceDialog
import com.hendraanggrian.openpss.db.buildQuery
import com.hendraanggrian.openpss.db.schemas.Customer
import com.hendraanggrian.openpss.db.schemas.Customers
import com.hendraanggrian.openpss.db.schemas.Employees
import com.hendraanggrian.openpss.db.schemas.Invoice
import com.hendraanggrian.openpss.db.schemas.Invoices
import com.hendraanggrian.openpss.db.schemas.Payment
import com.hendraanggrian.openpss.db.schemas.Payments
import com.hendraanggrian.openpss.db.schemas.calculateDue
import com.hendraanggrian.openpss.db.transaction
import com.hendraanggrian.openpss.io.properties.SettingsFile.INVOICE_PAGINATION_ITEMS
import com.hendraanggrian.openpss.layouts.DateBox
import com.hendraanggrian.openpss.ui.Controller
import com.hendraanggrian.openpss.ui.Refreshable
import com.hendraanggrian.openpss.ui.Selectable
import com.hendraanggrian.openpss.ui.Selectable2
import com.hendraanggrian.openpss.util.PATTERN_DATETIME_EXTENDED
import com.hendraanggrian.openpss.util.controller
import com.hendraanggrian.openpss.util.currencyCell
import com.hendraanggrian.openpss.util.doneCell
import com.hendraanggrian.openpss.util.findByDoc
import com.hendraanggrian.openpss.util.findById
import com.hendraanggrian.openpss.util.getResource
import com.hendraanggrian.openpss.util.matches
import com.hendraanggrian.openpss.util.pane
import com.hendraanggrian.openpss.util.stringCell
import com.hendraanggrian.openpss.util.style
import com.hendraanggrian.openpss.util.yesNoAlert
import javafx.beans.property.SimpleObjectProperty
import javafx.fxml.FXML
import javafx.fxml.FXMLLoader
import javafx.scene.Node
import javafx.scene.Scene
import javafx.scene.control.Button
import javafx.scene.control.Label
import javafx.scene.control.MenuButton
import javafx.scene.control.MenuItem
import javafx.scene.control.Pagination
import javafx.scene.control.RadioButton
import javafx.scene.control.RadioMenuItem
import javafx.scene.control.SelectionModel
import javafx.scene.control.SplitMenuButton
import javafx.scene.control.SplitPane
import javafx.scene.control.TableColumn
import javafx.scene.control.TableView
import javafx.scene.control.TableView.CONSTRAINED_RESIZE_POLICY
import javafx.scene.layout.Pane
import javafx.stage.Modality.APPLICATION_MODAL
import javafx.util.Callback
import kotlinx.nosql.equal
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
import ktfx.coroutines.onMouseClicked
import ktfx.layouts.columns
import ktfx.layouts.tableView
import ktfx.scene.input.isDoubleClick
import ktfx.stage.stage
import java.net.URL
import java.util.ResourceBundle
import kotlin.math.ceil

class InvoiceController : Controller(), Refreshable, Selectable<Invoice>, Selectable2<Payment> {

    @FXML lateinit var editInvoiceButton: Button
    @FXML lateinit var deleteInvoiceButton: Button
    @FXML lateinit var viewInvoiceButton: Button
    @FXML lateinit var addPaymentButton: Button
    @FXML lateinit var deletePaymentButton: Button
    @FXML lateinit var customerButton: SplitMenuButton
    @FXML lateinit var customerButtonItem: MenuItem
    @FXML lateinit var paymentButton: MenuButton
    @FXML lateinit var anyPaymentItem: RadioMenuItem
    @FXML lateinit var unpaidPaymentItem: RadioMenuItem
    @FXML lateinit var paidPaymentItem: RadioMenuItem
    @FXML lateinit var allDateRadio: RadioButton
    @FXML lateinit var pickDateRadio: RadioButton
    @FXML lateinit var dateBox: DateBox
    @FXML lateinit var splitPane: SplitPane
    @FXML lateinit var paymentPane: Pane
    @FXML lateinit var invoicePagination: Pagination
    @FXML lateinit var paymentTable: TableView<Payment>
    @FXML lateinit var paymentDateTimeColumn: TableColumn<Payment, String>
    @FXML lateinit var paymentEmployeeColumn: TableColumn<Payment, String>
    @FXML lateinit var paymentValueColumn: TableColumn<Payment, String>
    @FXML lateinit var paymentMethodColumn: TableColumn<Payment, String>
    @FXML lateinit var coverLabel: Label

    private val customerProperty = SimpleObjectProperty<Customer>()
    private lateinit var invoiceTable: TableView<Invoice>

    override fun initialize(location: URL, resources: ResourceBundle) {
        super.initialize(location, resources)
        paymentPane.minHeightProperty().bind(splitPane.heightProperty() * 0.2)

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
        pickDateRadio.graphic.disableProperty().bind(!pickDateRadio.selectedProperty())

        paymentDateTimeColumn.stringCell { dateTime.toString(PATTERN_DATETIME_EXTENDED) }
        paymentEmployeeColumn.stringCell { transaction { findById(Employees, employeeId).single() }!! }
        paymentValueColumn.currencyCell { value }
        paymentMethodColumn.stringCell { typedMethod.toString(this@InvoiceController) }
    }

    override fun refresh() = invoicePagination.pageFactoryProperty().bind(bindingOf(customerProperty,
        anyPaymentItem.selectedProperty(), unpaidPaymentItem.selectedProperty(), paidPaymentItem.selectedProperty(),
        allDateRadio.selectedProperty(), pickDateRadio.selectedProperty(), dateBox.valueProperty) {
        Callback<Int, Node> { page ->
            invoiceTable = tableView {
                columnResizePolicy = CONSTRAINED_RESIZE_POLICY
                columns {
                    getString(R.string.id)<String> { stringCell { no } }
                    getString(R.string.date)<String> { stringCell { dateTime.toString(PATTERN_DATETIME_EXTENDED) } }
                    getString(R.string.employee)<String> {
                        stringCell { transaction { findById(Employees, employeeId).single() }!! }
                    }
                    getString(R.string.customer)<String> {
                        stringCell { transaction { findById(Customers, customerId).single() }!! }
                    }
                    getString(R.string.total)<String> { currencyCell { total } }
                    getString(R.string.print)<Boolean> { doneCell { printed } }
                    getString(R.string.paid)<Boolean> { doneCell { paid } }
                    getString(R.string.done)<Boolean> { doneCell { done } }
                }
                onMouseClicked { if (it.isDoubleClick() && selected != null) viewInvoice() }
                later {
                    transaction {
                        val invoices = Invoices.find {
                            buildQuery {
                                if (customerProperty.value != null) and(customerId.equal(customerProperty.value.id))
                                if (unpaidPaymentItem.isSelected) and(paid.equal(false))
                                if (paidPaymentItem.isSelected) and(paid.equal(true))
                                if (pickDateRadio.isSelected) and(dateTime.matches(dateBox.value))
                            }
                        }
                        invoicePagination.pageCount =
                            ceil(invoices.count() / INVOICE_PAGINATION_ITEMS.toDouble()).toInt()
                        items = invoices
                            .skip(INVOICE_PAGINATION_ITEMS * page)
                            .take(INVOICE_PAGINATION_ITEMS).toMutableObservableList()
                    }
                }
            }
            later {
                editInvoiceButton.disableProperty().bind(!selectedBinding or !isFullAccess.toReadOnlyProperty())
                deleteInvoiceButton.disableProperty().bind(!selectedBinding or !isFullAccess.toReadOnlyProperty())
                viewInvoiceButton.disableProperty().bind(!selectedBinding)
                addPaymentButton.disableProperty().bind(!selectedBinding)
                deletePaymentButton.disableProperty().bind(!selectedBinding2 or !isFullAccess.toReadOnlyProperty())
            }
            paymentTable.itemsProperty().bind(bindingOf(invoiceTable.selectionModel.selectedItemProperty()) {
                if (selected == null) emptyObservableList()
                else transaction { Payments.find { invoiceId.equal(selected!!.id) }.toObservableList() }!!
            })
            coverLabel.visibleProperty().bind(invoiceTable.selectionModel.selectedItemProperty().isNull)
            invoiceTable
        }
    })

    override val selectionModel: SelectionModel<Invoice> get() = invoiceTable.selectionModel

    override val selectionModel2: SelectionModel<Payment> get() = paymentTable.selectionModel

    @FXML fun addInvoice() = InvoiceDialog(this, employee = _employee).showAndWait().ifPresent {
        transaction {
            it.id = Invoices.insert(it)
            invoiceTable.items.add(it)
            invoiceTable.selectionModel.selectFirst()
        }
    }

    @FXML fun editInvoice() = InvoiceDialog(this@InvoiceController, selected!!)
        .showAndWait()
        .ifPresent {
            transaction {
                findByDoc(Invoices, it)
                    .projection { plates + offsets + others + note + paid }
                    .update(it.plates, it.offsets, it.others, it.note, calculateDue(it) <= 0.0)
                reload(it)
            }
        }

    @FXML fun deleteInvoice() = yesNoAlert {
        transaction {
            findByDoc(Invoices, selected!!).remove()
            Payments.find { invoiceId.equal(selected!!.id) }.remove()
        }
        invoiceTable.items.remove(selected)
    }

    @FXML fun viewInvoice() = ViewInvoiceDialog(this, selected!!).show()

    @FXML fun addPayment() = AddPaymentDialog(this, _employee, selected!!).showAndWait().ifPresent {
        transaction {
            Payments.insert(it)
            updatePaymentStatus()
            reload(selected!!)
        }
    }

    @FXML fun deletePayment() = yesNoAlert {
        transaction {
            findById(Payments, selected2!!.id).remove()
            updatePaymentStatus()
            reload(selected!!)
        }
    }

    @FXML fun selectCustomer() = SearchCustomerDialog(this).showAndWait().ifPresent { customerProperty.set(it) }

    @FXML fun clearCustomer() = customerProperty.set(null)

    @FXML fun platePrice() = stage(getString(R.string.plate_price)) {
        initModality(APPLICATION_MODAL)
        val loader = FXMLLoader(getResource(R.layout.controller_price_plate), resources)
        scene = Scene(loader.pane).apply { style() }
        isResizable = false
        loader.controller._employee = _employee
    }.showAndWait()

    @FXML fun offsetPrice() = stage(getString(R.string.offset_price)) {
        initModality(APPLICATION_MODAL)
        val loader = FXMLLoader(getResource(R.layout.controller_price_offset), resources)
        scene = Scene(loader.pane).apply { style() }
        isResizable = false
        loader.controller._employee = _employee
    }.showAndWait()

    private fun MongoDBSession.updatePaymentStatus() = findByDoc(Invoices, selected!!)
        .projection { Invoices.paid }
        .update(calculateDue(selected!!) <= 0.0)

    private fun MongoDBSession.reload(invoice: Invoice) = invoiceTable.run {
        items.indexOf(invoice).let { index ->
            items[index] = findByDoc(Invoices, invoice).single()
            selectionModel.select(index)
        }
    }
}