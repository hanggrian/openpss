package com.hendraanggrian.openpss.popup.popover

import com.hendraanggrian.openpss.content.FxComponent
import com.hendraanggrian.openpss.control.TimeBox
import org.joda.time.LocalTime

class TimePopover(
    component: FxComponent,
    titleId: String,
    prefill: LocalTime = LocalTime.now()
) : ResultablePopover<LocalTime>(component, titleId) {

    private val timeBox: TimeBox = TimeBox(prefill)()

    override val nullableResult: LocalTime? get() = timeBox.valueProperty().value
}