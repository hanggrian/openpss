package com.hendraanggrian.openpss.ui.finance

import com.hendraanggrian.openpss.PATTERN_DATE
import com.hendraanggrian.openpss.PATTERN_TIME
import com.hendraanggrian.openpss.R
import com.hendraanggrian.openpss.R2
import com.hendraanggrian.openpss.control.DateBox
import com.hendraanggrian.openpss.control.MonthBox
import com.hendraanggrian.openpss.control.action
import com.hendraanggrian.openpss.data.Payment
import com.hendraanggrian.openpss.language
import com.hendraanggrian.openpss.ui.ActionController
import com.hendraanggrian.openpss.ui.Refreshable
import com.hendraanggrian.openpss.ui.invoice.ViewInvoicePopOver
import com.hendraanggrian.openpss.util.currencyCell
import com.hendraanggrian.openpss.util.doneCell
import com.hendraanggrian.openpss.util.numberCell
import com.hendraanggrian.openpss.util.stringCell
import com.hendraanggrian.openpss.util.toJava
import javafx.beans.binding.When
import javafx.fxml.FXML
import javafx.scene.Node
import javafx.scene.control.Button
import javafx.scene.control.MenuItem
import javafx.scene.control.TabPane
import javafx.scene.control.TableColumn
import javafx.scene.control.TableView
import javafx.scene.layout.BorderPane
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import ktfx.bindings.eq
import ktfx.collections.toMutableObservableList
import ktfx.controls.isSelected
import ktfx.coroutines.listener
import ktfx.coroutines.onAction
import ktfx.coroutines.onMouseClicked
import ktfx.inputs.isDoubleClick
import ktfx.layouts.NodeInvokable
import ktfx.layouts.borderPane
import ktfx.runLater
import java.net.URL
import java.util.ResourceBundle

class FinanceController : ActionController(), Refreshable {

    companion object {
        const val EXTRA_MAIN_CONTROLLER = "EXTRA_MAIN_CONTROLLER"
    }

    @FXML lateinit var tabPane: TabPane

    @FXML lateinit var dailyTable: TableView<Payment>
    @FXML lateinit var dailyNoColumn: TableColumn<Payment, String>
    @FXML lateinit var dailyTimeColumn: TableColumn<Payment, String>
    @FXML lateinit var dailyEmployeeColumn: TableColumn<Payment, String>
    @FXML lateinit var dailyValueColumn: TableColumn<Payment, String>
    @FXML lateinit var dailyCashColumn: TableColumn<Payment, Boolean>
    @FXML lateinit var dailyReferenceColumn: TableColumn<Payment, String>
    @FXML lateinit var viewInvoiceItem: MenuItem

    @FXML lateinit var monthlyTable: TableView<Report>
    @FXML lateinit var monthlyDateColumn: TableColumn<Report, String>
    @FXML lateinit var monthlyCashColumn: TableColumn<Report, String>
    @FXML lateinit var monthlyNonCashColumn: TableColumn<Report, String>
    @FXML lateinit var monthlyTotalColumn: TableColumn<Report, String>
    @FXML lateinit var viewPaymentsItem: MenuItem

    private lateinit var switchablePane: BorderPane
    private lateinit var refreshButton: Button
    private lateinit var viewTotalButton: Button

    private val dateBox: DateBox = DateBox().apply {
        valueProperty().listener { refresh() }
    }
    private val monthBox: MonthBox = MonthBox().apply {
        setLocale(defaults.language.toLocale())
        valueProperty().listener { refresh() }
    }

    override fun NodeInvokable.onCreateActions() {
        refreshButton = action(getString(R2.string.refresh), R.image.action_refresh) {
            onAction { refresh() }
        }
        viewTotalButton = action(getString(R2.string.total), R.image.action_money) {
            onAction { viewTotal() }
        }
        switchablePane = borderPane()
    }

    override fun initialize(location: URL, resources: ResourceBundle) {
        super.initialize(location, resources)
        switchablePane.centerProperty().bind(
            When(tabPane.selectionModel.selectedIndexProperty() eq 0)
                .then<Node>(dateBox)
                .otherwise(monthBox)
        )

        dailyNoColumn.numberCell(this) {
            runBlocking(Dispatchers.IO) {
                api.getInvoice(invoiceId).no
            }
        }
        dailyTimeColumn.stringCell { dateTime.toString(PATTERN_TIME) }
        dailyEmployeeColumn.stringCell {
            runBlocking(Dispatchers.IO) {
                api.getEmployee(employeeId).toString()
            }
        }
        dailyValueColumn.currencyCell(this) { value }
        dailyCashColumn.doneCell { isCash() }
        dailyReferenceColumn.stringCell { reference }
        viewInvoiceItem.disableProperty().bind(
            dailyTable.selectionModel.selectedItemProperty().isNull
        )
        dailyTable.onMouseClicked {
            if (it.isDoubleClick() && dailyTable.selectionModel.isSelected()) {
                viewInvoice()
            }
        }

        monthlyDateColumn.stringCell { date.toString(PATTERN_DATE) }
        monthlyCashColumn.currencyCell(this) { cash }
        monthlyNonCashColumn.currencyCell(this) { nonCash }
        monthlyTotalColumn.currencyCell(this) { total }
        viewPaymentsItem.disableProperty()
            .bind(monthlyTable.selectionModel.selectedItemProperty().isNull)
        monthlyTable.onMouseClicked {
            if (it.isDoubleClick() && monthlyTable.selectionModel.isSelected()) {
                viewPayments()
            }
        }

        tabPane.selectionModel.selectedIndexProperty().listener { refresh() }
    }

    override fun refresh() = runLater {
        runBlocking {
            when (tabPane.selectionModel.selectedIndex) {
                0 -> dailyTable.items = withContext(Dispatchers.IO) {
                    api.getPayments(dateBox.value!!).toMutableObservableList()
                }
                else -> monthlyTable.items = withContext(Dispatchers.IO) {
                    Report.listAll(api.getPayments(monthBox.value!!))
                }
            }
        }
    }

    @FXML
    fun viewInvoice() = ViewInvoicePopOver(this, runBlocking(Dispatchers.IO) {
        api.getInvoice(dailyTable.selectionModel.selectedItem.invoiceId)
    }).show(
        when (tabPane.selectionModel.selectedIndex) {
            0 -> dailyTable
            else -> monthlyTable
        }
    )

    @FXML
    fun viewPayments() {
        tabPane.selectionModel.selectFirst()
        dateBox.picker.value = monthlyTable.selectionModel.selectedItem.date.toJava()
    }

    private fun viewTotal() =
        ViewTotalPopOver(this, getTotal(true), getTotal(false)).show(viewTotalButton)

    private fun getTotal(isCash: Boolean): Double = when (tabPane.selectionModel.selectedIndex) {
        0 -> Payment.gather(dailyTable.items, isCash)
        else -> monthlyTable.items.sumByDouble {
            when {
                isCash -> it.cash
                else -> it.nonCash
            }
        }
    }
}