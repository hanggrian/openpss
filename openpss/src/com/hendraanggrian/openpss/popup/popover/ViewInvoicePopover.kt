package com.hendraanggrian.openpss.popup.popover

import com.hendraanggrian.openpss.PATTERN_DATETIME_EXTENDED
import com.hendraanggrian.openpss.R
import com.hendraanggrian.openpss.control.bold
import com.hendraanggrian.openpss.control.space
import com.hendraanggrian.openpss.currencyConverter
import com.hendraanggrian.openpss.db.schemas.Customer
import com.hendraanggrian.openpss.db.schemas.Customers
import com.hendraanggrian.openpss.db.schemas.Employee
import com.hendraanggrian.openpss.db.schemas.Employees
import com.hendraanggrian.openpss.db.schemas.GlobalSetting.Companion.KEY_INVOICE_HEADERS
import com.hendraanggrian.openpss.db.schemas.GlobalSetting.Companion.KEY_LANGUAGE
import com.hendraanggrian.openpss.db.schemas.Invoice
import com.hendraanggrian.openpss.db.schemas.Invoices
import com.hendraanggrian.openpss.db.transaction
import com.hendraanggrian.openpss.i18n.Language
import com.hendraanggrian.openpss.i18n.Resourced
import com.hendraanggrian.openpss.numberConverter
import com.sun.javafx.print.PrintHelper
import com.sun.javafx.print.Units.MM
import javafx.geometry.HPos.LEFT
import javafx.geometry.HPos.RIGHT
import javafx.geometry.Pos.CENTER
import javafx.geometry.Pos.CENTER_LEFT
import javafx.geometry.Pos.CENTER_RIGHT
import javafx.print.PageOrientation.PORTRAIT
import javafx.print.Paper
import javafx.print.Printer
import javafx.print.PrinterJob
import javafx.scene.layout.Border
import javafx.scene.layout.BorderStroke
import javafx.scene.layout.BorderStrokeStyle
import javafx.scene.layout.BorderStrokeStyle.DASHED
import javafx.scene.layout.BorderStrokeStyle.SOLID
import javafx.scene.layout.BorderWidths.DEFAULT
import javafx.scene.layout.CornerRadii.EMPTY
import javafx.scene.layout.Priority.ALWAYS
import javafx.scene.layout.VBox
import javafx.scene.paint.Color.BLACK
import javafx.scene.text.TextAlignment
import javafx.scene.transform.Scale
import kotlinx.nosql.update
import ktfx.NodeManager
import ktfx.application.later
import ktfx.coroutines.onAction
import ktfx.layouts._GridPane
import ktfx.layouts.button
import ktfx.layouts.columnConstraints
import ktfx.layouts.gridPane
import ktfx.layouts.hbox
import ktfx.layouts.label
import ktfx.layouts.line
import ktfx.layouts.region
import ktfx.layouts.textFlow
import ktfx.layouts.vbox
import ktfx.scene.layout.gap
import ktfx.scene.layout.paddingAll
import ktfx.scene.text.fontSize
import ktfx.util.invoke
import java.util.ResourceBundle

/**
 * Popup displaying invoice using server's language instead of local.
 * Size of invoice is equivalent to 10x14cm, possibly the smallest continuous form available.
 */
