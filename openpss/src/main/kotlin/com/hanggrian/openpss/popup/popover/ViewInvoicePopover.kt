package com.hanggrian.openpss.popup.popover

import com.hanggrian.openpss.Context
import com.hanggrian.openpss.Language
import com.hanggrian.openpss.PATTERN_DATETIME_EXTENDED
import com.hanggrian.openpss.R
import com.hanggrian.openpss.control.Space
import com.hanggrian.openpss.db.schemas.Customer
import com.hanggrian.openpss.db.schemas.Customers
import com.hanggrian.openpss.db.schemas.Employee
import com.hanggrian.openpss.db.schemas.Employees
import com.hanggrian.openpss.db.schemas.GlobalSetting.Companion.KEY_INVOICE_HEADERS
import com.hanggrian.openpss.db.schemas.Invoice
import com.hanggrian.openpss.db.schemas.Invoices
import com.hanggrian.openpss.db.transaction
import com.sun.javafx.print.PrintHelper
import com.sun.javafx.print.Units.MM
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
import ktfx.controls.CENTER
import ktfx.controls.H_LEFT
import ktfx.controls.H_RIGHT
import ktfx.controls.LEFT
import ktfx.controls.RIGHT
import ktfx.controls.columnConstraints
import ktfx.controls.insetsOf
import ktfx.coroutines.onAction
import ktfx.layouts.KtfxGridPane
import ktfx.layouts.NodeContainer
import ktfx.layouts.button
import ktfx.layouts.gridPane
import ktfx.layouts.hbox
import ktfx.layouts.label
import ktfx.layouts.line
import ktfx.layouts.region
import ktfx.layouts.styledLabel
import ktfx.layouts.styledText
import ktfx.layouts.textFlow
import ktfx.layouts.vbox
import ktfx.runLater
import ktfx.text.append
import ktfx.text.invoke
import ktfx.text.pt
import java.util.ResourceBundle

/**
 * Popup displaying invoice using server's language instead of local.
 * Size of invoice is equivalent to 10x14cm, possibly the smallest continuous form available.
 */
