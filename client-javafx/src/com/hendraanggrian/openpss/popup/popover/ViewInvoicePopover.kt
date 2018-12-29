package com.hendraanggrian.openpss.popup.popover

import com.hendraanggrian.openpss.R
import com.hendraanggrian.openpss.content.Formats
import com.hendraanggrian.openpss.content.FxComponent
import com.hendraanggrian.openpss.content.Stylesheets
import com.hendraanggrian.openpss.control.Space
import com.hendraanggrian.openpss.data.Customer
import com.hendraanggrian.openpss.data.Employee
import com.hendraanggrian.openpss.data.GlobalSetting
import com.hendraanggrian.openpss.data.GlobalSetting.Companion.KEY_INVOICE_HEADERS
import com.hendraanggrian.openpss.data.Invoice
import com.hendraanggrian.openpss.content.Language
import com.hendraanggrian.openpss.schema.typedTechnique
import com.sun.javafx.print.PrintHelper
import com.sun.javafx.print.Units
import javafx.geometry.HPos.LEFT
import javafx.geometry.HPos.RIGHT
import javafx.geometry.Pos.CENTER
import javafx.geometry.Pos.CENTER_LEFT
import javafx.geometry.Pos.CENTER_RIGHT
import javafx.print.PageOrientation
import javafx.print.PageRange
import javafx.scene.layout.Border
import javafx.scene.layout.BorderStroke
import javafx.scene.layout.BorderStrokeStyle
import javafx.scene.layout.BorderStrokeStyle.SOLID
import javafx.scene.layout.BorderWidths.DEFAULT
import javafx.scene.layout.CornerRadii.EMPTY
import javafx.scene.layout.Priority.ALWAYS
import javafx.scene.layout.VBox
import javafx.scene.paint.Color.BLACK
import javafx.scene.text.TextAlignment
import javafx.scene.transform.Scale
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.javafx.JavaFx
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import ktfx.controls.gap
import ktfx.controls.paddingAll
import ktfx.coroutines.onAction
import ktfx.invoke
import ktfx.later
import ktfx.layouts.NodeInvokable
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
import ktfx.print.createJob
import ktfx.print.defaultPrinter
import ktfx.text.updateFont
import org.apache.commons.lang3.SystemUtils
import java.util.ResourceBundle

/**
 * Popup displaying invoice using server's language instead of local.
 * Size of invoice is equivalent to 10x14cm, possibly the smallest continuous form available.
 *
 * Must create custom paper in Windows machine called `Invoice`, which is 10x14cm without margins.
 */
