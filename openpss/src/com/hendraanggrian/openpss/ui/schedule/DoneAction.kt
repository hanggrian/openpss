package com.hendraanggrian.openpss.ui.schedule

import com.hendraanggrian.openpss.R
import com.hendraanggrian.openpss.content.Action
import com.hendraanggrian.openpss.content.Context
import com.hendraanggrian.openpss.db.SessionWrapper
import com.hendraanggrian.openpss.db.schemas.Invoice
import com.hendraanggrian.openpss.db.schemas.Invoices
import kotlinx.nosql.update

class DoneAction(context: Context, val invoice: Invoice) : Action<Unit>(context) {

    override val log: String = getString(R.string._log_schedule_done, invoice.jobs.size, invoice.no)

    override fun SessionWrapper.handle() {
        Invoices[invoice].projection { isDone }.update(true)
    }
}