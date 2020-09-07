package com.hendraanggrian.openpss.ui.invoice

import com.hendraanggrian.openpss.content.Action
import com.hendraanggrian.openpss.content.Context
import com.hendraanggrian.openpss.db.SessionWrapper
import com.hendraanggrian.openpss.db.schemas.Invoice
import com.hendraanggrian.openpss.db.schemas.Invoices
import com.hendraanggrian.openpss.db.schemas.Payment
import com.hendraanggrian.openpss.db.schemas.Payments
import kotlinx.nosql.equal

class AddInvoiceAction(context: Context, val invoice: Invoice) : Action<Invoice>(context) {

    override fun SessionWrapper.handle(): Invoice = invoice.apply { id = Invoices.insert(invoice) }
}

class DeleteInvoiceAction(context: Context, val invoice: Invoice) : Action<Unit>(context, true) {

    override fun SessionWrapper.handle() {
        Invoices -= invoice
        Payments { Payments.invoiceId.equal(invoice.id) }.remove()
    }
}

class AddPaymentAction(context: Context, val payment: Payment) : Action<Unit>(context) {

    override fun SessionWrapper.handle() {
        Payments += payment
    }
}

class DeletePaymentAction(context: Context, val payment: Payment) : Action<Unit>(context, true) {

    override fun SessionWrapper.handle() {
        Payments -= payment
    }
}
