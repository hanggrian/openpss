package com.hendraanggrian.openpss.popup.popover

import com.hendraanggrian.openpss.content.Context
import com.hendraanggrian.openpss.control.DateBox
import org.joda.time.LocalDate

class DatePopover(
    context: Context,
    titleId: String,
    prefill: LocalDate = LocalDate.now()
) : ResultablePopover<LocalDate>(context, titleId) {

    private val dateBox: DateBox = DateBox(prefill)()

    override val nullableResult: LocalDate? get() = dateBox.valueProperty().value
}