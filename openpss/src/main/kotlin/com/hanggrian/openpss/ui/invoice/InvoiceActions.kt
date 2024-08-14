package com.hanggrian.openpss.ui.invoice

import com.hanggrian.openpss.Action
import com.hanggrian.openpss.Context
import com.hanggrian.openpss.db.ExtendedSession
import com.hanggrian.openpss.db.schemas.Invoice
import com.hanggrian.openpss.db.schemas.Invoices
import com.hanggrian.openpss.db.schemas.Payment
import com.hanggrian.openpss.db.schemas.Payments
import kotlinx.nosql.equal

class AddInvoiceAction(context: Context, val invoice: Invoice) : Action<Invoice>(context) {
    override fun ExtendedSession.handle(): Invoice = invoice.apply { id = Invoices.insert(invoice) }
}

class DeleteInvoiceAction(context: Context, val invoice: Invoice) : Action<Unit>(context, true) {
    override fun ExtendedSession.handle() {
        Invoices -= invoice
        Payments { Payments.invoiceId.equal(invoice.id) }.remove()
    }
}

class AddPaymentAction(context: Context, val payment: Payment) : Action<Unit>(context) {
    override fun ExtendedSession.handle() {
        Payments += payment
    }
}

class DeletePaymentAction(context: Context, val payment: Payment) : Action<Unit>(context, true) {
    override fun ExtendedSession.handle() {
        Payments -= payment
    }
}
