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
import javafx.geometry.HPos.RIGHT
import javafx.geometry.Pos.CENTER
import javafx.geometry.Pos.CENTER_LEFT
import javafx.geometry.Pos.CENTER_RIGHT
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
import javafxx.layouts.button
import javafxx.layouts.columnConstraints
import javafxx.layouts.gridPane
import javafxx.layouts.label
import javafxx.layouts.line
import javafxx.layouts.region
import javafxx.layouts.textFlow
import javafxx.layouts.vbox
import javafxx.scene.layout.gap
import javafxx.scene.layout.paddingAll
import java.util.ResourceBundle

class ViewInvoicePopover(invoice: Invoice) : Popover(object : Resourced {
    override val resources: ResourceBundle = Language.ofFullCode(transaction {
        findGlobalSettings(KEY_LANGUAGE).single().value
    }).toResourcesBundle()
}, R.string.invoice) {

    private companion object {
        const val WIDTH = 912.0 // 9.5-inch
        const val HEIGHT = 528.0 // 5.5-inch
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
        gridPane {
            border = getStroke(DASHED)
            gap = R.dimen.padding_large.toDouble()
            paddingAll = 20.0
            minWidth = WIDTH
            minHeight = HEIGHT
            columnConstraints {
                constraints {
                    hgrow = ALWAYS
                    isFillWidth = true
                }
                constraints(200.0)
                constraints(200.0) {
                    halignment = RIGHT
                }
            }
            vbox {
                alignment = CENTER_LEFT
                invoiceHeaders.forEachIndexed { index, s ->
                    label(s) { if (index == 0) font = bold() }
                }
            } row 0 col 0
            vbox {
                alignment = CENTER_RIGHT
                label(getString(R.string.invoice)) { font = bold(32) }
                label("# ${invoice.no}") { font = bold(18) }
            } row 0 col 1 colSpans 2
            line(endX = WIDTH - 32.0) row 1 col 0 colSpans 3
            label(customer.name) {
                font = bold(24)
            } row 2 col 0 colSpans 2
            label(invoice.dateTime.toString(PATTERN_DATETIME_EXTENDED) + '\n' +
                transaction { Employees[invoice.employeeId].single().name }
            ) { textAlignment = TextAlignment.RIGHT } row 2 col 2
            gridPane {
                hgap = R.dimen.padding_large.toDouble()
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
            } row 3 col 0 colSpans 3 vpriority ALWAYS
            line(endX = WIDTH - 32.0) row 4 col 0 colSpans 3
            textFlow {
                paddingAll = 8.0
                border = getStroke(SOLID)
                "${getString(R.string.note)}\n" { font = bold() }
                invoice.note()
            } row 5 col 0 rowSpans 2
            label(currencyConverter.toString(invoice.total)) {
                font = bold(18)
            } row 5 col 1 colSpans 2 halign RIGHT
            vbox {
                alignment = CENTER
                region { prefHeight = 50.0 }
                line(endX = 150.0)
                label(getString(R.string.employee))
            } row 6 col 1
            vbox {
                alignment = CENTER
                region { prefHeight = 50.0 }
                line(endX = 150.0)
                label(getString(R.string.customer))
            } row 6 col 2
        }
        buttonBar.run {
            button(getString(R.string.print)) {
                isDefaultButton = true
                isDisable = invoice.printed
            }
        }
    }

    private fun getStroke(style: BorderStrokeStyle) = Border(BorderStroke(BLACK, style, EMPTY, DEFAULT))
}