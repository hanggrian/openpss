package com.hendraanggrian.openpss.ui.invoice

import com.hendraanggrian.openpss.R
import com.hendraanggrian.openpss.db.buildQuery
import com.hendraanggrian.openpss.db.findByDoc
import com.hendraanggrian.openpss.db.findById
import com.hendraanggrian.openpss.db.schemas.Customer
import com.hendraanggrian.openpss.db.schemas.Customers
import com.hendraanggrian.openpss.db.schemas.Employees
import com.hendraanggrian.openpss.db.schemas.Invoice
import com.hendraanggrian.openpss.db.schemas.Invoices
import com.hendraanggrian.openpss.db.schemas.Payment
import com.hendraanggrian.openpss.db.schemas.Payments
import com.hendraanggrian.openpss.db.schemas.calculateDue
import com.hendraanggrian.openpss.db.transaction
import com.hendraanggrian.openpss.scene.control.CountBox
import com.hendraanggrian.openpss.scene.layout.DateBox
import com.hendraanggrian.openpss.time.PATTERN_DATETIME_EXTENDED
import com.hendraanggrian.openpss.ui.Controller
import com.hendraanggrian.openpss.ui.Refreshable
import com.hendraanggrian.openpss.ui.SeeInvoiceDialog
import com.hendraanggrian.openpss.ui.controller
import com.hendraanggrian.openpss.ui.pane
import com.hendraanggrian.openpss.ui.yesNoAlert
import com.hendraanggrian.openpss.utils.currencyCell
import com.hendraanggrian.openpss.utils.doneCell
import com.hendraanggrian.openpss.utils.getResource
import com.hendraanggrian.openpss.utils.stringCell
import javafx.beans.property.SimpleObjectProperty
import javafx.fxml.FXML
import javafx.fxml.FXMLLoader
import javafx.scene.Node
import javafx.scene.Scene
import javafx.scene.control.Button
import javafx.scene.control.ChoiceBox
import javafx.scene.control.Label
import javafx.scene.control.MenuItem
import javafx.scene.control.Pagination
import javafx.scene.control.RadioButton
import javafx.scene.control.SplitMenuButton
import javafx.scene.control.TableColumn
import javafx.scene.control.TableView
import javafx.scene.control.TableView.CONSTRAINED_RESIZE_POLICY
import javafx.stage.Modality.APPLICATION_MODAL
import javafx.util.Callback
import kotlinx.nosql.equal
import kotlinx.nosql.mongodb.MongoDBSession
import kotlinx.nosql.update
import ktfx.application.later
import ktfx.beans.binding.bindingOf
import ktfx.beans.binding.stringBindingOf
import ktfx.beans.property.toProperty
import ktfx.beans.value.or
import ktfx.collections.emptyObservableList
import ktfx.collections.isEmpty
import ktfx.collections.toMutableObservableList
import ktfx.collections.toObservableList
import ktfx.coroutines.onAction
import ktfx.coroutines.onMouseClicked
import ktfx.layouts.columns
import ktfx.layouts.contextMenu
import ktfx.layouts.separatorMenuItem
import ktfx.layouts.tableView
import ktfx.scene.input.isDoubleClick
import ktfx.stage.stage
import java.net.URL
import java.util.ResourceBundle
import kotlin.math.ceil

class InvoiceController : Controller(), Refreshable {

    @FXML lateinit var addPaymentButton: Button
    @FXML lateinit var seeInvoiceButton: Button
    @FXML lateinit var customerButton: SplitMenuButton
    @FXML lateinit var customerButtonItem: MenuItem
    @FXML lateinit var countBox: CountBox
    @FXML lateinit var statusBox: ChoiceBox<String>
    @FXML lateinit var allDateRadio: RadioButton
    @FXML lateinit var pickDateRadio: RadioButton
    @FXML lateinit var dateBox: DateBox
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

        customerButton.textProperty().bind(stringBindingOf(customerProperty) {
            customerProperty.value?.toString() ?: getString(R.string.search_customer)
        })
        customerButtonItem.disableProperty().bind(customerProperty.isNull)

        countBox.desc = getString(R.string.items)
        statusBox.items = listOf(R.string.any, R.string.unpaid, R.string.paid).map { getString(it) }.toObservableList()
        statusBox.selectionModel.selectFirst()
        pickDateRadio.graphic.disableProperty().bind(!pickDateRadio.selectedProperty())

