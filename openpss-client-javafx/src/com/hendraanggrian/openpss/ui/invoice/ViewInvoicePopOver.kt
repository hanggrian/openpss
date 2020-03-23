package com.hendraanggrian.openpss.ui.invoice

import com.hendraanggrian.openpss.FxComponent
import com.hendraanggrian.openpss.Language
import com.hendraanggrian.openpss.PATTERN_DATETIMEEXT
import com.hendraanggrian.openpss.R
import com.hendraanggrian.openpss.R2
import com.hendraanggrian.openpss.api.OpenPSSApi
import com.hendraanggrian.openpss.control.Space
import com.hendraanggrian.openpss.schema.Customer
import com.hendraanggrian.openpss.schema.Employee
import com.hendraanggrian.openpss.schema.GlobalSetting
import com.hendraanggrian.openpss.schema.GlobalSetting.Companion.KEY_INVOICE_HEADERS
import com.hendraanggrian.openpss.schema.Invoice
import com.hendraanggrian.openpss.schema.typedTechnique
import com.hendraanggrian.openpss.ui.BasePopOver
import com.hendraanggrian.openpss.ui.Stylesheets
import com.sun.javafx.print.PrintHelper
import com.sun.javafx.print.Units
import java.util.ResourceBundle
import javafx.geometry.HPos
import javafx.geometry.Pos
import javafx.print.PageOrientation
import javafx.print.PageRange
import javafx.print.Printer
import javafx.scene.layout.Border
import javafx.scene.layout.BorderStroke
import javafx.scene.layout.BorderStrokeStyle
import javafx.scene.layout.BorderWidths
import javafx.scene.layout.CornerRadii
import javafx.scene.layout.Priority
import javafx.scene.layout.VBox
import javafx.scene.paint.Color
import javafx.scene.text.TextAlignment
import javafx.scene.transform.Scale
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import ktfx.controls.columnConstraints
import ktfx.controls.gap
import ktfx.controls.paddings
import ktfx.controlsfx.isPlatformOSX
import ktfx.coroutines.onAction
import ktfx.layouts.KtfxGridPane
import ktfx.layouts.NodeManager
import ktfx.layouts.button
import ktfx.layouts.gridPane
import ktfx.layouts.hbox
import ktfx.layouts.label
import ktfx.layouts.line
import ktfx.layouts.region
import ktfx.layouts.text
import ktfx.layouts.textFlow
import ktfx.layouts.vbox
import ktfx.print.createJob
import ktfx.runLater
import ktfx.text.invoke
import ktfx.text.pt

/**
 * Popup displaying invoice using server's language instead of local.
 * Size of invoice is equivalent to 10x14cm, possibly the smallest continuous form available.
 *
 * Must create custom paper in Windows machine called `Invoice`, which is 10x14cm without margins.
 */
