package com.hendraanggrian.openpss.ui.finance

import com.hendraanggrian.openpss.App.Companion.STYLE_DEFAULT_BUTTON
import com.hendraanggrian.openpss.PATTERN_DATE
import com.hendraanggrian.openpss.PATTERN_TIME
import com.hendraanggrian.openpss.R
import com.hendraanggrian.openpss.control.currencyCell
import com.hendraanggrian.openpss.control.doneCell
import com.hendraanggrian.openpss.control.numberCell
import com.hendraanggrian.openpss.control.popover.ViewInvoicePopover
import com.hendraanggrian.openpss.control.space
import com.hendraanggrian.openpss.control.stretchableButton
import com.hendraanggrian.openpss.control.stringCell
import com.hendraanggrian.openpss.control.styledStretchableButton
import com.hendraanggrian.openpss.db.matches
import com.hendraanggrian.openpss.db.schemas.Employees
import com.hendraanggrian.openpss.db.schemas.Invoices
import com.hendraanggrian.openpss.db.schemas.Payment
import com.hendraanggrian.openpss.db.schemas.Payments
import com.hendraanggrian.openpss.db.transaction
import com.hendraanggrian.openpss.io.properties.PreferencesFile
import com.hendraanggrian.openpss.layout.DateBox
import com.hendraanggrian.openpss.layout.MonthBox
import com.hendraanggrian.openpss.layout.SegmentedTabPane
import com.hendraanggrian.openpss.layout.SegmentedTabPane.Companion.STRETCH_POINT
import com.hendraanggrian.openpss.layout.dateBox
import com.hendraanggrian.openpss.layout.monthBox
import com.hendraanggrian.openpss.ui.Refreshable
import com.hendraanggrian.openpss.ui.SegmentedController
import com.hendraanggrian.openpss.ui.Selectable
import com.hendraanggrian.openpss.ui.Selectable2
import com.hendraanggrian.openpss.ui.Selectable3
import com.hendraanggrian.openpss.util.toJava
import javafx.fxml.FXML
import javafx.scene.Node
import javafx.scene.control.Button
import javafx.scene.control.MenuItem
import javafx.scene.control.SelectionModel
import javafx.scene.control.Tab
import javafx.scene.control.TableColumn
import javafx.scene.control.TableView
import javafx.scene.image.ImageView
import javafx.scene.layout.Pane
import javafxx.application.later
import javafxx.collections.toMutableObservableList
import javafxx.coroutines.listener
import javafxx.coroutines.onAction
import javafxx.coroutines.onMouseClicked
import javafxx.layouts.LayoutManager
import javafxx.layouts.pane
import javafxx.scene.input.isDoubleClick
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
    @FXML lateinit var dailyCashColumn: TableColumn<Payment, Boolean>
    @FXML lateinit var dailyReferenceColumn: TableColumn<Payment, String>
    @FXML lateinit var viewInvoiceItem: MenuItem

    @FXML lateinit var monthlyTable: TableView<Report>
    @FXML lateinit var monthlyDateColumn: TableColumn<Report, String>
    @FXML lateinit var monthlyCashColumn: TableColumn<Report, String>
    @FXML lateinit var monthlyNonCashColumn: TableColumn<Report, String>
    @FXML lateinit var monthlyTotalColumn: TableColumn<Report, String>
    @FXML lateinit var viewPaymentsItem: MenuItem

    private lateinit var refreshButton: Button
    private lateinit var viewTotalButton: Button
    private lateinit var dateBox: DateBox
    private lateinit var monthBox: MonthBox

    override val selectionModel: SelectionModel<Tab> get() = tabPane.selectionModel
    override val selectionModel2: SelectionModel<Payment> get() = dailyTable.selectionModel
    override val selectionModel3: SelectionModel<Report> get() = monthlyTable.selectionModel

    override fun LayoutManager<Node>.onCreateRightActions() {
        pane()
    }

    override fun initialize(location: URL, resources: ResourceBundle) {
        super.initialize(location, resources)
        refreshButton =
            stretchableButton(STRETCH_POINT, getString(R.string.refresh), ImageView(R.image.btn_refresh_light)) {
                onAction { refresh() }
            }
        viewTotalButton = styledStretchableButton(
            STYLE_DEFAULT_BUTTON,
            STRETCH_POINT,
            getString(R.string.total),
            ImageView(R.image.btn_money_dark)
        ) {
            onAction { viewTotal() }
        }
        leftActionManager.childs.addAll(
            tabPane.header, space(),
            refreshButton, viewTotalButton
        )
        dateBox = dateBox {
            valueProperty().listener { refresh() }
        }
        monthBox = monthBox {
            setLocale(PreferencesFile.language.toLocale())
            valueProperty().listener { refresh() }
        }
        tabPane.header.toggleGroup.run {
            selectedToggleProperty().addListener { _, _, toggle ->
                val pane = rightActionManager.childs.first() as Pane
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
        dailyCashColumn.doneCell { isCash() }
        dailyReferenceColumn.stringCell { reference }
        viewInvoiceItem.disableProperty().bind(!selectedBinding2)
        dailyTable.onMouseClicked { if (it.isDoubleClick() && selected2 != null) viewInvoice() }

        monthlyDateColumn.stringCell { date.toString(PATTERN_DATE) }
        monthlyCashColumn.currencyCell { cash }
        monthlyNonCashColumn.currencyCell { nonCash }
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

    @FXML fun viewInvoice() = ViewInvoicePopover(transaction { Invoices[selected2!!.invoiceId].single() })
        .showAt(dailyTable)

    @FXML fun viewPayments() {
        selectFirst()
        dateBox.picker.value = selected3!!.date.toJava()
    }

    private fun viewTotal() = ViewTotalPopover(this, getTotal(true), getTotal(false)).showAt(viewTotalButton)

    private fun getTotal(isCash: Boolean): Double = when (selectedIndex) {
        0 -> Payment.gather(dailyTable.items, isCash)
        else -> monthlyTable.items.sumByDouble {
            when {
                isCash -> it.cash
                else -> it.nonCash
            }
        }
    }

    private fun List<Node>.addAll(vararg buttons: Node) {
        this as MutableList
        buttons.forEach { this += it }
    }
}