package com.hendraanggrian.openpss.ui.finance

import com.hendraanggrian.openpss.App.Companion.STYLE_DEFAULT_BUTTON
import com.hendraanggrian.openpss.R
import com.hendraanggrian.openpss.control.ViewInvoicePopOver
import com.hendraanggrian.openpss.control.stretchableButton
import com.hendraanggrian.openpss.control.styledStretchableButton
import com.hendraanggrian.openpss.db.schemas.Employees
import com.hendraanggrian.openpss.db.schemas.Invoices
import com.hendraanggrian.openpss.db.schemas.Payment
import com.hendraanggrian.openpss.db.schemas.Payment.Companion.gather
import com.hendraanggrian.openpss.db.schemas.Payment.Method
import com.hendraanggrian.openpss.db.schemas.Payment.Method.CASH
import com.hendraanggrian.openpss.db.schemas.Payment.Method.CHEQUE
import com.hendraanggrian.openpss.db.schemas.Payment.Method.CREDIT_CARD
import com.hendraanggrian.openpss.db.schemas.Payment.Method.DEBIT_CARD
import com.hendraanggrian.openpss.db.schemas.Payment.Method.TRANSFER
import com.hendraanggrian.openpss.db.schemas.Payments
import com.hendraanggrian.openpss.db.transaction
import com.hendraanggrian.openpss.io.properties.PreferencesFile
import com.hendraanggrian.openpss.layout.DateBox
import com.hendraanggrian.openpss.layout.MonthBox
import com.hendraanggrian.openpss.layout.SegmentedTabPane
import com.hendraanggrian.openpss.layout.dateBox
import com.hendraanggrian.openpss.layout.monthBox
import com.hendraanggrian.openpss.ui.Refreshable
import com.hendraanggrian.openpss.ui.SegmentedController
import com.hendraanggrian.openpss.ui.Selectable
import com.hendraanggrian.openpss.ui.Selectable2
import com.hendraanggrian.openpss.ui.Selectable3
import com.hendraanggrian.openpss.util.PATTERN_DATE
import com.hendraanggrian.openpss.util.PATTERN_TIME
import com.hendraanggrian.openpss.util.currencyCell
import com.hendraanggrian.openpss.util.matches
import com.hendraanggrian.openpss.util.numberCell
import com.hendraanggrian.openpss.util.stringCell
import com.hendraanggrian.openpss.util.toJava
import javafx.fxml.FXML
import javafx.geometry.Orientation.VERTICAL
import javafx.scene.Node
import javafx.scene.control.Button
import javafx.scene.control.MenuItem
import javafx.scene.control.SelectionModel
import javafx.scene.control.Tab
import javafx.scene.control.TableColumn
import javafx.scene.control.TableView
import javafx.scene.image.ImageView
import javafx.scene.layout.Pane
import ktfx.application.later
import ktfx.collections.toMutableObservableList
import ktfx.coroutines.listener
import ktfx.coroutines.onAction
import ktfx.coroutines.onMouseClicked
import ktfx.layouts.pane
import ktfx.layouts.separator
import ktfx.scene.input.isDoubleClick
import java.net.URL
import java.util.ResourceBundle

