package com.hendraanggrian.openpss.ui.finance

import com.hendraanggrian.openpss.db.schemas.Payment
import javafx.collections.ObservableList
import ktfx.collections.toObservableList
import org.joda.time.LocalDate

data class Report(
    val date: LocalDate,
    val cash: Double,
    val nonCash: Double
) {

    val total: Double get() = cash + nonCash

    companion object {
        fun listAll(payments: Iterable<Payment>): ObservableList<Report> = payments
            .groupBy { it.dateTime.toLocalDate() }
            .flatMap { (dateTime, payments) ->
                listOf(
                    Report(
                        dateTime,
                        Payment.gather(payments, true),
                        Payment.gather(payments, false)
                    )
                )
            }
            .toObservableList()
    }
}