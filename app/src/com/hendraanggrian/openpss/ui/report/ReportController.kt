package com.hendraanggrian.openpss.ui.report

import com.hendraanggrian.openpss.R
import com.hendraanggrian.openpss.currencyConverter
import com.hendraanggrian.openpss.db.schemas.Payments
import com.hendraanggrian.openpss.db.transaction
import com.hendraanggrian.openpss.io.properties.LoginFile
import com.hendraanggrian.openpss.scene.layout.MonthBox
import com.hendraanggrian.openpss.time.PATTERN_DATE
import com.hendraanggrian.openpss.time.toJava
import com.hendraanggrian.openpss.ui.Controller
import com.hendraanggrian.openpss.ui.Refreshable
import com.hendraanggrian.openpss.ui.main.MainController
import com.hendraanggrian.openpss.utils.matches
import com.hendraanggrian.openpss.utils.stringCell
import javafx.fxml.FXML
import javafx.scene.control.Button
import javafx.scene.control.TableColumn
import javafx.scene.control.TableView
import ktfx.coroutines.listener
import ktfx.coroutines.onAction
import ktfx.coroutines.onMouseClicked
import ktfx.layouts.contextMenu
import ktfx.scene.input.isDoubleClick
import java.net.URL
import java.util.Locale
import java.util.ResourceBundle

class ReportController : Controller(), Refreshable {

    companion object {
        const val EXTRA_MAIN_CONTROLLER = "EXTRA_MAIN_CONTROLLER"
    }

    @FXML lateinit var seePaymentsButton: Button
    @FXML lateinit var monthBox: MonthBox
    @FXML lateinit var reportTable: TableView<Report>
    @FXML lateinit var dateColumn: TableColumn<Report, String>
    @FXML lateinit var cashColumn: TableColumn<Report, String>
    @FXML lateinit var transferColumn: TableColumn<Report, String>
    @FXML lateinit var totalColumn: TableColumn<Report, String>

    override fun initialize(location: URL, resources: ResourceBundle) {
        super.initialize(location, resources)
        seePaymentsButton.bindToolbarButton()
        monthBox.setLocale(Locale(LoginFile.LANGUAGE))
        monthBox.valueProperty.listener { refresh() }
        reportTable.onMouseClicked { if (it.isDoubleClick()) seePayments() }
        reportTable.contextMenu { (getString(R.string.see_payments)) { onAction { seePayments() } } }
        dateColumn.stringCell { date.toString(PATTERN_DATE) }
        cashColumn.stringCell { currencyConverter.toString(cash) }
        transferColumn.stringCell { currencyConverter.toString(transfer) }
        totalColumn.stringCell { currencyConverter.toString(total) }
    }

    override fun refresh() {
        reportTable.items = transaction { Report.from(Payments.find { dateTime.matches(monthBox.value.toString()) }) }
    }

    @FXML fun seePayments() = getExtra<MainController>(EXTRA_MAIN_CONTROLLER).let {
        it.select(it.paymentController) { dateBox.picker.value = report.date.toJava() }
    }

    private inline val report: Report get() = reportTable.selectionModel.selectedItem

    private fun Button.bindToolbarButton() = disableProperty()
        .bind(reportTable.selectionModel.selectedItemProperty().isNull)
}