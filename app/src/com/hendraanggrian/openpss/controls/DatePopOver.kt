package com.hendraanggrian.openpss.controls

import com.hendraanggrian.openpss.layouts.DateBox
import com.hendraanggrian.openpss.layouts.dateBox
import com.hendraanggrian.openpss.internationalization.Resourced
import org.joda.time.LocalDate

class DatePopOver(
    resourced: Resourced,
    titleId: String,
    prefill: LocalDate = LocalDate.now()
) : DefaultPopOver<LocalDate>(resourced, titleId) {

    private val dateBox: DateBox = dateBox(prefill)

    override fun getResult(): LocalDate = dateBox.valueProperty().value
}