package com.hendraanggrian.openpss.ui.report

import com.hendraanggrian.openpss.R
import com.hendraanggrian.openpss.db.schemas.Payments
import com.hendraanggrian.openpss.db.transaction
import com.hendraanggrian.openpss.io.properties.LoginFile
import com.hendraanggrian.openpss.layouts.MonthBox
import com.hendraanggrian.openpss.layouts.monthBox
import com.hendraanggrian.openpss.ui.FinancialController
import com.hendraanggrian.openpss.ui.main.MainController
import com.hendraanggrian.openpss.util.PATTERN_DATE
import com.hendraanggrian.openpss.util.currencyConverter
import com.hendraanggrian.openpss.util.matches
import com.hendraanggrian.openpss.util.stringCell
import com.hendraanggrian.openpss.util.toJava
import javafx.fxml.FXML
import javafx.scene.Node
import javafx.scene.control.Button
import javafx.scene.control.TableColumn
import javafx.scene.control.TableView
import javafx.scene.image.ImageView
import ktfx.application.later
import ktfx.coroutines.listener
import ktfx.coroutines.onAction
import ktfx.coroutines.onMouseClicked
import ktfx.layouts.button
import ktfx.layouts.separator
import ktfx.layouts.tooltip
import ktfx.scene.input.isDoubleClick
import java.net.URL
import java.util.Locale
import java.util.ResourceBundle

class ReportController : FinancialController<Report>() {

    companion object {
        const val EXTRA_MAIN_CONTROLLER = "EXTRA_MAIN_CONTROLLER"
    }

    @FXML override lateinit var table: TableView<Report>
    @FXML lateinit var dateColumn: TableColumn<Report, String>
    @FXML lateinit var cashColumn: TableColumn<Report, String>
    @FXML lateinit var transferColumn: TableColumn<Report, String>
    @FXML lateinit var totalColumn: TableColumn<Report, String>

    private lateinit var refreshButton: Button
    private lateinit var viewPaymentsButton: Button
    override val leftSegment: List<Node> get() = listOf(refreshButton, separator(), viewPaymentsButton)

    private lateinit var monthBox: MonthBox
    override val rightSegment: List<Node> get() = listOf(monthBox)

    override fun initialize(location: URL, resources: ResourceBundle) {
        super.initialize(location, resources)
        refreshButton = button(graphic = ImageView(R.image.btn_refresh)) {
            tooltip(getString(R.string.refresh))
            onAction { refresh() }
        }
        viewPaymentsButton = button(graphic = ImageView(R.image.btn_payment)) {
            tooltip(getString(R.string.view_payments))
            onAction { viewPayments() }
            disableProperty().bind(!selectedBinding)
        }
        monthBox = monthBox {
            setLocale(Locale(LoginFile.LANGUAGE))
            valueProperty.listener { refresh() }
        }
        table.onMouseClicked { if (it.isDoubleClick() && selected != null) viewPayments() }
        dateColumn.stringCell { date.toString(PATTERN_DATE) }
        cashColumn.stringCell { currencyConverter.toString(cash) }
        transferColumn.stringCell { currencyConverter.toString(transfer) }
        totalColumn.stringCell { currencyConverter.toString(total) }
    }

    override val List<Report>.totalCash get(): Double = sumByDouble { it.cash }

    override val List<Report>.totalTransfer get(): Double = sumByDouble { it.transfer }

    override fun refresh() = later {
        table.items = transaction { Report.listAll(Payments { it.dateTime.matches(monthBox.value) }) }
    }

    private fun viewPayments() = getExtra<MainController>(EXTRA_MAIN_CONTROLLER).run {
        select(paymentController) { dateBox.picker.value = this@ReportController.selected!!.date.toJava() }
    }
}