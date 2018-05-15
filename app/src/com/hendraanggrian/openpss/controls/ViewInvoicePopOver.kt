package com.hendraanggrian.openpss.controls

import com.hendraanggrian.openpss.R
import com.hendraanggrian.openpss.db.schemas.Customer
import com.hendraanggrian.openpss.db.schemas.Customers
import com.hendraanggrian.openpss.db.schemas.Employee
import com.hendraanggrian.openpss.db.schemas.Employees
import com.hendraanggrian.openpss.db.schemas.GlobalSetting.Companion.KEY_CURRENCY_LANGUAGE
import com.hendraanggrian.openpss.db.schemas.GlobalSetting.Companion.KEY_INVOICE_HEADERS
import com.hendraanggrian.openpss.db.schemas.Invoice
import com.hendraanggrian.openpss.db.transaction
import com.hendraanggrian.openpss.resources.Resourced
import com.hendraanggrian.openpss.util.PATTERN_DATE
import com.hendraanggrian.openpss.util.PATTERN_TIME
import com.hendraanggrian.openpss.util.currencyConverter
import com.hendraanggrian.openpss.util.getFont
import com.hendraanggrian.openpss.util.numberConverter
import javafx.geometry.Pos.BOTTOM_CENTER
import javafx.geometry.Pos.CENTER
import javafx.geometry.Pos.CENTER_RIGHT
import javafx.scene.Node
import javafx.scene.control.Label
import javafx.scene.layout.Priority.ALWAYS
import ktfx.layouts.LayoutManager
import ktfx.layouts.button
import ktfx.layouts.hbox
import ktfx.layouts.label
import ktfx.layouts.line
import ktfx.layouts.region
import ktfx.layouts.vbox
import java.util.ResourceBundle
import java.util.ResourceBundle.getBundle

class ViewInvoicePopOver(invoice: Invoice) : SimplePopOver(object : Resourced {
    override val resources: ResourceBundle = getBundle("string_${transaction {
        findGlobalSettings(KEY_CURRENCY_LANGUAGE).single().value
    }}")
}, R.string.invoice) {

    private lateinit var invoiceHeaders: List<String>
    private lateinit var customer: Customer
    private lateinit var employee: Employee

    init {
        transaction {
            invoiceHeaders = findGlobalSettings(KEY_INVOICE_HEADERS).single().valueList
            employee = Employees[invoice.employeeId].single()
            customer = Customers[invoice.customerId].single()
        }
        vbox(16.0) {
            minWidth = MAX_WIDTH
            maxWidth = MAX_WIDTH
            alignment = CENTER_RIGHT
            vbox {
                alignment = CENTER
                invoiceHeaders.forEachIndexed { index, s ->
                    when (index) {
                        0 -> boldLabel(s)
                        else -> label(s)
                    }
                }
            }
            vbox {
                hbox {
                    alignment = BOTTOM_CENTER
                    boldLabel("#${invoice.no}", 18)
                    vbox {
                        alignment = CENTER_RIGHT
                        label(invoice.dateTime.toString(PATTERN_DATE))
                        label(invoice.dateTime.toString(PATTERN_TIME))
                    } hpriority ALWAYS
                }
                boldLabel(customer.name, 18)
                label("${getString(R.string.employee)}: ${employee.name}")
            }
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
                            label("  ${numberConverter.toString(it.qty)} x " + currencyConverter.toString(it.price))
                            region() hpriority ALWAYS
                            label(currencyConverter.toString(it.total))
                        }
                    }
                }
            }
            boldLabel(currencyConverter.toString(invoice.total), 18)
            if (invoice.note.isNotBlank()) {
                vbox {
                    boldLabel(getString(R.string.note))
                    label(invoice.note)
                }
            }
            vbox {
                alignment = CENTER
                region { prefHeight = 50.0 }
                line(endX = 200.0)
                label(getString(R.string.signature))
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
        const val MAX_WIDTH = 283.46456693 // equivalent to 7.5cm

        fun LayoutManager<Node>.boldLabel(
            text: String,
            size: Int = 13
        ): Label = label(text) {
            isWrapText = true
            font = getFont(R.font.sf_pro_text_bold, size)
        }
    }
}