package com.hendraanggrian.openpss.popup.popover

import com.hendraanggrian.openpss.content.Context
import com.hendraanggrian.openpss.control.TimeBox
import org.joda.time.LocalTime

class TimePopover(
    context: Context,
    titleId: String,
    prefill: LocalTime = LocalTime.now()
) : ResultablePopover<LocalTime>(context, titleId) {

    private val timeBox: TimeBox = TimeBox(prefill)()

    override val nullableResult: LocalTime? get() = timeBox.valueProperty().value
}