class ViewInvoicePopover(
    private val invoice: Invoice,
    private val isTest: Boolean = false
) : Popover(object : Resourced {
    override val resources: ResourceBundle = Language.ofFullCode(transaction {
        findGlobalSettings(KEY_LANGUAGE).single().value
    }).toResourcesBundle()
}, R.string.invoice) {

    private companion object {
        const val WIDTH = 378.0
        const val HEIGHT = 530.0

        val PAPER: Paper = PrintHelper.createPaper("Invoice", 100.0, 140.0, MM)
    }

    private lateinit var invoiceHeaders: List<String>
    private lateinit var customer: Customer
    private lateinit var employee: Employee
    private val invoiceBox: VBox

    init {
        graphic = label("${getString(R.string.server_language)}: $language")
        transaction {
            invoiceHeaders = findGlobalSettings(KEY_INVOICE_HEADERS).single().valueList
            employee = Employees[invoice.employeeId].single()
            customer = Customers[invoice.customerId].single()
        }
        invoiceBox = vbox(R.dimen.padding_medium.toDouble()) {
            border = DASHED.toBorder()
            paddingAll = R.dimen.padding_medium.toDouble()
            setMinSize(
                WIDTH,
                HEIGHT
            )
            setMaxSize(
                WIDTH,
                HEIGHT
            )
            hbox(R.dimen.padding_medium.toDouble()) {
                vbox {
                    alignment = CENTER_LEFT
                    invoiceHeaders.forEachIndexed { index, s -> label(s) { if (index == 0) font = bold() } }
                } hpriority ALWAYS
                vbox {
                    alignment = CENTER_RIGHT
                    label(getString(R.string.invoice)) { fontSize = 18.0 }
                    label("# ${invoice.no}") { fontSize = 32.0 }
                }
            }
            fullLine()
            vbox {
                alignment = CENTER
                label(
                    "${invoice.dateTime.toString(PATTERN_DATETIME_EXTENDED)} " +
                        "(${transaction { Employees[invoice.employeeId].single().name }})"
                )
                label("${customer.no}. ${customer.name}") { font = bold() }
            }
            vbox {
                gridPane {
                    hgap = R.dimen.padding_medium.toDouble()
                    columnConstraints {
                        constraints {
                            minWidth = USE_PREF_SIZE
                            halignment = RIGHT
                        }
                        constraints {
                            minWidth = USE_PREF_SIZE
                        }
                        constraints { hgrow = ALWAYS }
                        constraints {
                            minWidth = USE_PREF_SIZE
                            halignment = RIGHT
                        }
                    }
                    var row = 0
                    row += orderGridPane(row, R.string.plate, invoice.plates) { order, i ->
                        label(numberConverter(order.qty)) row i col 0
                        label(order.machine) row i col 1
                        label(order.title) {
                            isWrapText = true
                        } row i col 2
                        label(numberConverter(order.total)) row i col 3
                    }
                    row += orderGridPane(row, R.string.offset, invoice.offsets) { order, i ->
                        label(numberConverter(order.qty)) row i col 0
                        label("${order.machine}\n${order.typedTechnique.toString(this@ViewInvoicePopover)}") {
                            textAlignment = TextAlignment.CENTER
                        } row i col 1
                        label(order.title) {
                            isWrapText = true
                        } row i col 2
                        label(numberConverter(order.total)) row i col 3
                    }
                    row += orderGridPane(row, R.string.others, invoice.others) { order, i ->
                        label(numberConverter(order.qty)) row i col 0
                        label(order.title) {
                            isWrapText = true
                        } row i col 2
                        label(numberConverter(order.total)) row i col 3
                    }
                }
            } vpriority ALWAYS
            fullLine()
            gridPane {
                gap = R.dimen.padding_medium.toDouble()
                textFlow {
                    paddingAll = R.dimen.padding_small.toDouble()
                    border = SOLID.toBorder()
                    "${getString(R.string.note)}\n" { font = bold() }
                    invoice.note()
                } row 0 col 0 rowSpans 2 hpriority ALWAYS
                label(currencyConverter(invoice.total)) {
                    font = bold()
                } row 0 col 1 colSpans 2 halign RIGHT
                vbox {
                    alignment = CENTER
                    region { prefHeight = 48.0 }
                    line(endX = 64.0)
                    label(getString(R.string.employee))
                } row 1 col 1
                vbox {
                    alignment = CENTER
                    region { prefHeight = 48.0 }
                    line(endX = 64.0)
                    label(getString(R.string.customer))
                } row 1 col 2
            }
        }
        buttonManager.run {
            button(getString(R.string.print)) {
                isDefaultButton = true
                later { isDisable = invoice.printed }
                onAction {
                    // resize node to actual print size
                    val printer = Printer.getDefaultPrinter()
                    val layout = printer.createPageLayout(PAPER, PORTRAIT, 0.0, 0.0, 0.0, 0.0)
                    invoiceBox.run {
                        border = null
                        transforms += Scale(
                            layout.printableWidth / boundsInParent.width,
                            layout.printableHeight / boundsInParent.height
                        )
                    }
                    // disable auto-hide when print dialog is showing
                    isAutoHide = false
                    val job = PrinterJob.createPrinterJob(printer)!!
                    if (job.showPrintDialog(this@ViewInvoicePopover) && job.printPage(layout, invoiceBox)) {
                        job.endJob()
                        if (!isTest) transaction {
                            Invoices[invoice].projection { printed }.update(true)
                        }
                    }
                    hide()
                }
            }
        }
    }

    private fun <T : Invoice.Order> _GridPane.orderGridPane(
        currentRow: Int,
        titleId: String,
        orders: List<T>,
        lineBuilder: _GridPane.(order: T, row: Int) -> Unit
    ): Int {
        var row = currentRow
        label(getString(titleId)) { font = bold() } row row col 0 colSpans 4 halign LEFT
        row++
        orders.forEach {
            lineBuilder(it, row)
            row++
        }
        space(height = R.dimen.padding_small.toDouble()) row row col 0 colSpans 4
        row++
        return row
    }

    private fun BorderStrokeStyle.toBorder() = Border(BorderStroke(BLACK, this, EMPTY, DEFAULT))

    private fun NodeManager.fullLine() = line(endX = WIDTH - R.dimen.padding_medium.toDouble() * 2)
}