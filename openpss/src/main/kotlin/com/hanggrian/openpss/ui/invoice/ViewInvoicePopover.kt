package com.hanggrian.openpss.ui.invoice

import com.hanggrian.openpss.BuildConfig
import com.hanggrian.openpss.Context
import com.hanggrian.openpss.Language
import com.hanggrian.openpss.PATTERN_DATETIME_EXTENDED
import com.hanggrian.openpss.R
import com.hanggrian.openpss.STYLESHEET_OPENPSS
import com.hanggrian.openpss.control.Space
import com.hanggrian.openpss.db.schemas.Customer
import com.hanggrian.openpss.db.schemas.Customers
import com.hanggrian.openpss.db.schemas.Employee
import com.hanggrian.openpss.db.schemas.Employees
import com.hanggrian.openpss.db.schemas.GlobalSetting.Companion.KEY_INVOICE_HEADERS
import com.hanggrian.openpss.db.schemas.Invoice
import com.hanggrian.openpss.db.schemas.Invoices
import com.hanggrian.openpss.db.transaction
import com.hanggrian.openpss.popup.popover.Popover
import com.sun.javafx.print.PrintHelper
import com.sun.javafx.print.Units
import javafx.print.PageOrientation
import javafx.print.PageRange
import javafx.print.PrintColor
import javafx.print.PrintSides
import javafx.print.Printer
import javafx.print.PrinterJob
import javafx.scene.layout.Border
import javafx.scene.layout.BorderStroke
import javafx.scene.layout.BorderStrokeStyle
import javafx.scene.layout.BorderStrokeStyle.SOLID
import javafx.scene.layout.BorderWidths.DEFAULT
import javafx.scene.layout.CornerRadii.EMPTY
import javafx.scene.layout.Priority.ALWAYS
import javafx.scene.layout.VBox
import javafx.scene.paint.Color.BLACK
import javafx.scene.shape.Line
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
import ktfx.dialogs.errorAlert
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
import org.slf4j.LoggerFactory
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
    private val lines = mutableListOf<Line>()

    private val serverLanguage = Language.ofServer()

    override val resourceBundle: ResourceBundle = serverLanguage.toResourcesBundle()

    init {
        graphic = label("${getString(R.string_server_language)}: $serverLanguage")
        transaction {
            invoiceHeaders = findGlobalSettings(KEY_INVOICE_HEADERS).single().valueList
            employee = Employees[invoice.employeeId].singleOrNull() ?: Employee.NONE
            customer = Customers[invoice.customerId].single()
        }
        invoiceBox =
            vbox(getDouble(R.dimen_padding_medium)) {
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
                lines += fullLine()
                vbox {
                    alignment = CENTER
                    label(
                        "${invoice.dateTime.toString(PATTERN_DATETIME_EXTENDED)} " +
                            "(${employee.name})",
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
                lines += fullLine()
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
                    // find default printer
                    val printer = Printer.getDefaultPrinter()
                    if (printer == null) {
                        errorAlert {
                            dialogPane.stylesheets += STYLESHEET_OPENPSS
                            headerText = getString(R.string__no_printer)
                        }
                        return@onAction
                    }

                    // resize node to actual print size
                    val layout =
                        printer.createPageLayout(
                            PRINT_PAPER,
                            PageOrientation.PORTRAIT,
                            Printer.MarginType.HARDWARE_MINIMUM,
                        )
                    invoiceBox.run {
                        padding =
                            insetsOf(right = PRINT_PADDING_RIGHT, bottom = PRINT_PADDING_BOTTOM)
                        lines.forEach { it.endX -= PRINT_PADDING_RIGHT }
                        transforms +=
                            Scale(
                                layout.printableWidth / boundsInParent.width,
                                layout.printableHeight / boundsInParent.height,
                            )
                    }
                    if (BuildConfig.DEBUG) {
                        LOGGER.info(
                            "Printable size: " +
                                "${layout.printableWidth} \u00D7 " +
                                "${layout.printableHeight}",
                        )
                    }

                    // disable auto-hide when print dialog is showing
                    isAutoHide = false
                    val job = PrinterJob.createPrinterJob(printer)!!
                    job.jobSettings.run {
                        setPageRanges(PRINT_RANGE)
                        title = "#${getString(R.string_invoice)} + ${invoice.no}"
                        copies = PRINT_COPIES
                        pageLayout = layout
                        printResolution = printer.printerAttributes.defaultPrintResolution
                        printSides = PrintSides.ONE_SIDED
                        printColor = PrintColor.MONOCHROME
                    }
                    if (BuildConfig.DEBUG) {
                        LOGGER.info("Job: $job")
                    }
                    if (job.showPrintDialog(this@ViewInvoicePopover.scene.window) &&
                        job.printPage(invoiceBox)
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

    private fun NodeContainer.fullLine() = line(endX = WIDTH)

    private companion object {
        val LOGGER = LoggerFactory.getLogger(ViewInvoicePopover::class.java)!!

        const val WIDTH = 378.0
        const val HEIGHT = 530.0

        // Since moving away from JavaFX 8, we cannot explicitly set printer margin to 0. The end
        // parts of the print content will be slightly cut off when printing in JavaFX 11. The dirty
        // hotfix is to apply right padding to invoice box before printing.
        const val PRINT_PADDING_RIGHT = 60
        const val PRINT_PADDING_BOTTOM = 10

        const val PRINT_COPIES = 1
        val PRINT_RANGE = PageRange(1, 1)
        val PRINT_PAPER = PrintHelper.createPaper("Invoice", 4.25, 5.5, Units.INCH)!!
    }
}
