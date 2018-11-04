package com.hendraanggrian.openpss.ui.schedule

import com.hendraanggrian.openpss.content.numberConverter
import com.hendraanggrian.openpss.db.schemas.Invoice
import ktfx.util.invoke

data class Schedule(
    val invoice: Invoice,
    val firstColumn: String,
    val title: String,
    val qty: String = "",
    val type: String = ""
) {

    constructor(
        invoice: Invoice,
        firstColumn: String,
        title: String,
        qty: Int,
        type: String = ""
    ) : this(invoice, firstColumn, title, numberConverter(qty), type)
}