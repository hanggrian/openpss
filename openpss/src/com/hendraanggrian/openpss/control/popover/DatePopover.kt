package com.hendraanggrian.openpss.control.popover

import com.hendraanggrian.openpss.content.Context
import com.hendraanggrian.openpss.control.DateBox
import com.hendraanggrian.openpss.control.dateBox
import org.joda.time.LocalDate

class DatePopover(
    context: Context,
    titleId: String,
    prefill: LocalDate = LocalDate.now()
) : ResultablePopover<LocalDate>(context, titleId) {

    private val dateBox: DateBox = dateBox(prefill)

    override val nullableResult: LocalDate? get() = dateBox.valueProperty().value
}