        paymentTable.contextMenu { (getString(R.string.add)) { onAction { addPayment() } } }
        paymentDateTimeColumn.stringCell { dateTime.toString(PATTERN_DATETIME_EXTENDED) }
        paymentEmployeeColumn.stringCell { transaction { findById(Employees, employeeId).single() }!! }
        paymentValueColumn.currencyCell { value }
        paymentMethodColumn.stringCell { getMethodDisplayText(this@InvoiceController) }
    }

    override fun refresh() = invoicePagination.pageFactoryProperty()
        .bind(bindingOf(customerProperty, countBox.countProperty, statusBox.valueProperty(),
            allDateRadio.selectedProperty(), pickDateRadio.selectedProperty(), dateBox.dateProperty) {
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
                        getString(R.string.paid)<Boolean> { doneCell { paid } }
                        getString(R.string.print)<Boolean> { doneCell { printed } }
                    }
                    onMouseClicked { if (it.isDoubleClick()) seeInvoice() }
                    contextMenu {
                        (getString(R.string.add)) { onAction { addInvoice() } }
                        separatorMenuItem()
                        (getString(R.string.see_invoice)) { onAction { seeInvoice() } }
                        (getString(R.string.edit)) {
                            bindDisable()
                            onAction {
                                InvoiceDialog(this@InvoiceController, invoiceTable.selectionModel.selectedItem)
                                    .showAndWait()
                            }
                        }
                        (getString(R.string.delete)) {
                            bindDisable()
                            onAction {
                                yesNoAlert {
                                    invoiceTable.selectionModel.selectedItem.let {
                                        transaction { findByDoc(Invoices, it).remove() }
                                        invoiceTable.items.remove(it)
                                    }
                                }
                            }
                        }
                    }
                    later {
                        transaction {
                            val invoices = Invoices.find {
                                buildQuery {
                                    if (customerProperty.value != null)
                                        and(customerId.equal(customerProperty.value.id))
                                    when (statusBox.value) {
                                        getString(R.string.paid) -> and(paid.equal(true))
                                        getString(R.string.unpaid) -> and(paid.equal(false))
                                    }
                                    if (pickDateRadio.isSelected)
                                        and(dateTime.matches(dateBox.date.toString().toPattern()))
                                }
                            }
                            invoicePagination.pageCount = ceil(invoices.count() / countBox.count.toDouble()).toInt()
                            items = invoices.skip(countBox.count * page).take(countBox.count).toMutableObservableList()
                        }
                    }
                }
                addPaymentButton.bindDisable()
                seeInvoiceButton.bindDisable()
                paymentTable.itemsProperty().bind(bindingOf(invoiceTable.selectionModel.selectedItemProperty()) {
                    if (invoice == null) emptyObservableList()
                    else transaction { Payments.find { invoiceId.equal(invoice!!.id) }.toObservableList() }!!
                })
                coverLabel.visibleProperty().bind(invoiceTable.selectionModel.selectedItemProperty().isNull)
                invoiceTable
            }
        })

    @FXML fun addInvoice() = InvoiceDialog(this).showAndWait().ifPresent {
        transaction {
            it.id = Invoices.insert(it)
            invoiceTable.items.add(it)
            invoiceTable.selectionModel.selectFirst()
        }
    }

    @FXML fun addPayment() = AddPaymentDialog(this, _employee, invoice!!).showAndWait().ifPresent {
        transaction {
            it.id = Payments.insert(it)
            if (calculateDue(invoice!!) == 0.0)
                findByDoc(Invoices, invoice!!).projection { paid }.update(true)
            reload(invoice!!)
        }
    }

    @FXML fun seeInvoice() = SeeInvoiceDialog(this, invoice!!).show()

    @FXML fun selectCustomer() = SearchCustomerDialog(this).showAndWait().ifPresent { customerProperty.set(it) }

    @FXML fun clearCustomer() = customerProperty.set(null)

    @FXML fun platePrice() = stage(getString(R.string.plate_price)) {
        initModality(APPLICATION_MODAL)
        val loader = FXMLLoader(getResource(R.layout.controller_price_plate), resources)
        scene = Scene(loader.pane)
        isResizable = false
        loader.controller._employee = _employee
    }.showAndWait()

    @FXML fun offsetPrice() = stage(getString(R.string.offset_price)) {
        initModality(APPLICATION_MODAL)
        val loader = FXMLLoader(getResource(R.layout.controller_price_offset), resources)
        scene = Scene(loader.pane)
        isResizable = false
        loader.controller._employee = _employee
    }.showAndWait()

    private inline val invoice: Invoice? get() = invoiceTable.selectionModel.selectedItem

    private fun Button.bindDisable() = disableProperty().bind(invoiceTable.selectionModel.selectedItemProperty().isNull)

    private fun MenuItem.bindDisable() = later {
        disableProperty().bind(invoiceTable.selectionModel.selectedItems.isEmpty or !isFullAccess.toProperty())
    }

    private fun MongoDBSession.reload(invoice: Invoice) = invoiceTable.run {
        items.indexOf(invoice).let { index ->
            items[items.indexOf(invoice)] = findByDoc(Invoices, invoice).single()
            selectionModel.select(index)
        }
    }
}