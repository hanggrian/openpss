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
import com.hendraanggrian.openpss.util.PATTERN_DATE
import com.hendraanggrian.openpss.util.PATTERN_TIME
import com.hendraanggrian.openpss.util.currencyConverter
import com.hendraanggrian.openpss.util.getFont
import com.hendraanggrian.openpss.util.numberConverter
import javafx.geometry.HPos.RIGHT
import javafx.geometry.Insets
import javafx.geometry.Pos
import javafx.geometry.Pos.CENTER_RIGHT
import javafx.scene.Node
import javafx.scene.control.Label
import javafx.scene.image.ImageView
import javafx.scene.layout.Border
import javafx.scene.layout.BorderStroke
import javafx.scene.layout.BorderStrokeStyle.DASHED
import javafx.scene.layout.BorderStrokeStyle.SOLID
import javafx.scene.layout.BorderWidths.DEFAULT
import javafx.scene.layout.CornerRadii.EMPTY
import javafx.scene.layout.Priority.ALWAYS
import javafx.scene.layout.Priority.NEVER
import javafx.scene.paint.Color.BLACK
import ktfx.layouts.LayoutManager
import ktfx.layouts.button
import ktfx.layouts.columnConstraints
import ktfx.layouts.gridPane
import ktfx.layouts.hbox
import ktfx.layouts.label
import ktfx.layouts.line
import ktfx.layouts.pane
import ktfx.layouts.region
import ktfx.layouts.vbox
import ktfx.scene.layout.gap
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
                minWidth = MAX_WIDTH
                maxWidth = MAX_WIDTH
                minHeight = MAX_HEIGHT
                maxHeight = MAX_HEIGHT
                columnConstraints {
                    constraints { hgrow = ALWAYS }
                    constraints { hgrow = NEVER }
                    constraints { hgrow = NEVER }
                }
                var row = 0
                vbox {
                    invoiceHeaders.forEachIndexed { index, s ->
                        when (index) {
                            0 -> boldLabel(s)
                            else -> label(s)
                        }
                    }
                } row row col 0 colSpans 2
                vbox {
                    alignment = CENTER_RIGHT
                    boldLabel(getString(R.string.invoice), 32)
                    boldLabel("#${invoice.no}", 18)
                } row row col 2
                row++
                line(endX = MAX_WIDTH - 32.0) row row col 0 colSpans 3
                row++
                vbox {
                    label(customer.name, ImageView(R.image.text_customer)) {
                        font = getFont(R.font.sf_pro_text_bold, 24)
                    }
                    label("${getString(R.string.employee)}: ${transaction {
                        Employees[invoice.employeeId].single().name
                    }}") marginLeft 30.0
                } row row col 0 colSpans 2
                label(invoice.dateTime.toString(PATTERN_DATE) + '\n' + invoice.dateTime.toString(PATTERN_TIME),
                    ImageView(R.image.text_since)) row row col 2 halign RIGHT
                row++
                vbox {
                    invoice.plates.run {
                        if (isNotEmpty()) vbox {
                            boldLabel(getString(R.string.plate))
                            forEach {
                                label(it.title)
                                hbox {
                                    label("  ${it.machine} ${numberConverter.toString(it.qty)} x " +
                                        currencyConverter.toString(it.price))
                                    region() hpriority ALWAYS
                                    label(currencyConverter.toString(it.total))
                                }
                            }
                        }
                    }
                    invoice.offsets.run {
                        if (isNotEmpty()) vbox {
                            boldLabel(getString(R.string.offset))
                            forEach {
                                label(it.title)
                                hbox {
                                    label("  ${it.machine} ${it.typedTechnique.toString(this@ViewInvoicePopOver)} " +
                                        numberConverter.toString(it.qty))
                                    region() hpriority ALWAYS
                                    label(currencyConverter.toString(it.total))
                                }
                            }
                        }
                    }
                    invoice.others.run {
                        if (isNotEmpty()) vbox {
                            boldLabel(getString(R.string.others))
                            forEach {
                                label(it.title)
                                hbox {
                                    label("  ${numberConverter.toString(it.qty)} x " +
                                        currencyConverter.toString(it.price))
                                    region() hpriority ALWAYS
                                    label(currencyConverter.toString(it.total))
                                }
                            }
                        }
                    }
                } row row col 0 colSpans 3 vpriority ALWAYS
                row++
                line(endX = MAX_WIDTH - 32.0) row row col 0 colSpans 3
                row++
                boldLabel(currencyConverter.toString(invoice.total), 18) row row col 2 halign RIGHT
                row++
                label(invoice.note, ImageView(R.image.text_note)) {
                    maxWidth = Double.MAX_VALUE
                    border = Border(BorderStroke(BLACK, SOLID, EMPTY, DEFAULT))
                } row row col 0 hpriority ALWAYS
                vbox {
                    alignment = Pos.CENTER
                    region { prefHeight = 50.0 }
                    line(endX = 200.0)
                    label(getString(R.string.signature))
                } row row col 1
                vbox {
                    alignment = Pos.CENTER
                    region { prefHeight = 50.0 }
                    line(endX = 200.0)
                    label(getString(R.string.signature))
                } row row col 2
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
        const val MAX_WIDTH = 912.0 // equivalent to 9.5"
        const val MAX_HEIGHT = 528.0 // equivalent to 5.5"

        fun LayoutManager<Node>.boldLabel(
            text: String,
            size: Int = 13
        ): Label = label(text) {
            isWrapText = true
            font = getFont(R.font.sf_pro_text_bold, size)
        }
    }
}