class ViewInvoicePopOver(
    component: FxComponent,
    private val invoice: Invoice,
    private val isTest: Boolean = false
) : BasePopOver(component, R2.string.invoice) {

    private companion object {
        const val MM_WIDTH = 100.0
        const val MM_HEIGHT = 140.0
        const val PX_WIDTH = 378.0
        const val PX_HEIGHT = 530.0
    }

    private lateinit var invoiceHeaders: List<String>
    private lateinit var customer: Customer
    private lateinit var employee: Employee
    private val invoiceBox: VBox

    override val resourceBundle: ResourceBundle = Language
        .ofFullCode(runBlocking(Dispatchers.IO) {
            OpenPSSApi.getSetting(GlobalSetting.KEY_LANGUAGE).value
        })
        .toResourcesBundle()

    init {
        graphic = label("${getString(R2.string.server_language)}: $language")
        runBlocking {
            invoiceHeaders = OpenPSSApi.getSetting(KEY_INVOICE_HEADERS).valueList
            employee = OpenPSSApi.getEmployee(invoice.employeeId)
            customer = OpenPSSApi.getCustomer(invoice.customerId)
        }
        invoiceBox = vbox(getDouble(R.value.padding_medium)) {
            if (!isPlatformOSX()) {
                stylesheets += Stylesheets.INVOICE
            }
            setMinSize(PX_WIDTH, PX_HEIGHT)
            setMaxSize(PX_WIDTH, PX_HEIGHT)
            hbox(getDouble(R.value.padding_medium)) {
                vbox {
                    alignment = Pos.CENTER_LEFT
                    invoiceHeaders.forEachIndexed { index, s ->
                        label(s) {
                            if (index == 0) {
                                styleClass += R.style.bold
                            }
                        }
                    }
                } hgrow true
                vbox {
                    alignment = Pos.CENTER_RIGHT
                    label(getString(R2.string.invoice)) {
                        font = 18.pt
                    }
                    label("# ${invoice.no}") {
                        font = 32.pt
                    }
                }
            }
            fullLine()
            vbox {
                alignment = Pos.CENTER
                label("${invoice.dateTime.toString(PATTERN_DATETIMEEXT)} " + "(${employee.name})")
                label(customer.name) {
                    styleClass += R.style.bold
                }
            }
            vbox {
                gridPane {
                    hgap = getDouble(R.value.padding_medium)
                    columnConstraints {
                        constraints {
                            minWidth = USE_PREF_SIZE
                            halignment = HPos.RIGHT
                        }
                        constraints {
                            minWidth = USE_PREF_SIZE
                        }
                        constraints { hgrow = Priority.ALWAYS }
                        constraints {
                            minWidth = USE_PREF_SIZE
                            halignment = HPos.RIGHT
                        }
                    }
                    var row = 0
                    if (invoice.offsetJobs.isNotEmpty()) {
                        row += jobGridPane(row, R2.string.offset, invoice.offsetJobs) { job, i ->
                            label(numberConverter(job.qty)) row i col 0
                            label("${job.type}\n${job.typedTechnique.toString(this@ViewInvoicePopOver)}") {
                                textAlignment = TextAlignment.CENTER
                            } row i col 1
                            label(job.desc) { isWrapText = true } row i col 2
                            label(numberConverter(job.total)) row i col 3
                        }
                    }
                    if (invoice.digitalJobs.isNotEmpty()) {
                        row += jobGridPane(row, R2.string.digital, invoice.digitalJobs) { job, i ->
                            label(numberConverter(job.qty)) row i col 0
                            label("${job.type}\n${getString(if (job.isTwoSide) R2.string.two_side else R2.string.one_side)}") {
                                textAlignment = TextAlignment.CENTER
                            } row i col 1
                            label(job.desc) { isWrapText = true } row i col 2
                            label(numberConverter(job.total)) row i col 3
                        }
                    }
                    if (invoice.plateJobs.isNotEmpty()) {
                        row += jobGridPane(row, R2.string.plate, invoice.plateJobs) { job, i ->
                            label(numberConverter(job.qty)) row i col 0
                            label(job.type) row i col 1
                            label(job.desc) { isWrapText = true } row i col 2
                            label(numberConverter(job.total)) row i col 3
                        }
                    }
                    if (invoice.otherJobs.isNotEmpty()) {
                        row += jobGridPane(row, R2.string.others, invoice.otherJobs) { job, i ->
                            label(numberConverter(job.qty)) row i col 0
                            label(job.desc) { isWrapText = true } row i col 2
                            label(numberConverter(job.total)) row i col 3
                        }
                    }
                }
            } vgrow true
            fullLine()
            gridPane {
                gap = getDouble(R.value.padding_medium)
                textFlow {
                    paddings = getDouble(R.value.padding_small)
                    border = BorderStrokeStyle.SOLID.toBorder()
                    "${getString(R2.string.note)}\n" {
                        styleClass += R.style.bold
                    }
                    text(invoice.note)
                } row (0 to 2) col 0 hgrow true
                label(currencyConverter(invoice.total)) {
                    styleClass += R.style.bold
                } row 0 col (1 to 2) halign HPos.RIGHT
                vbox {
                    alignment = Pos.CENTER
                    region { prefHeight = 48.0 }
                    line(endX = 64.0)
                    label(getString(R2.string.employee))
                } row 1 col 1
                vbox {
                    alignment = Pos.CENTER
                    region { prefHeight = 48.0 }
                    line(endX = 64.0)
                    label(getString(R2.string.customer))
                } row 1 col 2
            }
        }
        buttonManager.run {
            button(getString(R2.string.print)) {
                isDefaultButton = true
                runLater { isDisable = invoice.isPrinted }
                onAction {
                    // resize node to actual print size
                    val printer = Printer.getDefaultPrinter()
                    val layout = printer.createPageLayout(
                        PrintHelper.createPaper(
                            "Invoice",
                            MM_WIDTH,
                            MM_HEIGHT, Units.MM
                        ),
                        PageOrientation.PORTRAIT,
                        0.0, 0.0, 0.0, 0.0
                    )
                    val scale = Scale(
                        (layout.printableWidth - layout.leftMargin * 3 / 2 - layout.rightMargin * 3 / 2) /
                            invoiceBox.boundsInParent.width,
                        (layout.printableHeight - layout.bottomMargin) /
                            invoiceBox.boundsInParent.height
                    )
                    invoiceBox.transforms += scale
                    // disable auto-hide when print dialog is showing
                    isAutoHide = false
                    val job = printer.createJob {
                        jobName = "${getString(R2.string.invoice)} #${invoice.no}"
                        setPageRanges(PageRange(1, 1))
                        pageLayout = layout
                    }
                    if (job.showPrintDialog(this@ViewInvoicePopOver) && job.printPage(invoiceBox)) {
                        job.endJob()
                        isDisable = true
                        if (!isTest) {
                            invoice.isPrinted = true
                        }
                    }
                    // restore state
                    isAutoHide = true
                    invoiceBox.transforms -= scale
                }
            }
        }
    }

    private fun <T : Invoice.Job> KtfxGridPane.jobGridPane(
        currentRow: Int,
        titleId: String,
        jobs: List<T>,
        lineBuilder: KtfxGridPane.(order: T, row: Int) -> Unit
    ): Int {
        var row = currentRow
        label(getString(titleId)) { styleClass += R.style.bold } row row col (0 to 4) halign HPos.LEFT
        row++
        jobs.forEach {
            lineBuilder(it, row)
            row++
        }
        addChild(Space(height = getDouble(R.value.padding_small))) row row col (0 to 4)
        row++
        return row
    }

    private fun BorderStrokeStyle.toBorder() =
        Border(BorderStroke(Color.BLACK, this, CornerRadii.EMPTY, BorderWidths.DEFAULT))

    private fun NodeManager.fullLine() = line(endX = PX_WIDTH)
}
