package com.hendraanggrian.openpss.ui.finance

import com.hendraanggrian.openpss.App.Companion.STYLE_DEFAULT_BUTTON
import com.hendraanggrian.openpss.R
import com.hendraanggrian.openpss.controls.adaptableButton
import com.hendraanggrian.openpss.controls.styledAdaptableButton
import com.hendraanggrian.openpss.db.schemas.Payment
import com.hendraanggrian.openpss.db.schemas.Payment.Method.CASH
import com.hendraanggrian.openpss.db.schemas.Payment.Method.TRANSFER
import com.hendraanggrian.openpss.io.properties.LoginFile
import com.hendraanggrian.openpss.layouts.DateBox
import com.hendraanggrian.openpss.layouts.MonthBox
import com.hendraanggrian.openpss.layouts.SegmentedTabPane
import com.hendraanggrian.openpss.ui.Refreshable
import com.hendraanggrian.openpss.ui.SegmentedController
import com.hendraanggrian.openpss.ui.Selectable
import com.hendraanggrian.openpss.ui.report.Report
import javafx.fxml.FXML
import javafx.geometry.Orientation.VERTICAL
import javafx.scene.Node
import javafx.scene.control.Button
import javafx.scene.control.SelectionModel
import javafx.scene.control.Tab
import javafx.scene.control.TableColumn
import javafx.scene.control.TableView
import ktfx.coroutines.listener
import ktfx.coroutines.onAction
import ktfx.layouts.separator
import org.controlsfx.control.SegmentedButton
import java.net.URL
import java.util.Locale
import java.util.ResourceBundle

class FinanceController : SegmentedController(), Refreshable, Selectable<Tab> {

    @FXML lateinit var tabPane: SegmentedTabPane

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
    override val leftButtons: List<Node> = mutableListOf()

    private lateinit var dateBox: DateBox
    private lateinit var monthBox: MonthBox
    private lateinit var viewTotalButton: Button
    override val rightButtons: List<Node> = mutableListOf()

    override fun initialize(location: URL, resources: ResourceBundle) {
        super.initialize(location, resources)
        refreshButton = adaptableButton(getString(R.string.refresh), R.image.btn_refresh_light) {
            onAction { refresh() }
        }
        dateBox = com.hendraanggrian.openpss.layouts.dateBox()
        monthBox = com.hendraanggrian.openpss.layouts.monthBox {
            setLocale(Locale(LoginFile.LANGUAGE))
            valueProperty.listener { refresh() }
        }
        viewTotalButton = styledAdaptableButton(STYLE_DEFAULT_BUTTON,
            getString(R.string.total), R.image.btn_money_dark) {
            onAction {
                ViewTotalPopup(this@FinanceController, totalCash, totalTransfer).show(this@styledAdaptableButton)
            }
        }
        tabPane.header = SegmentedButton()
        leftButtons.addAll(tabPane.header, separator(VERTICAL), refreshButton, viewTotalButton)
        tabPane.header.toggleGroup.run {
            selectedToggleProperty().addListener { _, _, toggle ->
                (rightButtons as MutableList).clear()
                when (toggles.indexOf(toggle)) {
                    0 -> rightButtons.addAll(dateBox)
                    else -> rightButtons.addAll(monthBox)
                }
            }
        }
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

    private fun List<Node>.addAll(vararg buttons: Node) {
        this as MutableList
        buttons.forEach { this += it }
    }
}