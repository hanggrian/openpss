package com.hendraanggrian.openpss.ui.report

import org.joda.time.LocalDate

data class Report(
    val date: LocalDate,
    val cash: Double,
    val transfer: Double
) {
    val total: Double get() = cash + transfer
}