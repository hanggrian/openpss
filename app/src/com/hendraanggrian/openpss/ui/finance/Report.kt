package com.hendraanggrian.openpss.ui.finance

import com.hendraanggrian.openpss.db.schemas.Payment
import javafx.collections.ObservableList
import ktfx.collections.toObservableList
import org.joda.time.LocalDate

data class Report(
    val date: LocalDate,
    val cash: Double,
    val others: Double
) {
    val total: Double get() = cash + others

    companion object {
        fun listAll(payments: Iterable<Payment>): ObservableList<Report> = payments
            .groupBy { it.dateTime.toLocalDate() }
            .flatMap { (dateTime, payments) ->
                listOf(Report(dateTime, Payment.gather(payments), Payment.gather(payments, false)))
            }
            .toObservableList()
    }
}