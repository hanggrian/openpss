package com.hendraanggrian.openpss.ui.finance

import com.hendraanggrian.openpss.R
import com.hendraanggrian.openpss.content.PATTERN_DATE
import com.hendraanggrian.openpss.content.PATTERN_TIME
import com.hendraanggrian.openpss.control.DateBox
import com.hendraanggrian.openpss.control.MonthBox
import com.hendraanggrian.openpss.db.schemas.Employees
import com.hendraanggrian.openpss.db.schemas.Invoices
import com.hendraanggrian.openpss.db.schemas.Payment
import com.hendraanggrian.openpss.db.schemas.Payments
import com.hendraanggrian.openpss.db.transaction
import com.hendraanggrian.openpss.io.properties.PreferencesFile
import com.hendraanggrian.openpss.popup.popover.ViewInvoicePopover
import com.hendraanggrian.openpss.ui.ActionController
import com.hendraanggrian.openpss.ui.Refreshable
import com.hendraanggrian.openpss.util.currencyCell
import com.hendraanggrian.openpss.util.doneCell
import com.hendraanggrian.openpss.util.matches
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
import javafx.scene.image.ImageView
import javafx.scene.layout.BorderPane
import ktfx.bindings.eq
import ktfx.collections.toMutableObservableList
import ktfx.controls.isSelected
import ktfx.coroutines.listener
import ktfx.coroutines.onAction
import ktfx.coroutines.onMouseClicked
import ktfx.inputs.isDoubleClick
import ktfx.jfoenix.layouts.styledJFXButton
import ktfx.layouts.NodeManager
import ktfx.layouts.borderPane
import ktfx.layouts.tooltip
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
        setLocale(PreferencesFile.language.toLocale())
        valueProperty().listener { refresh() }
    }

    override fun NodeManager.onCreateActions() {
        refreshButton = styledJFXButton(null, ImageView(R.image.act_refresh), R.style.flat) {
            tooltip(getString(R.string.refresh))
            onAction { refresh() }
        }
        viewTotalButton = styledJFXButton(null, ImageView(R.image.act_money), R.style.flat) {
            tooltip(getString(R.string.total))
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

        dailyNoColumn.numberCell(this) { transaction { Invoices[invoiceId].single().no } }
        dailyTimeColumn.stringCell { dateTime.toString(PATTERN_TIME) }
        dailyEmployeeColumn.stringCell { transaction { Employees[employeeId].single().toString() } }
        dailyValueColumn.currencyCell(this) { value }
        dailyCashColumn.doneCell { isCash() }
        dailyReferenceColumn.stringCell { reference }
        viewInvoiceItem.disableProperty().bind(dailyTable.selectionModel.selectedItemProperty().isNull)
        dailyTable.onMouseClicked {
            if (it.isDoubleClick() && dailyTable.selectionModel.isSelected()) {
                viewInvoice()
            }
        }

        monthlyDateColumn.stringCell { date.toString(PATTERN_DATE) }
        monthlyCashColumn.currencyCell(this) { cash }
        monthlyNonCashColumn.currencyCell(this) { nonCash }
        monthlyTotalColumn.currencyCell(this) { total }
        viewPaymentsItem.disableProperty().bind(monthlyTable.selectionModel.selectedItemProperty().isNull)
        monthlyTable.onMouseClicked {
            if (it.isDoubleClick() && monthlyTable.selectionModel.isSelected()) {
                viewPayments()
            }
        }

        tabPane.selectionModel.selectedIndexProperty().listener { refresh() }
    }

    override fun refresh() = runLater {
        transaction {
            when (tabPane.selectionModel.selectedIndex) {
                0 -> dailyTable.items = Payments { it.dateTime.matches(dateBox.value!!) }.toMutableObservableList()
                else -> monthlyTable.items = Report.listAll(Payments { it.dateTime.matches(monthBox.value!!) })
            }
        }
    }

    @FXML fun viewInvoice() = ViewInvoicePopover(
        this,
        transaction {
            Invoices[dailyTable.selectionModel.selectedItem.invoiceId].single()
        }
    ).show(
        when (tabPane.selectionModel.selectedIndex) {
            0 -> dailyTable
            else -> monthlyTable
        }
    )

    @FXML fun viewPayments() {
        tabPane.selectionModel.selectFirst()
        dateBox.picker.value = monthlyTable.selectionModel.selectedItem.date.toJava()
    }

    private fun viewTotal() = ViewTotalPopover(this, getTotal(true), getTotal(false)).show(viewTotalButton)

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