class ViewInvoicePopover(
    component: FxComponent,
    private val invoice: Invoice,
    private val isTest: Boolean = false
) : Popover(component, R.string.invoice) {

    private companion object {

        const val WIDTH_MM = 100.0
        const val HEIGHT_MM = 140.0

        const val WIDTH_PX = 378.0
        const val HEIGHT_PX = 530.0
    }

    private lateinit var invoiceHeaders: List<String>
    private lateinit var customer: Customer
    private lateinit var employee: Employee
    private val invoiceBox: VBox

    override val resourceBundle: ResourceBundle = Language
        .ofFullCode(runBlocking { api.getGlobalSetting(GlobalSetting.KEY_LANGUAGE).value })
        .toResourcesBundle()

    init {
        graphic = label("${getString(R.string.server_language)}: $language")
        GlobalScope.launch(Dispatchers.JavaFx) {
            invoiceHeaders = api.getGlobalSetting(KEY_INVOICE_HEADERS).valueList
            employee = api.getEmployee(invoice.employeeId)
            customer = api.getCustomer(invoice.customerId)
        }
        invoiceBox = vbox(getDouble(R.value.padding_medium)) {
            if (!SystemUtils.IS_OS_MAC) stylesheets += Stylesheets.INVOICE
            setMinSize(WIDTH_PX, HEIGHT_PX)
            setMaxSize(WIDTH_PX, HEIGHT_PX)
            hbox(getDouble(R.value.padding_medium)) {
                vbox {
                    alignment = CENTER_LEFT
                    invoiceHeaders.forEachIndexed { index, s ->
                        label(s) {
                            if (index == 0) {
                                styleClass += R.style.bold
                            }
                        }
                    }
                } hpriority ALWAYS
                vbox {
                    alignment = CENTER_RIGHT
                    label(getString(R.string.invoice)) {
                        updateFont(18)
                    }
                    label("# ${invoice.no}") {
                        updateFont(32)
                    }
                }
            }
            fullLine()
            vbox {
                alignment = CENTER
                label("${invoice.dateTime.toString(Formats.DATETIME_EXTENDED)} " + "(${employee.name})")
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
                    if (invoice.offsetJobs.isNotEmpty()) {
                        row += jobGridPane(row, R.string.offset, invoice.offsetJobs) { job, i ->
                            label(numberConverter(job.qty)) row i col 0
                            label("${job.type}\n${job.typedTechnique.toString(this@ViewInvoicePopover)}") {
                                textAlignment = TextAlignment.CENTER
                            } row i col 1
                            label(job.desc) {
                                isWrapText = true
                            } row i col 2
                            label(numberConverter(job.total)) row i col 3
                        }
                    }
                    if (invoice.digitalJobs.isNotEmpty()) {
                        row += jobGridPane(row, R.string.digital, invoice.digitalJobs) { job, i ->
                            label(numberConverter(job.qty)) row i col 0
                            label("${job.type}\n${getString(if (job.isTwoSide) R.string.two_side else R.string.one_side)}") {
                                textAlignment = TextAlignment.CENTER
                            } row i col 1
                            label(job.desc) {
                                isWrapText = true
                            } row i col 2
                            label(numberConverter(job.total)) row i col 3
                        }
                    }
                    if (invoice.plateJobs.isNotEmpty()) {
                        row += jobGridPane(row, R.string.plate, invoice.plateJobs) { job, i ->
                            label(numberConverter(job.qty)) row i col 0
                            label(job.type) row i col 1
                            label(job.desc) {
                                isWrapText = true
                            } row i col 2
                            label(numberConverter(job.total)) row i col 3
                        }
                    }
                    if (invoice.otherJobs.isNotEmpty()) {
                        row += jobGridPane(row, R.string.others, invoice.otherJobs) { job, i ->
                            label(numberConverter(job.qty)) row i col 0
                            label(job.desc) {
                                isWrapText = true
                            } row i col 2
                            label(numberConverter(job.total)) row i col 3
                        }
                    }
                }
            } vpriority ALWAYS
            fullLine()
            gridPane {
                gap = getDouble(R.value.padding_medium)
                textFlow {
                    paddingAll = getDouble(R.value.padding_small)
                    border = SOLID.toBorder()
                    "${getString(R.string.note)}\n" {
                        styleClass += R.style.bold
                    }
                    invoice.note()
                } row 0 col 0 rowSpans 2 hpriority ALWAYS
                label(currencyConverter(invoice.total)) {
                    styleClass += R.style.bold
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
                later { isDisable = invoice.isPrinted }
                onAction {
                    // resize node to actual print size
                    val printer = defaultPrinter
                    val layout = printer.createPageLayout(
                        PrintHelper.createPaper("Invoice", WIDTH_MM, HEIGHT_MM, Units.MM),
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
                        jobName = "${getString(R.string.invoice)} #${invoice.no}"
                        setPageRanges(PageRange(1, 1))
                        pageLayout = layout
                    }
                    if (job.showPrintDialog(this@ViewInvoicePopover) && job.printPage(invoiceBox)) {
                        job.endJob()
                        isDisable = true
                        if (!isTest) GlobalScope.launch(Dispatchers.JavaFx) {
                            api.editInvoice(invoice, isPrinted = true)
                        }
                    }
                    // restore state
                    isAutoHide = true
                    invoiceBox.transforms -= scale
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
            styleClass += R.style.bold
        } row row col 0 colSpans 4 halign LEFT
        row++
        jobs.forEach {
            lineBuilder(it, row)
            row++
        }
        Space(height = getDouble(R.value.padding_small))() row row col 0 colSpans 4
        row++
        return row
    }

    private fun BorderStrokeStyle.toBorder() = Border(BorderStroke(BLACK, this, EMPTY, DEFAULT))

    private fun NodeInvokable.fullLine() = line(endX = WIDTH_PX)
}