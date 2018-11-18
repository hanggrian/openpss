package com.hendraanggrian.openpss.ui.schedule

import com.hendraanggrian.openpss.R
import com.hendraanggrian.openpss.content.Context
import com.hendraanggrian.openpss.db.schemas.Invoice
import javafx.collections.ObservableList
import ktfx.collections.mutableObservableListOf
import ktfx.util.invoke

data class Schedule(
    val invoice: Invoice,
    val jobType: String,
    val title: String,
    val qty: String = "",
    val type: String = ""
) {

    companion object {

        fun of(context: Context, invoice: Invoice): ObservableList<Schedule> {
            val schedules = mutableObservableListOf<Schedule>()
            invoice.offsetJobs.forEach {
                schedules += Schedule(
                    invoice,
                    context.getString(R.string.offset),
                    it.desc,
                    context.numberConverter(it.qty),
                    "${it.type} (${it.typedTechnique.toString(context)})"
                )
            }
            invoice.digitalJobs.forEach {
                schedules += Schedule(
                    invoice,
                    context.getString(R.string.digital),
                    it.desc,
                    context.numberConverter(it.qty),
                    when {
                        it.isTwoSide -> "${it.type} (${context.getString(R.string.two_side)})"
                        else -> it.type
                    }
                )
            }
            invoice.plateJobs.forEach {
                schedules += Schedule(
                    invoice,
                    context.getString(R.string.plate),
                    it.desc,
                    context.numberConverter(it.qty),
                    it.type
                )
            }
            invoice.otherJobs.forEach {
                schedules += Schedule(
                    invoice,
                    context.getString(R.string.others),
                    it.desc,
                    context.numberConverter(it.qty)
                )
            }
            return schedules
        }
    }
}