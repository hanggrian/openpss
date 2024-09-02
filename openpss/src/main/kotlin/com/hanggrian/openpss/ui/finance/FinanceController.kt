package com.hanggrian.openpss.ui.finance

import com.hanggrian.openpss.PATTERN_DATE
import com.hanggrian.openpss.PATTERN_TIME
import com.hanggrian.openpss.R
import com.hanggrian.openpss.control.DateBox
import com.hanggrian.openpss.control.MonthBox
import com.hanggrian.openpss.db.schemas.Employees
import com.hanggrian.openpss.db.schemas.Invoices
import com.hanggrian.openpss.db.schemas.Payment
import com.hanggrian.openpss.db.schemas.Payments
import com.hanggrian.openpss.db.transaction
import com.hanggrian.openpss.io.properties.PreferencesFile
import com.hanggrian.openpss.ui.ActionController
import com.hanggrian.openpss.ui.Refreshable
import com.hanggrian.openpss.ui.invoice.ViewInvoicePopover
import com.hanggrian.openpss.util.currencyCell
import com.hanggrian.openpss.util.doneCell
import com.hanggrian.openpss.util.matches
import com.hanggrian.openpss.util.numberCell
import com.hanggrian.openpss.util.stringCell
import com.hanggrian.openpss.util.toJava
import com.jfoenix.effects.JFXDepthManager
import javafx.beans.binding.When
import javafx.fxml.FXML
import javafx.scene.Node
import javafx.scene.control.Button
import javafx.scene.control.MenuItem
import javafx.scene.control.TabPane
import javafx.scene.control.TableColumn
import javafx.scene.control.TableView
import javafx.scene.image.ImageView
import javafx.scene.layout.BorderPane
import ktfx.bindings.eq
import ktfx.collections.toMutableObservableList
import ktfx.controls.isSelected
import ktfx.controls.notSelectedBinding
import ktfx.coroutines.listener
import ktfx.coroutines.onAction
import ktfx.coroutines.onMouseClicked
import ktfx.inputs.isDoubleClick
import ktfx.jfoenix.layouts.styledJfxButton
import ktfx.layouts.NodeContainer
import ktfx.layouts.borderPane
import ktfx.layouts.tooltip
import ktfx.runLater
import java.net.URL
import java.util.ResourceBundle

