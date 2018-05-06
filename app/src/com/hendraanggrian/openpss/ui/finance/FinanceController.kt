package com.hendraanggrian.openpss.ui.finance

import com.hendraanggrian.openpss.App.Companion.STYLE_DEFAULT_BUTTON
import com.hendraanggrian.openpss.R
import com.hendraanggrian.openpss.controls.Popup
import com.hendraanggrian.openpss.db.schemas.Payment
import com.hendraanggrian.openpss.db.schemas.Payment.Method.CASH
import com.hendraanggrian.openpss.db.schemas.Payment.Method.TRANSFER
import com.hendraanggrian.openpss.layouts.HiddenTabPane
import com.hendraanggrian.openpss.resources.Resourced
import com.hendraanggrian.openpss.ui.Refreshable
import com.hendraanggrian.openpss.ui.SegmentedController
import com.hendraanggrian.openpss.ui.Selectable
import com.hendraanggrian.openpss.ui.report.Report
import com.hendraanggrian.openpss.util.currencyConverter
import javafx.fxml.FXML
import javafx.geometry.Orientation.VERTICAL
import javafx.scene.Node
import javafx.scene.control.Button
import javafx.scene.control.SelectionModel
import javafx.scene.control.Tab
import javafx.scene.control.TableColumn
import javafx.scene.control.TableView
import javafx.scene.image.ImageView
import ktfx.coroutines.onAction
import ktfx.layouts.button
import ktfx.layouts.gridPane
import ktfx.layouts.label
import ktfx.layouts.separator
import ktfx.layouts.styledButton
import ktfx.scene.layout.gap
import java.net.URL
import java.util.ResourceBundle

class FinanceController : SegmentedController(), Refreshable, Selectable<Tab> {

    @FXML lateinit var tabPane: HiddenTabPane

    @FXML lateinit var dailyTable: TableView<Payment>
    @FXML lateinit var dailyNoColumn: TableColumn<Payment, String>
    @FXML lateinit var dailyTimeColumn: TableColumn<Payment, String>
    @FXML lateinit var dailyEmployeeColumn: TableColumn<Payment, String>
    @FXML lateinit var dailyValueColumn: TableColumn<Payment, String>
    @FXML lateinit var dailyMethodColumn: TableColumn<Payment, String>

    @FXML lateinit var monthlyTable: TableView<Report>
    @FXML lateinit var monthlyDateColumn: TableColumn<Report, String>
    @FXML lateinit var monthlyCashColumn: TableColumn<Report, String>
    @FXML lateinit var monthlyTransferColumn: TableColumn<Report, String>
    @FXML lateinit var monthlyTotalColumn: TableColumn<Report, String>

    private lateinit var refreshButton: Button
    private lateinit var viewTotalButton: Button
    override val leftButtons: List<Node> get() = listOf(refreshButton, separator(VERTICAL), viewTotalButton)

    override val rightButtons: List<Node> = mutableListOf()

    override fun initialize(location: URL, resources: ResourceBundle) {
        super.initialize(location, resources)
        refreshButton = button(getString(R.string.refresh), ImageView(R.image.btn_refresh)) { onAction { refresh() } }
        viewTotalButton = styledButton(STYLE_DEFAULT_BUTTON, getString(R.string.view_total),
            ImageView(R.image.btn_money_dark)) {
            onAction { ViewTotalPopup(this@FinanceController).show(this@styledButton) }
        }
        rightButtons as MutableList<Node> += tabPane.segmentedButton
    }

    override val selectionModel: SelectionModel<Tab> get() = tabPane.selectionModel

    override fun refresh() {
    }

    val totalCash: Double
        get() = when (selectedIndex) {
            0 -> Payment.gather(dailyTable.items, CASH)
            else -> monthlyTable.items.sumByDouble { it.cash }
        }

    val totalTransfer: Double
        get() = when (selectedIndex) {
            0 -> Payment.gather(dailyTable.items, TRANSFER)
            else -> monthlyTable.items.sumByDouble { it.transfer }
        }

    private inner class ViewTotalPopup(resourced: Resourced) : Popup<Nothing>(resourced, R.string.view_total) {

        override val content: Node
            get() = gridPane {
                gap = 8.0
                label(getString(R.string.cash)) col 0 row 0
                label(currencyConverter.toString(totalCash)) col 1 row 0
                label(getString(R.string.cash)) col 0 row 1
                label(currencyConverter.toString(totalTransfer)) col 1 row 1
                label(getString(R.string.cash)) col 0 row 2
                label(currencyConverter.toString(totalCash + totalTransfer)) col 1 row 2
            }
    }
}