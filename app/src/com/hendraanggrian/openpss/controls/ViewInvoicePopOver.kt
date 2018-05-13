package com.hendraanggrian.openpss.controls

import com.hendraanggrian.openpss.R
import com.hendraanggrian.openpss.db.schemas.Customer
import com.hendraanggrian.openpss.db.schemas.Customers
import com.hendraanggrian.openpss.db.schemas.Employee
import com.hendraanggrian.openpss.db.schemas.Employees
import com.hendraanggrian.openpss.db.schemas.GlobalSetting.Companion.KEY_INVOICE_HEADERS
import com.hendraanggrian.openpss.db.schemas.Invoice
import com.hendraanggrian.openpss.db.transaction
import com.hendraanggrian.openpss.resources.Resourced
import com.hendraanggrian.openpss.util.PATTERN_DATETIME_EXTENDED
import com.hendraanggrian.openpss.util.currencyConverter
import com.hendraanggrian.openpss.util.getFont
import com.hendraanggrian.openpss.util.numberConverter
import javafx.geometry.Pos.CENTER
import javafx.geometry.Pos.CENTER_RIGHT
import javafx.scene.Node
import javafx.scene.control.Label
import javafx.scene.layout.Priority.ALWAYS
import javafx.scene.layout.Region
import ktfx.layouts.LayoutManager
import ktfx.layouts.button
import ktfx.layouts.gridPane
import ktfx.layouts.hbox
import ktfx.layouts.label
import ktfx.layouts.line
import ktfx.layouts.region
import ktfx.layouts.vbox

class ViewInvoicePopOver(resourced: Resourced, invoice: Invoice) : SimplePopOver(resourced, R.string.invoice) {

    private lateinit var invoiceHeaders: List<String>
    private lateinit var customer: Customer
    private lateinit var employee: Employee

    init {
        transaction {
            invoiceHeaders = findGlobalSettings(KEY_INVOICE_HEADERS).single().valueList
            employee = Employees[invoice.employeeId].single()
            customer = Customers[invoice.customerId].single()
        }
        vbox {
            maxWidth()
            alignment = CENTER
            invoiceHeaders.forEachIndexed { index, s ->
                when (index) {
                    0 -> boldLabel(s)
                    else -> regularLabel(s)
                }
            }
            line(endX = MAX_WIDTH)
            vbox {
                alignment = CENTER
                regularLabel(invoice.dateTime.toString(PATTERN_DATETIME_EXTENDED))
                gridPane {
                    maxWidth()
                    hgap = 8.0
                    regularLabel(getString(R.string.id)) row 0 col 0
                    boldLabel(invoice.no.toString()) row 0 col 1
                    regularLabel(getString(R.string.customer)) row 1 col 0
                    boldLabel(customer.name) row 1 col 1
                    regularLabel(customer.id.toString()) row 2 col 1
                }
            }
            invoice.plates.run {
                if (isNotEmpty()) {
                    line(endX = MAX_WIDTH)
                    vbox {
                        maxWidth()
                        boldLabel(getString(R.string.plate))
                        forEach {
                            regularLabel(it.title)
                            hbox {
                                maxWidth()
                                regularLabel("  ${it.machine} ${numberConverter.toString(it.qty)} x " +
                                    currencyConverter.toString(it.price))
                                region() hpriority ALWAYS
                                regularLabel(currencyConverter.toString(it.total))
                            }
                        }
                    }
                }
            }
            invoice.offsets.run {
                if (isNotEmpty()) {
                    line(endX = MAX_WIDTH)
                    vbox {
                        maxWidth()
                        boldLabel(getString(R.string.offset))
                        forEach {
                            regularLabel(it.title)
                            hbox {
                                maxWidth()
                                regularLabel("  ${it.machine} ${numberConverter.toString(it.qty)}")
                                region() hpriority ALWAYS
                                regularLabel(currencyConverter.toString(it.total))
                            }
                        }
                    }
                }
            }
            invoice.others.run {
                if (isNotEmpty()) {
                    line(endX = MAX_WIDTH)
                    vbox {
                        maxWidth()
                        boldLabel(getString(R.string.others))
                        forEach {
                            regularLabel(it.title)
                            hbox {
                                maxWidth()
                                regularLabel("  ${numberConverter.toString(it.qty)} x " +
                                    currencyConverter.toString(it.price))
                                region() hpriority ALWAYS
                                regularLabel(currencyConverter.toString(it.total))
                            }
                        }
                    }
                }
            }
            line(endX = MAX_WIDTH)
            hbox {
                maxWidth()
                alignment = CENTER_RIGHT
                boldLabel("${getString(R.string.total)} ${currencyConverter.toString(invoice.total)}")
            }
            if (invoice.note.isNotBlank()) {
                line(endX = MAX_WIDTH)
                vbox {
                    maxWidth()
                    boldLabel(getString(R.string.note))
                    label(invoice.note)
                }
            }
            hbox {
                vbox {
                    halfWidth()
                    alignment = CENTER
                    boldLabel(getString(R.string.employee))
                    region { prefHeight = 48.0 }
                    regularLabel(employee.name)
                }
                vbox {
                    halfWidth()
                    alignment = CENTER
                    boldLabel(getString(R.string.customer))
                    region { prefHeight = 48.0 }
                    regularLabel(customer.name.split(' ')[0])
                }
            } marginTop 8.0
        }
        buttonBar.run {
            button(getString(R.string.print)) {
                isDefaultButton = true
                isDisable = invoice.printed
            }
        }
    }

    private companion object {
        const val MAX_WIDTH = 285.0 // equivalent to 7.5cm

        fun Region.maxWidth() {
            minWidth = MAX_WIDTH
            maxWidth = MAX_WIDTH
        }

        fun Region.halfWidth() {
            minWidth = MAX_WIDTH / 2
            maxWidth = MAX_WIDTH / 2
        }

        fun LayoutManager<Node>.regularLabel(text: String): Label = label(text)

        fun LayoutManager<Node>.boldLabel(text: String): Label = label(text) {
            font = getFont(R.font.sf_pro_text_bold)
        }
    }
}