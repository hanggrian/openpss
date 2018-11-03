package com.hendraanggrian.openpss.ui.invoice

import com.hendraanggrian.openpss.content.Action
import com.hendraanggrian.openpss.content.Context
import com.hendraanggrian.openpss.db.schemas.Invoice
import com.hendraanggrian.openpss.db.schemas.Invoices
import com.hendraanggrian.openpss.db.schemas.Payments
import kotlinx.nosql.equal

class AddInvoiceAction(context: Context, invoice: Invoice) :
    Action<Invoice>(context, "Created an invoice '${invoice.no}' with total of '${invoice.total}'", {
        invoice.apply { id = Invoices.insert(invoice) }
    })

class DeleteInvoiceAction(context: Context, invoice: Invoice) :
    Action<Unit>(context, "Deleted an invoice '${invoice.id}' with total of '${invoice.total}'", {
        Invoices -= invoice
        Payments { Payments.invoiceId.equal(invoice.id) }.remove()
    })