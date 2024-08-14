package com.hanggrian.openpss.popup.popover

import com.hanggrian.openpss.Context
import com.hanggrian.openpss.control.TimeBox
import org.joda.time.LocalTime

class TimePopover(context: Context, titleId: String, prefill: LocalTime = LocalTime.now()) :
    ResultablePopover<LocalTime>(context, titleId) {
    private val timeBox = addChild(TimeBox(prefill))

    override val nullableResult: LocalTime? get() = timeBox.valueProperty.value
}
