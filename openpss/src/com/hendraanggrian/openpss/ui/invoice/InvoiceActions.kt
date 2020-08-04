package com.hendraanggrian.openpss.ui.invoice

import com.hendraanggrian.openpss.R
import com.hendraanggrian.openpss.content.Action
import com.hendraanggrian.openpss.content.Context
import com.hendraanggrian.openpss.db.SessionWrapper
import com.hendraanggrian.openpss.db.schemas.Customers
import com.hendraanggrian.openpss.db.schemas.Invoice
import com.hendraanggrian.openpss.db.schemas.Invoices
import com.hendraanggrian.openpss.db.schemas.Payment
import com.hendraanggrian.openpss.db.schemas.Payments
import com.hendraanggrian.openpss.db.transaction
import kotlinx.nosql.equal

class AddInvoiceAction(context: Context, val invoice: Invoice) : Action<Invoice>(context) {

    override val log: String = getString(
        R.string._log_invoice_add, invoice.no,
        transaction {
            Customers[invoice.customerId].single().name
        }
    )

    override fun SessionWrapper.handle(): Invoice = invoice.apply { id = Invoices.insert(invoice) }
}

class DeleteInvoiceAction(context: Context, val invoice: Invoice) : Action<Unit>(context, true) {

    override val log: String = getString(
        R.string._log_invoice_delete, invoice.no,
        transaction {
            Customers[invoice.customerId].single().name
        }
    )

    override fun SessionWrapper.handle() {
        Invoices -= invoice
        Payments { Payments.invoiceId.equal(invoice.id) }.remove()
    }
}

class AddPaymentAction(context: Context, val payment: Payment, invoiceNo: Int) : Action<Unit>(context) {

    override val log: String = getString(R.string._log_payment_add, payment.value, invoiceNo)

    override fun SessionWrapper.handle() {
        Payments += payment
    }
}

class DeletePaymentAction(context: Context, val payment: Payment, invoiceNo: Int) : Action<Unit>(context, true) {

    override val log: String = getString(R.string._log_payment_delete, payment.value, invoiceNo)

    override fun SessionWrapper.handle() {
        Payments -= payment
    }
}
