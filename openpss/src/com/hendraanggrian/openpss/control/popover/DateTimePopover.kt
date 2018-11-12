package com.hendraanggrian.openpss.control.popover

import com.hendraanggrian.openpss.R
import com.hendraanggrian.openpss.content.Context
import com.hendraanggrian.openpss.control.DateBox
import com.hendraanggrian.openpss.control.TimeBox
import com.hendraanggrian.openpss.control.dateBox
import com.hendraanggrian.openpss.control.timeBox
import com.hendraanggrian.openpss.ui.wage.record.Record
import ktfx.coroutines.onAction
import ktfx.jfoenix.jfxButton
import ktfx.layouts.gridPane
import ktfx.scene.layout.gap
import org.joda.time.DateTime

class DateTimePopover(
    context: Context,
    titleId: String,
    defaultButtonTextId: String,
    prefill: DateTime
) : ResultablePopover<DateTime>(context, titleId) {

    private lateinit var dateBox: DateBox
    private lateinit var timeBox: TimeBox

    init {
        gridPane {
            gap = R.dimen.padding_medium.toDouble()
            dateBox = dateBox(prefill.toLocalDate()) row 0 col 1
            jfxButton("-${Record.WORKING_HOURS}") {
                onAction {
                    repeat(Record.WORKING_HOURS) {
                        timeBox.previousButton.fire()
                    }
                }
            } row 1 col 0
            timeBox = timeBox(prefill.toLocalTime()) {
                onOverlap = { plus ->
                    dateBox.picker.value = when {
                        plus -> dateBox.picker.value.plusDays(1)
                        else -> dateBox.picker.value.minusDays(1)
                    }
                }
            } row 1 col 1
            jfxButton("+${Record.WORKING_HOURS}") {
                onAction {
                    repeat(Record.WORKING_HOURS) {
                        timeBox.nextButton.fire()
                    }
                }
            } row 1 col 2
        }
        defaultButton.text = getString(defaultButtonTextId)
    }

    override val nullableResult: DateTime? get() = dateBox.value!!.toDateTime(timeBox.value)
}