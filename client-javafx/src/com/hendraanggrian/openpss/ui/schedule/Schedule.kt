package com.hendraanggrian.openpss.ui.schedule

import com.hendraanggrian.openpss.R
import com.hendraanggrian.openpss.ui.FxComponent
import com.hendraanggrian.openpss.data.Invoice
import com.hendraanggrian.openpss.schema.typedTechnique
import javafx.collections.ObservableList
import ktfx.collections.mutableObservableListOf
import ktfx.invoke

data class Schedule(
    val invoice: Invoice,
    val jobType: String,
    val title: String,
    val qty: String = "",
    val type: String = ""
) {

    companion object {

        fun of(component: FxComponent, invoice: Invoice): ObservableList<Schedule> {
            val schedules = mutableObservableListOf<Schedule>()
            invoice.offsetJobs.forEach {
                schedules += Schedule(
                    invoice,
                    component.getString(R.string.offset),
                    it.desc,
                    component.numberConverter(it.qty),
                    "${it.type} (${it.typedTechnique.toString(component)})"
                )
            }
            invoice.digitalJobs.forEach {
                schedules += Schedule(
                    invoice,
                    component.getString(R.string.digital),
                    it.desc,
                    component.numberConverter(it.qty),
                    when {
                        it.isTwoSide -> "${it.type} (${component.getString(R.string.two_side)})"
                        else -> it.type
                    }
                )
            }
            invoice.plateJobs.forEach {
                schedules += Schedule(
                    invoice,
                    component.getString(R.string.plate),
                    it.desc,
                    component.numberConverter(it.qty),
                    it.type
                )
            }
            invoice.otherJobs.forEach {
                schedules += Schedule(
                    invoice,
                    component.getString(R.string.others),
                    it.desc,
                    component.numberConverter(it.qty)
                )
            }
            return schedules
        }
    }
}