package com.hendraanggrian.openpss.ui.report

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
import javafx.scene.control.TableColumn
import javafx.scene.control.TableView
import ktfx.application.later
import ktfx.coroutines.listener
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

    private lateinit var monthBox: MonthBox
    override val rightButtons: List<Node> get() = listOf(monthBox)

    override fun initialize(location: URL, resources: ResourceBundle) {
        super.initialize(location, resources)
        monthBox = monthBox {
            setLocale(Locale(LoginFile.LANGUAGE))
            valueProperty.listener { refresh() }
        }
        dateColumn.stringCell { date.toString(PATTERN_DATE) }
        cashColumn.stringCell { currencyConverter.toString(cash) }
        transferColumn.stringCell { currencyConverter.toString(transfer) }
        totalColumn.stringCell { currencyConverter.toString(total) }
    }

    override fun view(item: Report) {
        getExtra<MainController>(EXTRA_MAIN_CONTROLLER).run {
            select(paymentController) { dateBox.picker.value = item.date.toJava() }
        }
    }

    override val List<Report>.totalCash get(): Double = sumByDouble { it.cash }

    override val List<Report>.totalTransfer get(): Double = sumByDouble { it.transfer }

    override fun refresh() = later {
        table.items = transaction { Report.listAll(Payments { it.dateTime.matches(monthBox.value) }) }
    }
}