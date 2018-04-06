package com.hendraanggrian.openpss.ui.payment

import com.hendraanggrian.openpss.R
import com.hendraanggrian.openpss.currencyConverter
import com.hendraanggrian.openpss.db.findById
import com.hendraanggrian.openpss.db.schema.Employees
import com.hendraanggrian.openpss.db.schema.Payment
import com.hendraanggrian.openpss.db.schema.PaymentMethod
import com.hendraanggrian.openpss.db.schema.PaymentMethod.CASH
import com.hendraanggrian.openpss.db.schema.PaymentMethod.TRANSFER
import com.hendraanggrian.openpss.db.schema.Payments
import com.hendraanggrian.openpss.db.transaction
import com.hendraanggrian.openpss.scene.layout.DateBox
import com.hendraanggrian.openpss.time.PATTERN_TIME
import com.hendraanggrian.openpss.ui.Controller
import com.hendraanggrian.openpss.ui.Refreshable
import com.hendraanggrian.openpss.ui.main.MainController
import com.hendraanggrian.openpss.utils.currencyCell
import com.hendraanggrian.openpss.utils.getFont
import com.hendraanggrian.openpss.utils.stringCell
import javafx.fxml.FXML
import javafx.scene.control.Button
import javafx.scene.control.Label
import javafx.scene.control.TableColumn
import javafx.scene.control.TableView
import ktfx.beans.binding.stringBindingOf
import ktfx.collections.toMutableObservableList
import ktfx.coroutines.listener
import java.net.URL
import java.util.ResourceBundle

class PaymentController : Controller(), Refreshable {

    companion object {
        const val EXTRA_MAIN_CONTROLLER = "EXTRA_MAIN_CONTROLLER"
    }

    @FXML lateinit var seeReceiptButton: Button
    @FXML lateinit var dateBox: DateBox
    @FXML lateinit var totalCashLabel1: Label
    @FXML lateinit var totalCashLabel2: Label
    @FXML lateinit var totalTransferLabel1: Label
    @FXML lateinit var totalTransferLabel2: Label
    @FXML lateinit var totalAllLabel1: Label
    @FXML lateinit var totalAllLabel2: Label
    @FXML lateinit var paymentTable: TableView<Payment>
    @FXML lateinit var idColumn: TableColumn<Payment, String>
    @FXML lateinit var timeColumn: TableColumn<Payment, String>
    @FXML lateinit var employeeColumn: TableColumn<Payment, String>
    @FXML lateinit var valueColumn: TableColumn<Payment, String>
    @FXML lateinit var methodColumn: TableColumn<Payment, String>

    override fun initialize(location: URL, resources: ResourceBundle) {
        super.initialize(location, resources)
        seeReceiptButton.bindToolbarButton()
        dateBox.dateProperty.listener { refresh() }
        totalAllLabel1.font = getFont(R.font.opensans_bold)
        totalCashLabel1.font = getFont(R.font.opensans_bold)
        totalTransferLabel1.font = getFont(R.font.opensans_bold)
        totalCashLabel2.bindTotal(CASH)
        totalTransferLabel2.bindTotal(TRANSFER)
        totalAllLabel2.textProperty().bind(
            stringBindingOf(totalCashLabel2.textProperty(), totalTransferLabel2.textProperty()) {
                currencyConverter.toString(currencyConverter.fromString(totalCashLabel2.text).toDouble() +
                    currencyConverter.fromString(totalTransferLabel2.text).toDouble())
            })
        idColumn.stringCell { id }
        timeColumn.stringCell { dateTime.toString(PATTERN_TIME) }
        employeeColumn.stringCell { transaction { findById(Employees, employeeId).single() }!! }
        valueColumn.currencyCell { value }
        methodColumn.stringCell { getMethodDisplayText(this@PaymentController) }
    }

    override fun refresh() {
        paymentTable.items = transaction {
            Payments.find { dateTime.matches(dateBox.date.toString().toPattern()) }.toMutableObservableList()
        }
    }

    @FXML fun seeReceipt() {
        getExtra<MainController>(EXTRA_MAIN_CONTROLLER).tabPane.selectionModel.select(1)
    }

    private fun Label.bindTotal(method: PaymentMethod) = textProperty().bind(
        stringBindingOf(paymentTable.itemsProperty()) {
            currencyConverter.toString(paymentTable.items
                .filter { it.method == method }
                .sumByDouble { it.value })
        })

    private fun Button.bindToolbarButton() = disableProperty()
        .bind(paymentTable.selectionModel.selectedItemProperty().isNull)
}