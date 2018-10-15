package com.hendraanggrian.openpss.ui.schedule

import com.hendraanggrian.openpss.db.schemas.Invoices
import com.hendraanggrian.openpss.numberConverter
import kotlinx.nosql.Id
import ktfx.util.invoke

data class Schedule(
    val invoiceId: Id<String, Invoices>?,
    val firstColumn: String,
    val title: String,
    val qty: String = "",
    val type: String = ""
) {

    constructor(
        invoiceId: Id<String, Invoices>?,
        firstColumn: String,
        title: String,
        qty: Int,
        type: String = ""
    ) : this(invoiceId, firstColumn, title, numberConverter(qty), type)

    fun isNode(): Boolean = invoiceId != null

    fun isChild(): Boolean = invoiceId == null
}