class FinanceController :
    ActionController(),
    Refreshable {
    @FXML
    lateinit var tabPane: TabPane

    @FXML
    lateinit var dailyTable: TableView<Payment>

    @FXML
    lateinit var dailyNoColumn: TableColumn<Payment, String>

    @FXML
    lateinit var dailyTimeColumn: TableColumn<Payment, String>

    @FXML
    lateinit var dailyEmployeeColumn: TableColumn<Payment, String>

    @FXML
    lateinit var dailyValueColumn: TableColumn<Payment, String>

    @FXML
    lateinit var dailyCashColumn: TableColumn<Payment, Boolean>

    @FXML
    lateinit var dailyReferenceColumn: TableColumn<Payment, String>

    @FXML
    lateinit var viewInvoiceItem: MenuItem

    @FXML
    lateinit var monthlyTable: TableView<Report>

    @FXML
    lateinit var monthlyDateColumn: TableColumn<Report, String>

    @FXML
    lateinit var monthlyCashColumn: TableColumn<Report, String>

    @FXML
    lateinit var monthlyNonCashColumn: TableColumn<Report, String>

    @FXML
    lateinit var monthlyTotalColumn: TableColumn<Report, String>

    @FXML
    lateinit var viewPaymentsItem: MenuItem

    private lateinit var switchablePane: BorderPane
    private lateinit var refreshButton: Button
    private lateinit var viewTotalButton: Button

    private val dateBox: DateBox =
        DateBox().apply {
            valueProperty.listener { refresh() }
        }
    private val monthBox: MonthBox =
        MonthBox().apply {
            setLocale(PreferencesFile.language.toLocale())
            valueProperty.listener { refresh() }
        }

    override fun NodeContainer.onCreateActions() {
        refreshButton =
            styledJfxButton(null, ImageView(R.image_act_refresh), R.style_flat) {
                tooltip(getString(R.string_refresh))
                onAction { refresh() }
            }
        viewTotalButton =
            styledJfxButton(null, ImageView(R.image_act_money), R.style_flat) {
                tooltip(getString(R.string_total))
                onAction { viewTotal() }
            }
        switchablePane = borderPane()
    }

    override fun initialize(location: URL, resources: ResourceBundle) {
        super.initialize(location, resources)
        JFXDepthManager.setDepth(tabPane, 0)
        switchablePane.centerProperty().bind(
            When(tabPane.selectionModel.selectedIndexProperty() eq 0)
                .then<Node>(dateBox)
                .otherwise(monthBox),
        )

        dailyNoColumn.numberCell(this) { transaction { Invoices[invoiceId].single().no } }
        dailyTimeColumn.stringCell { dateTime.toString(PATTERN_TIME) }
        dailyEmployeeColumn.stringCell {
            transaction {
                Employees[employeeId]
                    .singleOrNull()
                    ?.toString()
                    .orEmpty()
            }
        }
        dailyValueColumn.currencyCell(this) { value }
        dailyCashColumn.doneCell { isCash() }
        dailyReferenceColumn.stringCell { reference }
        viewInvoiceItem
            .disableProperty()
            .bind(dailyTable.selectionModel.notSelectedBinding)
        dailyTable.onMouseClicked {
            if (it.isDoubleClick() && dailyTable.selectionModel.isSelected()) {
                viewInvoice()
            }
        }

        monthlyDateColumn.stringCell { date.toString(PATTERN_DATE) }
        monthlyCashColumn.currencyCell(this) { cash }
        monthlyNonCashColumn.currencyCell(this) { nonCash }
        monthlyTotalColumn.currencyCell(this) { total }
        viewPaymentsItem
            .disableProperty()
            .bind(monthlyTable.selectionModel.notSelectedBinding)
        monthlyTable.onMouseClicked {
            if (it.isDoubleClick() && monthlyTable.selectionModel.isSelected()) {
                viewPayments()
            }
        }

        tabPane.selectionModel.selectedIndexProperty().listener { refresh() }
    }

    override fun refresh() =
        runLater {
            transaction {
                when (tabPane.selectionModel.selectedIndex) {
                    0 ->
                        dailyTable.items =
                            Payments { it.dateTime.matches(dateBox.value!!) }
                                .toMutableObservableList()
                    else ->
                        monthlyTable.items =
                            Report.listAll(Payments { it.dateTime.matches(monthBox.value!!) })
                }
            }
        }

    @FXML
    fun viewInvoice() =
        ViewInvoicePopover(
            this,
            transaction { Invoices[dailyTable.selectionModel.selectedItem.invoiceId].single() },
        ).show(
            when (tabPane.selectionModel.selectedIndex) {
                0 -> dailyTable
                else -> monthlyTable
            },
        )

    @FXML
    fun viewPayments() {
        tabPane.selectionModel.selectFirst()
        dateBox.picker.value = monthlyTable.selectionModel.selectedItem.date.toJava()
    }

    private fun viewTotal() =
        ViewTotalPopover(this, getTotal(true), getTotal(false)).show(viewTotalButton)

    private fun getTotal(isCash: Boolean) =
        when (tabPane.selectionModel.selectedIndex) {
            0 -> Payment.gather(dailyTable.items, isCash)
            else ->
                monthlyTable.items.sumOf {
                    when {
                        isCash -> it.cash
                        else -> it.nonCash
                    }
                }
        }

    companion object {
        const val EXTRA_MAIN_CONTROLLER = "EXTRA_MAIN_CONTROLLER"
    }
}
