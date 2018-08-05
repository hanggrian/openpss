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
import com.hendraanggrian.openpss.util.bold
import javafx.geometry.Pos
import javafx.scene.layout.Border
import javafx.scene.layout.BorderStroke
import javafx.scene.layout.BorderStrokeStyle.DASHED
import javafx.scene.layout.BorderWidths.DEFAULT
import javafx.scene.layout.CornerRadii.EMPTY
import javafx.scene.paint.Color.BLACK
import javafxx.layouts.button
import javafxx.layouts.label
import javafxx.layouts.line
import javafxx.layouts.pane
import javafxx.layouts.vbox
import javafxx.scene.layout.paddingAll
import java.util.ResourceBundle

class ViewInvoicePopover(invoice: Invoice) : Popover(object : Resourced {
    override val resources: ResourceBundle = Language.ofFullCode(transaction {
        findGlobalSettings(KEY_LANGUAGE).single().value
    }).toResourcesBundle()
}, R.string.invoice) {

    private companion object {
        const val MAX_WIDTH = 283.46456693 // equivalent to 7.5cm
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
        pane {
            border = Border(BorderStroke(BLACK, DASHED, EMPTY, DEFAULT))
            vbox {
                paddingAll = R.dimen.padding_small.toDouble()
                spacing = R.dimen.padding_verysmall.toDouble()
                minWidth = MAX_WIDTH
                maxWidth = MAX_WIDTH
                vbox {
                    alignment = Pos.CENTER
                    invoiceHeaders.forEachIndexed { index, s ->
                        label(s) { if (index == 0) font = bold() }
                    }
                }
                line(endX = MAX_WIDTH - R.dimen.padding_small.toDouble() * 2)
            }
        }
        buttonBar.run {
            button(getString(R.string.print)) {
                isDefaultButton = true
                isDisable = invoice.printed
            }
        }
    }
}