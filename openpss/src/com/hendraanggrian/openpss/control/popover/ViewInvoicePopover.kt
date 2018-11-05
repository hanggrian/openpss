package com.hendraanggrian.openpss.control.popover

import com.hendraanggrian.openpss.R
import com.hendraanggrian.openpss.content.Context
import com.hendraanggrian.openpss.content.Language
import com.hendraanggrian.openpss.content.PATTERN_DATETIME_EXTENDED
import com.hendraanggrian.openpss.content.currencyConverter
import com.hendraanggrian.openpss.content.numberConverter
import com.hendraanggrian.openpss.control.space
import com.hendraanggrian.openpss.db.schemas.Customer
import com.hendraanggrian.openpss.db.schemas.Customers
import com.hendraanggrian.openpss.db.schemas.Employee
import com.hendraanggrian.openpss.db.schemas.Employees
import com.hendraanggrian.openpss.db.schemas.GlobalSetting.Companion.KEY_INVOICE_HEADERS
import com.hendraanggrian.openpss.db.schemas.Invoice
import com.hendraanggrian.openpss.db.schemas.Invoices
import com.hendraanggrian.openpss.db.transaction
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
import ktfx.NodeInvokable
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
    context: Context,
    private val invoice: Invoice,
    private val isTest: Boolean = false
) : Popover(context, R.string.invoice) {

    private companion object {
        const val WIDTH = 378.0
        const val HEIGHT = 530.0

        val PAPER: Paper = PrintHelper.createPaper("Invoice", 100.0, 140.0, MM)
    }

    private lateinit var invoiceHeaders: List<String>
    private lateinit var customer: Customer
    private lateinit var employee: Employee
    private val invoiceBox: VBox

    override val resources: ResourceBundle = Language.ofServer().toResourcesBundle()

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
            setMinSize(WIDTH, HEIGHT)
            setMaxSize(WIDTH, HEIGHT)
            hbox(R.dimen.padding_medium.toDouble()) {
                vbox {
                    alignment = CENTER_LEFT
                    invoiceHeaders.forEachIndexed { index, s ->
                        label(s) {
                            if (index == 0) {
                                styleClass += "bold"
                            }
                        }
                    }
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
                label("${customer.no}. ${customer.name}") {
                    styleClass += "bold"
                }
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
                    row += jobGridPane(row, R.string.offset, invoice.offsetJobs) { job, i ->
                        label(numberConverter(job.qty)) row i col 0
                        label("${job.type}\n${job.typedTechnique.toString(this@ViewInvoicePopover)}") {
                            textAlignment = TextAlignment.CENTER
                        } row i col 1
                        label(job.title) {
                            isWrapText = true
                        } row i col 2
                        label(numberConverter(job.total)) row i col 3
                    }
                    row += jobGridPane(row, R.string.digital, invoice.digitalJobs) { job, i ->
                        label(numberConverter(job.qty)) row i col 0
                        label("${job.type}\n${getString(if (job.isTwoSide) R.string.two_side else R.string.one_side)}") {
                            textAlignment = TextAlignment.CENTER
                        } row i col 1
                        label(job.title) {
                            isWrapText = true
                        } row i col 2
                        label(numberConverter(job.total)) row i col 3
                    }
                    row += jobGridPane(row, R.string.plate, invoice.plateJobs) { job, i ->
                        label(numberConverter(job.qty)) row i col 0
                        label(job.type) row i col 1
                        label(job.title) {
                            isWrapText = true
                        } row i col 2
                        label(numberConverter(job.total)) row i col 3
                    }
                    row += jobGridPane(row, R.string.others, invoice.otherJobs) { job, i ->
                        label(numberConverter(job.qty)) row i col 0
                        label(job.title) {
                            isWrapText = true
                        } row i col 2
                        label(numberConverter(job.total)) row i col 3
                    }
                }
            } vpriority ALWAYS
            fullLine()
            gridPane {
                gap = R.dimen.padding_medium.toDouble()
                textFlow {
                    paddingAll = R.dimen.padding_small.toDouble()
                    border = SOLID.toBorder()
                    "${getString(R.string.note)}\n" {
                        styleClass += "bold"
                    }
                    invoice.note()
                } row 0 col 0 rowSpans 2 hpriority ALWAYS
                label(currencyConverter(invoice.total)) {
                    styleClass += "bold"
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
        buttonInvokable.run {
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

    private fun <T : Invoice.Job> _GridPane.jobGridPane(
        currentRow: Int,
        titleId: String,
        jobs: List<T>,
        lineBuilder: _GridPane.(order: T, row: Int) -> Unit
    ): Int {
        var row = currentRow
        label(getString(titleId)) {
            styleClass += "bold"
        } row row col 0 colSpans 4 halign LEFT
        row++
        jobs.forEach {
            lineBuilder(it, row)
            row++
        }
        space(height = R.dimen.padding_small.toDouble()) row row col 0 colSpans 4
        row++
        return row
    }

    private fun BorderStrokeStyle.toBorder() = Border(BorderStroke(BLACK, this, EMPTY, DEFAULT))

    private fun NodeInvokable.fullLine() = line(endX = WIDTH - R.dimen.padding_medium.toDouble() * 2)
}