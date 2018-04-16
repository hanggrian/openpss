package com.hendraanggrian.openpss.ui.payment

import com.hendraanggrian.openpss.db.schemas.Employees
import com.hendraanggrian.openpss.db.schemas.Invoices
import com.hendraanggrian.openpss.db.schemas.Payment
import com.hendraanggrian.openpss.db.schemas.Payment.Method.CASH
import com.hendraanggrian.openpss.db.schemas.Payment.Method.TRANSFER
import com.hendraanggrian.openpss.db.schemas.Payments
import com.hendraanggrian.openpss.db.transaction
import com.hendraanggrian.openpss.scene.layout.DateBox
import com.hendraanggrian.openpss.time.PATTERN_TIME
import com.hendraanggrian.openpss.ui.FinancialController
import com.hendraanggrian.openpss.ui.ViewInvoiceDialog
import com.hendraanggrian.openpss.utils.currencyCell
import com.hendraanggrian.openpss.utils.findById
import com.hendraanggrian.openpss.utils.stringCell
import javafx.collections.ObservableList
import javafx.fxml.FXML
import javafx.scene.control.Button
import javafx.scene.control.TableColumn
import javafx.scene.control.TableView
import ktfx.collections.toMutableObservableList
import ktfx.coroutines.listener
import ktfx.coroutines.onMouseClicked
import ktfx.scene.input.isDoubleClick
import java.net.URL
import java.util.ResourceBundle

class PaymentController : FinancialController<Payment>() {

    @FXML lateinit var viewInvoiceButton: Button
    @FXML lateinit var dateBox: DateBox
    @FXML lateinit var paymentTable: TableView<Payment>
    @FXML lateinit var noColumn: TableColumn<Payment, String>
    @FXML lateinit var timeColumn: TableColumn<Payment, String>
    @FXML lateinit var employeeColumn: TableColumn<Payment, String>
    @FXML lateinit var valueColumn: TableColumn<Payment, String>
    @FXML lateinit var methodColumn: TableColumn<Payment, String>

    override fun initialize(location: URL, resources: ResourceBundle) {
        super.initialize(location, resources)
        viewInvoiceButton.disableProperty().bind(!selectedBinding)
        dateBox.valueProperty.listener { refresh() }
        paymentTable.onMouseClicked { if (it.isDoubleClick()) viewInvoice() }
        noColumn.stringCell { transaction { findById(Invoices, invoiceId).single().no }!! }
        timeColumn.stringCell { dateTime.toString(PATTERN_TIME) }
        employeeColumn.stringCell { transaction { findById(Employees, employeeId).single() }!! }
        valueColumn.currencyCell { value }
        methodColumn.stringCell { getMethodText(this@PaymentController) }
    }

    override val table: TableView<Payment> get() = paymentTable

    override fun ObservableList<Payment>.getTotalCash(): Double = filter { it.method == CASH }
        .sumByDouble { it.value }

    override fun ObservableList<Payment>.getTransferCash(): Double = filter { it.method == TRANSFER }
        .sumByDouble { it.value }

    override fun refresh() {
        paymentTable.items = transaction {
            Payments.find { dateTime.matches(dateBox.value.toString().toPattern()) }.toMutableObservableList()
        }
    }

    @FXML fun viewInvoice() = ViewInvoiceDialog(this,
        transaction { findById(Invoices, selected!!.invoiceId).single() }!!).show()
}