package com.hendraanggrian.openpss.ui.payment

import com.hendraanggrian.openpss.R
import com.hendraanggrian.openpss.controls.ViewInvoiceDialog
import com.hendraanggrian.openpss.db.schemas.Employees
import com.hendraanggrian.openpss.db.schemas.Invoices
import com.hendraanggrian.openpss.db.schemas.Payment
import com.hendraanggrian.openpss.db.schemas.Payment.Method.CASH
import com.hendraanggrian.openpss.db.schemas.Payment.Method.TRANSFER
import com.hendraanggrian.openpss.db.schemas.Payments
import com.hendraanggrian.openpss.db.transaction
import com.hendraanggrian.openpss.layouts.DateBox
import com.hendraanggrian.openpss.layouts.dateBox
import com.hendraanggrian.openpss.ui.FinancialController
import com.hendraanggrian.openpss.util.PATTERN_TIME
import com.hendraanggrian.openpss.util.currencyCell
import com.hendraanggrian.openpss.util.matches
import com.hendraanggrian.openpss.util.stringCell
import javafx.fxml.FXML
import javafx.scene.Node
import javafx.scene.control.Button
import javafx.scene.control.TableColumn
import javafx.scene.control.TableView
import javafx.scene.image.ImageView
import ktfx.application.later
import ktfx.collections.toMutableObservableList
import ktfx.coroutines.listener
import ktfx.coroutines.onAction
import ktfx.coroutines.onMouseClicked
import ktfx.layouts.button
import ktfx.layouts.separator
import ktfx.layouts.tooltip
import ktfx.scene.input.isDoubleClick
import java.net.URL
import java.util.ResourceBundle

class PaymentController : FinancialController<Payment>() {

    @FXML override lateinit var table: TableView<Payment>
    @FXML lateinit var noColumn: TableColumn<Payment, String>
    @FXML lateinit var timeColumn: TableColumn<Payment, String>
    @FXML lateinit var employeeColumn: TableColumn<Payment, String>
    @FXML lateinit var valueColumn: TableColumn<Payment, String>
    @FXML lateinit var methodColumn: TableColumn<Payment, String>

    private lateinit var refreshButton: Button
    private lateinit var viewInvoiceButton: Button
    override val leftSegment: List<Node> get() = listOf(refreshButton, separator(), viewInvoiceButton)

    lateinit var dateBox: DateBox
    override val rightSegment: List<Node> get() = listOf(dateBox)

    override fun initialize(location: URL, resources: ResourceBundle) {
        super.initialize(location, resources)
        refreshButton = button(graphic = ImageView(R.image.btn_refresh)) {
            tooltip(getString(R.string.refresh))
            onAction { refresh() }
        }
        viewInvoiceButton = button(graphic = ImageView(R.image.btn_invoice)) {
            tooltip(getString(R.string.view_invoice))
            onAction { viewInvoice() }
        }
        dateBox = dateBox()
        viewInvoiceButton.disableProperty().bind(!selectedBinding)
        dateBox.valueProperty.listener { refresh() }
        table.onMouseClicked { if (it.isDoubleClick() && selected != null) viewInvoice() }
        noColumn.stringCell { transaction { Invoices[invoiceId].single().no } }
        timeColumn.stringCell { dateTime.toString(PATTERN_TIME) }
        employeeColumn.stringCell { transaction { Employees[employeeId].single() } }
        valueColumn.currencyCell { value }
        methodColumn.stringCell { typedMethod.toString(this@PaymentController) }
    }

    override val List<Payment>.totalCash get(): Double = Payment.gather(this, CASH)

    override val List<Payment>.totalTransfer get(): Double = Payment.gather(this, TRANSFER)

    override fun refresh() = later {
        table.items = transaction {
            Payments { it.dateTime.matches(dateBox.value) }.toMutableObservableList()
        }
    }

    private fun viewInvoice() = ViewInvoiceDialog(this, transaction { Invoices[selected!!.invoiceId].single() }).show()
}