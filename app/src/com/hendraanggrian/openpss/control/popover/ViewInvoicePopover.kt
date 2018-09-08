package com.hendraanggrian.openpss.control.popover

import com.hendraanggrian.openpss.R
import com.hendraanggrian.openpss.db.Order
import com.hendraanggrian.openpss.db.schemas.Customer
import com.hendraanggrian.openpss.db.schemas.Customers
import com.hendraanggrian.openpss.db.schemas.Employee
import com.hendraanggrian.openpss.db.schemas.Employees
import com.hendraanggrian.openpss.db.schemas.GlobalSetting.Companion.KEY_INVOICE_HEADERS
import com.hendraanggrian.openpss.db.schemas.GlobalSetting.Companion.KEY_LANGUAGE
import com.hendraanggrian.openpss.db.schemas.Invoice
import com.hendraanggrian.openpss.db.transaction
import com.hendraanggrian.openpss.i18n.Language
import com.hendraanggrian.openpss.i18n.Resourced
import com.hendraanggrian.openpss.PATTERN_DATETIME_EXTENDED
import com.hendraanggrian.openpss.control.bold
import com.hendraanggrian.openpss.currencyConverter
import com.hendraanggrian.openpss.numberConverter
import javafx.geometry.HPos.LEFT
import javafx.geometry.HPos.RIGHT
import javafx.geometry.Pos.CENTER
import javafx.geometry.Pos.CENTER_LEFT
import javafx.geometry.Pos.CENTER_RIGHT
import javafx.scene.Node
import javafx.scene.layout.Border
import javafx.scene.layout.BorderStroke
import javafx.scene.layout.BorderStrokeStyle
import javafx.scene.layout.BorderStrokeStyle.DASHED
import javafx.scene.layout.BorderStrokeStyle.SOLID
import javafx.scene.layout.BorderWidths.DEFAULT
import javafx.scene.layout.CornerRadii.EMPTY
import javafx.scene.layout.Priority.ALWAYS
import javafx.scene.paint.Color.BLACK
import javafxx.layouts.LayoutManager
import javafxx.layouts._GridPane
import javafxx.layouts._VBox
import javafxx.layouts.button
import javafxx.layouts.columnConstraints
import javafxx.layouts.gridPane
import javafxx.layouts.hbox
import javafxx.layouts.label
import javafxx.layouts.line
import javafxx.layouts.region
import javafxx.layouts.textFlow
import javafxx.layouts.vbox
import javafxx.scene.layout.gap
import javafxx.scene.layout.paddingAll
import java.util.ResourceBundle

/**
 * Popup displaying invoice using server's language instead of local.
 * Size of invoice is equivalent to 10x14cm, possibly the smallest continuous form available.
 */
class ViewInvoicePopover(invoice: Invoice) : Popover(object : Resourced {
    override val resources: ResourceBundle = Language.ofFullCode(transaction {
        findGlobalSettings(KEY_LANGUAGE).single().value
    }).toResourcesBundle()
}, R.string.invoice) {

    private companion object {
        const val WIDTH = 378.0
        const val HEIGHT = 530.0
    }

    private lateinit var invoiceHeaders: List<String>
    private lateinit var customer: Customer
    private lateinit var employee: Employee

    init {
        graphic = javafxx.layouts.label("${getString(R.string.server_language)}: $language")
        transaction {
            invoiceHeaders = findGlobalSettings(KEY_INVOICE_HEADERS).single().valueList
            employee = Employees[invoice.employeeId].single()
            customer = Customers[invoice.customerId].single()
        }
        vbox(R.dimen.padding_small.toDouble()) {
            border = DASHED.toBorder()
            paddingAll = R.dimen.padding_small.toDouble()
            setMinSize(WIDTH, HEIGHT)
            setMaxSize(WIDTH, HEIGHT)
            hbox(R.dimen.padding_small.toDouble()) {
                vbox {
                    alignment = CENTER_LEFT
                    invoiceHeaders.forEachIndexed { index, s -> label(s) { if (index == 0) font = bold() } }
                } hpriority ALWAYS
                vbox {
                    alignment = CENTER_RIGHT
                    label(getString(R.string.invoice)) { font = bold(18) }
                    label("# ${invoice.no}") { font = bold(32) }
                }
            }
            fullLine()
            vbox {
                alignment = CENTER
                label("${invoice.dateTime.toString(PATTERN_DATETIME_EXTENDED)} " +
                    "(${transaction { Employees[invoice.employeeId].single().name }})")
                label("${customer.no}. ${customer.name}") { font = bold() }
            }
            vbox {
                contentGridPane(R.string.plate, invoice.plates) { plate, row ->
                    label(numberConverter.toString(plate.qty)) row row col 0
                    label(plate.title) { isWrapText = true } row row col 1
                    label(plate.machine) row row col 2
                    label(currencyConverter.toString(plate.total)) row row col 3
                }
                contentGridPane(R.string.offset, invoice.offsets) { offset, row ->
                    label(numberConverter.toString(offset.qty)) row row col 0
                    label(offset.title) { isWrapText = true } row row col 1
                    label(offset.machine) row row col 2
                    label(currencyConverter.toString(offset.total)) row row col 3
                }
                contentGridPane(R.string.others, invoice.others) { other, row ->
                    label(numberConverter.toString(other.qty)) row row col 0
                    label(other.title) { isWrapText = true } row row col 1 colSpans 2
                    label(currencyConverter.toString(other.total)) row row col 3
                }
            } vpriority ALWAYS
            fullLine()
            gridPane {
                gap = R.dimen.padding_small.toDouble()
                textFlow {
                    paddingAll = R.dimen.padding_small.toDouble()
                    border = SOLID.toBorder()
                    "${getString(R.string.note)}\n" { font = bold() }
                    invoice.note()
                } row 0 col 0 rowSpans 2 hpriority ALWAYS
                label(currencyConverter.toString(invoice.total)) {
                    font = bold()
                } row 0 col 1 colSpans 2 halign RIGHT
                vbox {
                    alignment = CENTER
                    region { prefHeight = 50.0 }
                    line(endX = 75.0)
                    label(getString(R.string.employee))
                } row 1 col 1
                vbox {
                    alignment = CENTER
                    region { prefHeight = 50.0 }
                    line(endX = 75.0)
                    label(getString(R.string.customer))
                } row 1 col 2
            }
        }
        buttonBar.run {
            button(getString(R.string.print)) {
                isDefaultButton = true
                isDisable = invoice.printed
            }
        }
    }

    private fun <T : Order> _VBox.contentGridPane(
        titleId: String,
        orders: List<T>,
        lineBuilder: _GridPane.(order: T, row: Int) -> Unit
    ) {
        if (orders.isNotEmpty()) gridPane {
            hgap = R.dimen.padding_small.toDouble()
            columnConstraints {
                constraints {
                    minWidth = USE_PREF_SIZE
                    halignment = RIGHT
                }
                constraints { hgrow = ALWAYS }
                constraints {
                    minWidth = USE_PREF_SIZE
                }
                constraints {
                    minWidth = USE_PREF_SIZE
                    halignment = RIGHT
                }
            }
            var row = 0
            label(getString(titleId)) { font = bold() } row row col 0 colSpans 4 halign LEFT
            row++
            orders.forEach {
                lineBuilder(it, row)
                row++
            }
        }
    }

    private fun BorderStrokeStyle.toBorder() = Border(BorderStroke(BLACK, this, EMPTY, DEFAULT))

    private fun LayoutManager<Node>.fullLine() = line(endX = WIDTH - R.dimen.padding_small.toDouble() * 2)
}