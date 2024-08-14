package com.hanggrian.openpss.ui.schedule

import com.hanggrian.openpss.Context
import com.hanggrian.openpss.R
import com.hanggrian.openpss.db.schemas.Invoice
import javafx.collections.ObservableList
import ktfx.collections.mutableObservableListOf
import ktfx.text.invoke

data class Schedule(
    val invoice: Invoice,
    val jobType: String,
    val title: String,
    val qty: String = "",
    val type: String = "",
) {
    companion object {
        fun of(context: Context, invoice: Invoice): ObservableList<Schedule> {
            val schedules = mutableObservableListOf<Schedule>()
            invoice.offsetJobs.forEach {
                schedules +=
                    Schedule(
                        invoice,
                        context.getString(R.string_offset),
                        it.desc,
                        context.numberConverter(it.qty),
                        "${it.type} (${it.typedTechnique.toString(context)})",
                    )
            }
            invoice.digitalJobs.forEach {
                schedules +=
                    Schedule(
                        invoice,
                        context.getString(R.string_digital),
                        it.desc,
                        context.numberConverter(it.qty),
                        when {
                            it.isTwoSide -> "${it.type} (${context.getString(R.string_two_side)})"
                            else -> it.type
                        },
                    )
            }
            invoice.plateJobs.forEach {
                schedules +=
                    Schedule(
                        invoice,
                        context.getString(R.string_plate),
                        it.desc,
                        context.numberConverter(it.qty),
                        it.type,
                    )
            }
            invoice.otherJobs.forEach {
                schedules +=
                    Schedule(
                        invoice,
                        context.getString(R.string_others),
                        it.desc,
                        context.numberConverter(it.qty),
                    )
            }
            return schedules
        }
    }
}
