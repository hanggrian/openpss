package com.hendraanggrian.openpss.ui.invoice

import com.hendraanggrian.openpss.R
import com.hendraanggrian.openpss.content.Action
import com.hendraanggrian.openpss.content.Context
import com.hendraanggrian.openpss.db.SessionWrapper
import com.hendraanggrian.openpss.db.schemas.Customers
import com.hendraanggrian.openpss.db.schemas.Invoice
import com.hendraanggrian.openpss.db.schemas.Invoices
import com.hendraanggrian.openpss.db.schemas.Payments
import com.hendraanggrian.openpss.db.transaction
import kotlinx.nosql.equal

class AddInvoiceAction(context: Context, val invoice: Invoice) : Action<Invoice>(context) {

    override val message: String = getString(R.string._event_invoice_add, invoice.no, transaction {
        Customers[invoice.customerId].single().name
    })

    override fun SessionWrapper.handle(): Invoice = invoice.apply { id = Invoices.insert(invoice) }
}

class DeleteInvoiceAction(context: Context, val invoice: Invoice) : Action<Unit>(context) {

    override val message: String = getString(R.string._event_invoice_delete, invoice.no, transaction {
        Customers[invoice.customerId].single().name
    })

    override fun SessionWrapper.handle() {
        Invoices -= invoice
        Payments { Payments.invoiceId.equal(invoice.id) }.remove()
    }
}