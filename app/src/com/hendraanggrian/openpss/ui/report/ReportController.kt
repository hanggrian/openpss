package com.hendraanggrian.openpss.ui.report

import com.hendraanggrian.openpss.currencyConverter
import com.hendraanggrian.openpss.scene.layout.DateBox
import com.hendraanggrian.openpss.time.PATTERN_DATE
import com.hendraanggrian.openpss.ui.Controller
import com.hendraanggrian.openpss.ui.Refreshable
import com.hendraanggrian.openpss.utils.stringCell
import javafx.fxml.FXML
import javafx.scene.control.TableColumn
import javafx.scene.control.TableView
import java.net.URL
import java.util.ResourceBundle

class ReportController : Controller(), Refreshable {

    @FXML lateinit var dateBox: DateBox
    @FXML lateinit var reportTable: TableView<Report>
    @FXML lateinit var dateColumn: TableColumn<Report, String>
    @FXML lateinit var cashColumn: TableColumn<Report, String>
    @FXML lateinit var transferColumn: TableColumn<Report, String>
    @FXML lateinit var totalColumn: TableColumn<Report, String>

    override fun initialize(location: URL, resources: ResourceBundle) {
        super.initialize(location, resources)
        dateColumn.stringCell { date.toString(PATTERN_DATE) }
        cashColumn.stringCell { currencyConverter.toString(cash) }
        transferColumn.stringCell { currencyConverter.toString(transfer) }
        totalColumn.stringCell { currencyConverter.toString(total) }
    }

    override fun refresh() {
        // reportTable.items = transaction { Payments.find { dateTime.matches(dateBox.date.toString()) }.map {  } }
    }
}