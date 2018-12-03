package com.hendraanggrian.openpss.popup.popover

import com.hendraanggrian.openpss.content.FxComponent
import com.hendraanggrian.openpss.control.DateBox
import org.joda.time.LocalDate

class DatePopover(
    component: FxComponent,
    titleId: String,
    prefill: LocalDate = LocalDate.now()
) : ResultablePopover<LocalDate>(component, titleId) {

    private val dateBox: DateBox = DateBox(prefill)()

    override val nullableResult: LocalDate? get() = dateBox.valueProperty().value
}