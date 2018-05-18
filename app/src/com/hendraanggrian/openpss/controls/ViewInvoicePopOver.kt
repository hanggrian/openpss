package com.hendraanggrian.openpss.controls

import com.hendraanggrian.openpss.R
import com.hendraanggrian.openpss.db.schemas.Customer
import com.hendraanggrian.openpss.db.schemas.Customers
import com.hendraanggrian.openpss.db.schemas.Employee
import com.hendraanggrian.openpss.db.schemas.Employees
import com.hendraanggrian.openpss.db.schemas.GlobalSetting.Companion.KEY_INVOICE_HEADERS
import com.hendraanggrian.openpss.db.schemas.GlobalSetting.Companion.KEY_LANGUAGE
import com.hendraanggrian.openpss.db.schemas.Invoice
import com.hendraanggrian.openpss.db.transaction
import com.hendraanggrian.openpss.localization.Language
import com.hendraanggrian.openpss.localization.Resourced
import com.hendraanggrian.openpss.util.PATTERN_DATETIME_EXTENDED
import com.hendraanggrian.openpss.util.currencyConverter
import com.hendraanggrian.openpss.util.getFont
import com.hendraanggrian.openpss.util.numberConverter
import javafx.geometry.HPos.RIGHT
import javafx.geometry.Insets
import javafx.geometry.Pos.CENTER
import javafx.scene.Node
import javafx.scene.control.Label
import javafx.scene.layout.Border
import javafx.scene.layout.BorderStroke
import javafx.scene.layout.BorderStrokeStyle.DASHED
import javafx.scene.layout.BorderStrokeStyle.SOLID
import javafx.scene.layout.BorderWidths.DEFAULT
import javafx.scene.layout.CornerRadii.EMPTY
import javafx.scene.layout.Priority.ALWAYS
import javafx.scene.paint.Color.BLACK
import javafx.scene.text.TextAlignment
import ktfx.layouts.LayoutManager
import ktfx.layouts.button
import ktfx.layouts.columnConstraints
import ktfx.layouts.gridPane
import ktfx.layouts.label
import ktfx.layouts.line
import ktfx.layouts.pane
import ktfx.layouts.region
import ktfx.layouts.textFlow
import ktfx.layouts.vbox
import ktfx.scene.layout.gap
import ktfx.scene.layout.paddingAll
import java.util.ResourceBundle

class ViewInvoicePopOver(invoice: Invoice) : SimplePopOver(object : Resourced {
    override val resources: ResourceBundle = Language.ofFullCode(transaction {
        findGlobalSettings(KEY_LANGUAGE).single().value
    }).toResourcesBundle()
}, R.string.invoice) {

    private lateinit var invoiceHeaders: List<String>
    private lateinit var customer: Customer
    private lateinit var employee: Employee

    init {
        graphic = ktfx.layouts.label(language.toString())
        transaction {
            invoiceHeaders = findGlobalSettings(KEY_INVOICE_HEADERS).single().valueList
            employee = Employees[invoice.employeeId].single()
            customer = Customers[invoice.customerId].single()
        }
        pane {
            border = Border(BorderStroke(BLACK, DASHED, EMPTY, DEFAULT))
            gridPane {
                gap = 16.0
                padding = Insets(16.0)
                minWidth = EXPECTED_WIDTH
                minHeight = EXPECTED_HEIGHT
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
                    invoiceHeaders.forEachIndexed { index, s ->
                        label(s) {
                            if (index == 0) font = getFont(R.font.sf_pro_text_bold)
                        }
                    }
                } row 0 col 0 colSpans 2
                textFlow {
                    textAlignment = TextAlignment.RIGHT
                    "${getString(R.string.invoice)}\n" { font = getFont(R.font.sf_pro_text_bold, 32) }
                    "# ${invoice.no}" { font = getFont(R.font.sf_pro_text_bold, 18) }
                } row 0 col 2
                line(endX = EXPECTED_WIDTH - 32.0) row 1 col 0 colSpans 3
                label(customer.name) {
                    font = getFont(R.font.sf_pro_text_bold, 24)
                } row 2 col 0 colSpans 2
                label(invoice.dateTime.toString(PATTERN_DATETIME_EXTENDED) + '\n' +
                    transaction { Employees[invoice.employeeId].single().name }
                ) { textAlignment = TextAlignment.RIGHT } row 2 col 2
                gridPane {
                    hgap = 16.0
                    columnConstraints {
                        constraints { hgrow = ALWAYS }
                        constraints()
                        constraints()
                    }
                    var row = 0
                    invoice.plates.run {
                        if (isNotEmpty()) {
                            label(getString(R.string.plate)) { font = getFont(R.font.sf_pro_text_bold) } row row col 0
                            row++
                            forEach {
                                label(it.title) row row col 0
                                label("${it.machine} ${numberConverter.toString(it.qty)} x " +
                                    currencyConverter.toString(it.price)) row row col 1
                                label(currencyConverter.toString(it.total)) row row col 2
                                row++
                            }
                        }
                    }
                    invoice.offsets.run {
                        if (isNotEmpty()) {
                            label(getString(R.string.offset)) { font = getFont(R.font.sf_pro_text_bold) } row row col 0
                            row++
                            forEach {
                                label(it.title) row row col 0
                                label("${it.machine} ${it.typedTechnique.toString(this@ViewInvoicePopOver)} " +
                                    numberConverter.toString(it.qty)) row row col 1
                                label(currencyConverter.toString(it.total)) row row col 2
                                row++
                            }
                        }
                    }
                    invoice.others.run {
                        if (isNotEmpty()) {
                            label(getString(R.string.others)) { font = getFont(R.font.sf_pro_text_bold) } row row col 0
                            row++
                            forEach {
                                label(it.title) row row col 0
                                label("${numberConverter.toString(it.qty)} x " +
                                    currencyConverter.toString(it.price)) row row col 1
                                label(currencyConverter.toString(it.total)) row row col 2
                                row++
                            }
                        }
                    }
                } row 3 col 0 colSpans 3 vpriority ALWAYS
                line(endX = EXPECTED_WIDTH - 32.0) row 4 col 0 colSpans 3
                textFlow {
                    paddingAll = 8.0
                    border = Border(BorderStroke(BLACK, SOLID, EMPTY, DEFAULT))
                    "${getString(R.string.note)}\n" { font = getFont(R.font.sf_pro_text_bold) }
                    invoice.note()
                } row 5 col 0 rowSpans 2
                boldLabel(currencyConverter.toString(invoice.total), 18) row 5 col 1 colSpans 2 halign RIGHT
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
        }
        buttonBar.run {
            button(getString(R.string.print)) {
                isDefaultButton = true
                isDisable = invoice.printed
            }
        }
    }

    private companion object {
        const val EXPECTED_WIDTH = 912.0 // 9.5-inch
        const val EXPECTED_HEIGHT = 528.0 // 5.5-inch

        fun LayoutManager<Node>.boldLabel(
            text: String,
            size: Int = 13
        ): Label = label(text) {
            isWrapText = true
            font = getFont(R.font.sf_pro_text_bold, size)
        }
    }
}