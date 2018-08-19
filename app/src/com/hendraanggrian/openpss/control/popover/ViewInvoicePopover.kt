package com.hendraanggrian.openpss.control.popover

import com.hendraanggrian.openpss.R
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
import com.hendraanggrian.openpss.util.PATTERN_DATETIME_EXTENDED
import com.hendraanggrian.openpss.util.bold
import com.hendraanggrian.openpss.util.currencyConverter
import com.hendraanggrian.openpss.util.numberConverter
import javafx.geometry.Dimension2D
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
import javafx.scene.text.TextAlignment
import javafxx.layouts.LayoutManager
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
import kotlin.Double.Companion.MAX_VALUE

class ViewInvoicePopover(invoice: Invoice) : Popover(object : Resourced {
    override val resources: ResourceBundle = Language.ofFullCode(transaction {
        findGlobalSettings(KEY_LANGUAGE).single().value
    }).toResourcesBundle()
}, R.string.invoice) {

    private companion object {
        /** Size equivalent to 10x14cm, possibly the smallest continuous form available. */
        val SIZE = Dimension2D(378.0, 530.0)
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
            setMinSize(SIZE.width, SIZE.height)
            setMaxSize(SIZE.width, SIZE.height)
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
            hbox(R.dimen.padding_small.toDouble()) {
                label(customer.name) { maxWidth = MAX_VALUE } hpriority ALWAYS
                label(invoice.dateTime.toString(PATTERN_DATETIME_EXTENDED) + '\n' +
                    transaction { Employees[invoice.employeeId].single().name }
                ) { textAlignment = TextAlignment.RIGHT }
            }
            gridPane {
                hgap = R.dimen.padding_small.toDouble()
                columnConstraints {
                    constraints { hgrow = ALWAYS }
                    constraints()
                    constraints()
                    constraints()
                }
                var row = 0
                invoice.plates.run {
                    if (isNotEmpty()) {
                        label(getString(R.string.plate)) { font = bold() } row row col 0
                        row++
                        forEach {
                            label(it.title) row row col 0
                            label(it.machine) row row col 1
                            label("${numberConverter.toString(it.qty)} x " +
                                currencyConverter.toString(it.price)) row row col 2
                            label(currencyConverter.toString(it.total)) row row col 3
                            row++
                        }
                    }
                }
                invoice.offsets.run {
                    if (isNotEmpty()) {
                        label(getString(R.string.offset)) { font = bold() } row row col 0
                        row++
                        forEach {
                            label(it.title) row row col 0
                            label(it.machine) row row col 1
                            label("${it.typedTechnique.toString(this@ViewInvoicePopover)} " +
                                numberConverter.toString(it.qty)) row row col 2
                            label(currencyConverter.toString(it.total)) row row col 3
                            row++
                        }
                    }
                }
                invoice.others.run {
                    if (isNotEmpty()) {
                        label(getString(R.string.others)) { font = bold() } row row col 0
                        row++
                        forEach {
                            label(it.title) row row col 0
                            label("${numberConverter.toString(it.qty)} x " +
                                currencyConverter.toString(it.price)) row row col 2
                            label(currencyConverter.toString(it.total)) row row col 3
                            row++
                        }
                    }
                }
            } vpriority ALWAYS
            fullLine()
            gridPane {
                gap = R.dimen.padding_small.toDouble()
                textFlow {
                    paddingAll = 8.0
                    border = SOLID.toBorder()
                    "${getString(R.string.note)}\n" { font = bold() }
                    invoice.note()
                } row 0 col 0 rowSpans 2 hpriority ALWAYS
                label(currencyConverter.toString(invoice.total)) { font = bold() } row 0 col 1 colSpans 2 halign RIGHT
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

    private fun BorderStrokeStyle.toBorder() = Border(BorderStroke(BLACK, this, EMPTY, DEFAULT))

    private fun LayoutManager<Node>.fullLine() = line(endX = SIZE.width - R.dimen.padding_small.toDouble() * 2)
}