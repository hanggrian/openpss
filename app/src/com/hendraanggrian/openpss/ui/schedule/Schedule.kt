package com.hendraanggrian.openpss.ui.schedule

import com.hendraanggrian.openpss.db.schemas.Invoice
import javafx.collections.ObservableList
import ktfx.collections.toObservableList

data class Schedule(
    val type: Type,
    val title: String
) {
    companion object {
        /** Invoices that aren't done yet. */
        fun from(invoices: Iterable<Invoice>): ObservableList<Schedule> =
            (invoices.flatMap { it.plates }.map { Schedule(Type.PLATE, it.title) } +
                invoices.flatMap { it.offsets }.map { Schedule(Type.OFFSET, it.title) } +
                invoices.flatMap { it.others }.map { Schedule(Type.OTHER, it.title) })
                .toObservableList()
    }

    enum class Type {
        PLATE, OFFSET, OTHER
    }
}