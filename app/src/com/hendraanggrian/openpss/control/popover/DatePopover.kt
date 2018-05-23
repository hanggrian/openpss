package com.hendraanggrian.openpss.control.popover

import com.hendraanggrian.openpss.layout.DateBox
import com.hendraanggrian.openpss.layout.dateBox
import com.hendraanggrian.openpss.localization.Resourced
import org.joda.time.LocalDate

class DatePopover(
    resourced: Resourced,
    titleId: String,
    prefill: LocalDate = LocalDate.now()
) : ResultablePopover<LocalDate>(resourced, titleId) {

    private val dateBox: DateBox = dateBox(prefill)

    override val optionalResult: LocalDate? get() = dateBox.valueProperty().value
}