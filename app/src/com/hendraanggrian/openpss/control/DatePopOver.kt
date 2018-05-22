package com.hendraanggrian.openpss.control

import com.hendraanggrian.openpss.layout.DateBox
import com.hendraanggrian.openpss.layout.dateBox
import com.hendraanggrian.openpss.localization.Resourced
import org.joda.time.LocalDate

class DatePopOver(
    resourced: Resourced,
    titleId: String,
    prefill: LocalDate = LocalDate.now()
) : DefaultPopOver<LocalDate>(resourced, titleId) {

    private val dateBox: DateBox = dateBox(prefill)

    override fun getResult(): LocalDate = dateBox.valueProperty().value
}