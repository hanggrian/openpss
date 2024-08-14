package com.hanggrian.openpss.popup.popover

import com.hanggrian.openpss.Context
import com.hanggrian.openpss.control.DateBox
import org.joda.time.LocalDate

class DatePopover(context: Context, titleId: String, prefill: LocalDate = LocalDate.now()) :
    ResultablePopover<LocalDate>(context, titleId) {
    private val dateBox = addChild(DateBox(prefill))

    override val nullableResult: LocalDate? get() = dateBox.valueProperty.value
}