class FinanceController : SegmentedController(), Refreshable,
    Selectable<Tab>, Selectable2<Payment>, Selectable3<Report> {

    companion object {
        const val EXTRA_MAIN_CONTROLLER = "EXTRA_MAIN_CONTROLLER"
    }

    @FXML lateinit var tabPane: SegmentedTabPane

    @FXML lateinit var dailyTable: TableView<Payment>
    @FXML lateinit var dailyNoColumn: TableColumn<Payment, String>
    @FXML lateinit var dailyTimeColumn: TableColumn<Payment, String>
    @FXML lateinit var dailyEmployeeColumn: TableColumn<Payment, String>
    @FXML lateinit var dailyValueColumn: TableColumn<Payment, String>
    @FXML lateinit var dailyMethodColumn: TableColumn<Payment, String>
    @FXML lateinit var dailyReferenceColumn: TableColumn<Payment, String>
    @FXML lateinit var viewInvoiceItem: MenuItem

    @FXML lateinit var monthlyTable: TableView<Report>
    @FXML lateinit var monthlyDateColumn: TableColumn<Report, String>
    @FXML lateinit var monthlyCashColumn: TableColumn<Report, String>
    @FXML lateinit var monthlyCreditColumn: TableColumn<Report, String>
    @FXML lateinit var monthlyDebitColumn: TableColumn<Report, String>
    @FXML lateinit var monthlyChequeColumn: TableColumn<Report, String>
    @FXML lateinit var monthlyTransferColumn: TableColumn<Report, String>
    @FXML lateinit var monthlyTotalColumn: TableColumn<Report, String>
    @FXML lateinit var viewPaymentsItem: MenuItem

    private lateinit var refreshButton: Button
    private lateinit var viewTotalButton: Button
    override val leftButtons: List<Node> = mutableListOf()

    private lateinit var dateBox: DateBox
    private lateinit var monthBox: MonthBox
    override val rightButtons: List<Node> = listOf(pane())

    override val selectionModel: SelectionModel<Tab> get() = tabPane.selectionModel
    override val selectionModel2: SelectionModel<Payment> get() = dailyTable.selectionModel
    override val selectionModel3: SelectionModel<Report> get() = monthlyTable.selectionModel

    override fun initialize(location: URL, resources: ResourceBundle) {
        super.initialize(location, resources)
        refreshButton = stretchableButton(getString(R.string.refresh), ImageView(R.image.btn_refresh_light)) {
            onAction { refresh() }
        }
        viewTotalButton = styledStretchableButton(STYLE_DEFAULT_BUTTON, getString(R.string.total),
            ImageView(R.image.btn_money_dark)) {
            onAction { viewTotal() }
        }
        leftButtons.addAll(tabPane.header, separator(VERTICAL), refreshButton, viewTotalButton)
        dateBox = dateBox {
            valueProperty().listener { refresh() }
        }
        monthBox = monthBox {
            setLocale(PreferencesFile.language.toLocale())
            valueProperty().listener { refresh() }
        }
        tabPane.header.toggleGroup.run {
            selectedToggleProperty().addListener { _, _, toggle ->
                val pane = rightButtons.first() as Pane
                pane.children.clear()
                pane.children += when (toggles.indexOf(toggle)) {
                    0 -> dateBox
                    else -> monthBox
                }
            }
        }

        dailyNoColumn.numberCell { transaction { Invoices[invoiceId].single().no } }
        dailyTimeColumn.stringCell { dateTime.toString(PATTERN_TIME) }
        dailyEmployeeColumn.stringCell { transaction { Employees[employeeId].single().toString() } }
        dailyValueColumn.currencyCell { value }
        dailyMethodColumn.stringCell { typedMethod.toString(this@FinanceController) }
        dailyReferenceColumn.stringCell { reference }
        viewInvoiceItem.disableProperty().bind(!selectedBinding2)
        dailyTable.onMouseClicked { if (it.isDoubleClick() && selected2 != null) viewInvoice() }

        monthlyDateColumn.stringCell { date.toString(PATTERN_DATE) }
        monthlyCashColumn.currencyCell { valueMap[CASH]!! }
        monthlyCreditColumn.currencyCell { valueMap[CREDIT_CARD]!! }
        monthlyDebitColumn.currencyCell { valueMap[DEBIT_CARD]!! }
        monthlyChequeColumn.currencyCell { valueMap[CHEQUE]!! }
        monthlyTransferColumn.currencyCell { valueMap[TRANSFER]!! }
        monthlyTotalColumn.currencyCell { total }
        viewPaymentsItem.disableProperty().bind(!selectedBinding3)
        monthlyTable.onMouseClicked { if (it.isDoubleClick() && selected3 != null) viewPayments() }

        selectedProperty.listener { refresh() }
    }

    override fun refresh() = later {
        transaction {
            when (selectedIndex) {
                0 -> dailyTable.items = Payments { it.dateTime.matches(dateBox.value) }.toMutableObservableList()
                else -> monthlyTable.items = Report.listAll(Payments { it.dateTime.matches(monthBox.value) })
            }
        }
    }

    @FXML fun viewInvoice() = ViewInvoicePopOver(transaction { Invoices[selected2!!.invoiceId].single() })
        .showAt(dailyTable)

    @FXML fun viewPayments() {
        selectFirst()
        dateBox.picker.value = selected3!!.date.toJava()
    }

    private fun viewTotal() = ViewTotalPopOver(this, getTotal(CASH), getTotal(CREDIT_CARD), getTotal(DEBIT_CARD),
        getTotal(CHEQUE), getTotal(TRANSFER)).showAt(viewTotalButton)

    private fun getTotal(method: Method) = when (selectedIndex) {
        0 -> gather(dailyTable.items, method)
        else -> monthlyTable.items.sumByDouble { it.valueMap[method]!! }
    }

    private fun List<Node>.addAll(vararg buttons: Node) {
        this as MutableList
        buttons.forEach { this += it }
    }
}