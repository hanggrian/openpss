package com.hendraanggrian.openpss.ui.finance

import com.hendraanggrian.openpss.db.schemas.Payment
import com.hendraanggrian.openpss.db.schemas.Payment.Method
import com.hendraanggrian.openpss.db.schemas.Payment.Method.CASH
import com.hendraanggrian.openpss.db.schemas.Payment.Method.CHEQUE
import com.hendraanggrian.openpss.db.schemas.Payment.Method.CREDIT_CARD
import com.hendraanggrian.openpss.db.schemas.Payment.Method.DEBIT_CARD
import com.hendraanggrian.openpss.db.schemas.Payment.Method.TRANSFER
import javafx.collections.ObservableList
import javafxx.collections.toObservableList
import org.joda.time.LocalDate

data class Report(
    val date: LocalDate,
    val valueMap: Map<Method, Double>
) {
    val total: Double get() = valueMap.values.sum()

    companion object {
        fun listAll(payments: Iterable<Payment>): ObservableList<Report> = payments
            .groupBy { it.dateTime.toLocalDate() }
            .flatMap { (dateTime, payments) ->
                listOf(Report(dateTime, mapOf(
                    CASH to Payment.gather(payments, CASH),
                    CREDIT_CARD to Payment.gather(payments, CREDIT_CARD),
                    DEBIT_CARD to Payment.gather(payments, DEBIT_CARD),
                    CHEQUE to Payment.gather(payments, CHEQUE),
                    TRANSFER to Payment.gather(payments, TRANSFER))))
            }
            .toObservableList()
    }
}