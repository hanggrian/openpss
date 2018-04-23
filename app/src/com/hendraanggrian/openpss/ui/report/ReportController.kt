package com.hendraanggrian.openpss.ui.report

import com.hendraanggrian.openpss.db.schemas.Payments
import com.hendraanggrian.openpss.db.transaction
import com.hendraanggrian.openpss.io.properties.LoginFile
import com.hendraanggrian.openpss.layouts.MonthBox
import com.hendraanggrian.openpss.core.PATTERN_DATE
import com.hendraanggrian.openpss.core.toJava
import com.hendraanggrian.openpss.ui.FinancialController
import com.hendraanggrian.openpss.ui.main.MainController
import com.hendraanggrian.openpss.utils.currencyConverter
import com.hendraanggrian.openpss.utils.matches
import com.hendraanggrian.openpss.utils.stringCell
import javafx.fxml.FXML
import javafx.scene.control.Button
import javafx.scene.control.TableColumn
import javafx.scene.control.TableView
import ktfx.coroutines.listener
import ktfx.coroutines.onMouseClicked
import ktfx.scene.input.isDoubleClick
import java.net.URL
import java.util.Locale
import java.util.ResourceBundle

class ReportController : FinancialController<Report>() {

    companion object {
        const val EXTRA_MAIN_CONTROLLER = "EXTRA_MAIN_CONTROLLER"
    }

    @FXML lateinit var viewPaymentsButton: Button
    @FXML lateinit var monthBox: MonthBox
    @FXML lateinit var reportTable: TableView<Report>
    @FXML lateinit var dateColumn: TableColumn<Report, String>
    @FXML lateinit var cashColumn: TableColumn<Report, String>
    @FXML lateinit var transferColumn: TableColumn<Report, String>
    @FXML lateinit var totalColumn: TableColumn<Report, String>

    override fun initialize(location: URL, resources: ResourceBundle) {
        super.initialize(location, resources)
        viewPaymentsButton.disableProperty().bind(!selectedBinding)
        monthBox.setLocale(Locale(LoginFile.LANGUAGE))
        monthBox.valueProperty.listener { refresh() }
        reportTable.onMouseClicked { if (it.isDoubleClick() && selected != null) viewPayments() }
        dateColumn.stringCell { date.toString(PATTERN_DATE) }
        cashColumn.stringCell { currencyConverter.toString(cash) }
        transferColumn.stringCell { currencyConverter.toString(transfer) }
        totalColumn.stringCell { currencyConverter.toString(total) }
    }

    override val table: TableView<Report> get() = reportTable

    override fun List<Report>.getTotalCash(): Double = sumByDouble { it.cash }

    override fun List<Report>.getTransferCash(): Double = sumByDouble { it.transfer }

    override fun refresh() {
        reportTable.items = transaction { Report.listAll(Payments.find { dateTime.matches(monthBox.value) }) }
    }

    @FXML fun viewPayments() = getExtra<MainController>(EXTRA_MAIN_CONTROLLER).run {
        select(paymentController) { dateBox.picker.value = this@ReportController.selected!!.date.toJava() }
    }
}