package com.hendraanggrian.openpss.ui.report

import com.hendraanggrian.openpss.db.schemas.Payment
import com.hendraanggrian.openpss.db.schemas.Payment.Method.CASH
import com.hendraanggrian.openpss.db.schemas.Payment.Method.TRANSFER
import javafx.collections.ObservableList
import ktfx.collections.toObservableList
import org.joda.time.LocalDate

data class Report(
    val date: LocalDate,
    val cash: Double,
    val transfer: Double
) {
    val total: Double get() = cash + transfer

    companion object {
        fun listAll(payments: Iterable<Payment>): ObservableList<Report> = payments
            .groupBy { it.dateTime.toLocalDate() }
            .flatMap { (dateTime, payments) ->
                listOf(Report(dateTime, Payment.gather(payments, CASH), Payment.gather(payments, TRANSFER)))
            }
            .toObservableList()
    }
}