class ViewInvoicePopover(
    context: Context,
    private val invoice: Invoice,
    private val isTest: Boolean = false,
) : Popover(context, R.string_invoice) {
    private lateinit var invoiceHeaders: List<String>
    private lateinit var customer: Customer
    private lateinit var employee: Employee
    private val invoiceBox: VBox

    override val resourceBundle: ResourceBundle = Language.ofServer().toResourcesBundle()

    init {
        graphic = label("${getString(R.string_server_language)}: $language")
        transaction {
            invoiceHeaders = findGlobalSettings(KEY_INVOICE_HEADERS).single().valueList
            employee = Employees[invoice.employeeId].single()
            customer = Customers[invoice.customerId].single()
        }
        invoiceBox =
            vbox(getDouble(R.dimen_padding_medium)) {
                border = DASHED.toBorder()
                padding = insetsOf(getDouble(R.dimen_padding_medium))
                setMinSize(WIDTH, HEIGHT)
                setMaxSize(WIDTH, HEIGHT)
                hbox(getDouble(R.dimen_padding_medium)) {
                    vbox {
                        alignment = LEFT
                        invoiceHeaders.forEachIndexed { index, s ->
                            label(s) {
                                if (index == 0) {
                                    styleClass += R.style_bold
                                }
                            }
                        }
                    }.hgrow()
                    vbox {
                        alignment = RIGHT
                        label(getString(R.string_invoice)) { font = 18.pt }
                        label("# ${invoice.no}") { font = 32.pt }
                    }
                }
                fullLine()
                vbox {
                    alignment = CENTER
                    label(
                        "${invoice.dateTime.toString(PATTERN_DATETIME_EXTENDED)} " +
                            "(${transaction { Employees[invoice.employeeId].single().name }})",
                    )
                    styledLabel("${customer.no}. ${customer.name}", null, R.style_bold)
                }
                vbox {
                    gridPane {
                        hgap = getDouble(R.dimen_padding_medium)
                        columnConstraints {
                            append {
                                minWidth = USE_PREF_SIZE
                                halignment = H_RIGHT
                            }
                            append {
                                minWidth = USE_PREF_SIZE
                            }
                            append { hgrow = ALWAYS }
                            append {
                                minWidth = USE_PREF_SIZE
                                halignment = H_RIGHT
                            }
                        }
                        var row = 0
                        if (invoice.offsetJobs.isNotEmpty()) {
                            row +=
                                jobGridPane(row, R.string_offset, invoice.offsetJobs) { job, i ->
                                    label(numberConverter(job.qty))
                                        .grid(row = i, col = 0)
                                    label(
                                        "${job.type}\n" +
                                            job.typedTechnique.toString(this@ViewInvoicePopover),
                                    ) { textAlignment = TextAlignment.CENTER }
                                        .grid(row = i, col = 1)
                                    label(job.desc) { isWrapText = true }
                                        .grid(row = i, col = 2)
                                    label(numberConverter(job.total))
                                        .grid(row = i, col = 3)
                                }
                        }
                        if (invoice.digitalJobs.isNotEmpty()) {
                            row +=
                                jobGridPane(row, R.string_digital, invoice.digitalJobs) { job, i ->
                                    label(numberConverter(job.qty)).grid(row = i, col = 0)
                                    label(
                                        "${job.type}\n" +
                                            getString(
                                                when {
                                                    job.isTwoSide -> R.string_two_side
                                                    else -> R.string_one_side
                                                },
                                            ),
                                    ) { textAlignment = TextAlignment.CENTER }
                                        .grid(row = i, col = 1)
                                    label(job.desc) { isWrapText = true }
                                        .grid(row = i, col = 2)
                                    label(numberConverter(job.total))
                                        .grid(row = i, col = 3)
                                }
                        }
                        if (invoice.plateJobs.isNotEmpty()) {
                            row +=
                                jobGridPane(row, R.string_plate, invoice.plateJobs) { job, i ->
                                    label(numberConverter(job.qty)).grid(row = i, col = 0)
                                    label(job.type).grid(row = i, col = 1)
                                    label(job.desc) {
                                        isWrapText = true
                                    }.grid(row = i, col = 2)
                                    label(numberConverter(job.total)).grid(row = i, col = 3)
                                }
                        }
                        if (invoice.otherJobs.isEmpty()) {
                            return@gridPane
                        }
                        row +=
                            jobGridPane(row, R.string_others, invoice.otherJobs) { job, i ->
                                label(numberConverter(job.qty)).grid(row = i, col = 0)
                                label(job.desc) {
                                    isWrapText = true
                                }.grid(row = i, col = 2)
                                label(numberConverter(job.total)).grid(row = i, col = 3)
                            }
                    }
                }.vgrow()
                fullLine()
                gridPane {
                    hgap = getDouble(R.dimen_padding_medium)
                    vgap = getDouble(R.dimen_padding_medium)
                    textFlow {
                        padding = insetsOf(getDouble(R.dimen_padding_small))
                        border = SOLID.toBorder()
                        styledText("${getString(R.string_note)}\n", R.style_bold)
                        append(invoice.note)
                    }.grid(row = 0 to 2, col = 0)
                        .hgrow()
                    styledLabel(currencyConverter(invoice.total), null, R.style_bold)
                        .grid(row = 0, col = 1 to 2)
                        .halign(H_RIGHT)
                    vbox {
                        alignment = CENTER
                        region { prefHeight = 48.0 }
                        line(endX = 64.0)
                        label(getString(R.string_employee))
                    }.grid(row = 1, col = 1)
                    vbox {
                        alignment = CENTER
                        region { prefHeight = 48.0 }
                        line(endX = 64.0)
                        label(getString(R.string_customer))
                    }.grid(row = 1, col = 2)
                }
            }
        buttonManager.run {
            button(getString(R.string_print)) {
                isDefaultButton = true
                runLater { isDisable = invoice.isPrinted }
                onAction {
                    // resize node to actual print size
                    val printer = Printer.getDefaultPrinter()
                    val layout = printer.createPageLayout(PAPER, PORTRAIT, 0.0, 0.0, 0.0, 0.0)
                    invoiceBox.run {
                        border = null
                        transforms +=
                            Scale(
                                layout.printableWidth / boundsInParent.width,
                                layout.printableHeight / boundsInParent.height,
                            )
                    }
                    // disable auto-hide when print dialog is showing
                    // isAutoHide = false
                    val job = PrinterJob.createPrinterJob(printer)!!
                    if (job.showPrintDialog(this@ViewInvoicePopover.scene.window) &&
                        job.printPage(layout, invoiceBox)
                    ) {
                        job.endJob()
                        if (!isTest) {
                            transaction { Invoices[invoice].projection { printed }.update(true) }
                        }
                    }
                    hide()
                }
            }
        }
    }

    private fun <T : Invoice.Job> KtfxGridPane.jobGridPane(
        currentRow: Int,
        titleId: String,
        jobs: List<T>,
        lineBuilder: KtfxGridPane.(order: T, row: Int) -> Unit,
    ): Int {
        var row = currentRow
        styledLabel(getString(titleId), null, R.style_bold)
            .grid(row = row, col = 0 to 4)
            .halign(H_LEFT)
        row++
        jobs.forEach {
            lineBuilder(it, row)
            row++
        }
        addChild(Space(height = getDouble(R.dimen_padding_small)))
            .grid(row = row, col = 0 to 4)
        row++
        return row
    }

    private fun BorderStrokeStyle.toBorder() = Border(BorderStroke(BLACK, this, EMPTY, DEFAULT))

    private fun NodeContainer.fullLine() =
        line(endX = WIDTH - getDouble(R.dimen_padding_medium) * 2)

    private companion object {
        const val WIDTH = 378.0
        const val HEIGHT = 530.0

        val PAPER: Paper = PrintHelper.createPaper("Invoice", 100.0, 140.0, MM)
    }
}
