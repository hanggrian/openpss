package com.hendraanggrian.openpss.ui.schedule

import com.hendraanggrian.openpss.FxComponent
import com.hendraanggrian.openpss.R2
import com.hendraanggrian.openpss.schema.Invoice
import com.hendraanggrian.openpss.schema.typedTechnique
import javafx.collections.ObservableList
import ktfx.collections.mutableObservableListOf
import ktfx.text.invoke

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
                    component.getString(R2.string.offset),
                    it.desc,
                    component.numberConverter(it.qty),
                    "${it.type} (${it.typedTechnique.toString(component)})"
                )
            }
            invoice.digitalJobs.forEach {
                schedules += Schedule(
                    invoice,
                    component.getString(R2.string.digital),
                    it.desc,
                    component.numberConverter(it.qty),
                    when {
                        it.isTwoSide -> "${it.type} (${component.getString(R2.string.two_side)})"
                        else -> it.type
                    }
                )
            }
            invoice.plateJobs.forEach {
                schedules += Schedule(
                    invoice,
                    component.getString(R2.string.plate),
                    it.desc,
                    component.numberConverter(it.qty),
                    it.type
                )
            }
            invoice.otherJobs.forEach {
                schedules += Schedule(
                    invoice,
                    component.getString(R2.string.others),
                    it.desc,
                    component.numberConverter(it.qty)
                )
            }
            return schedules
        }
    }
}
