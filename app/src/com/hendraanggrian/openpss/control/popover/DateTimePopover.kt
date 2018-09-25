package com.hendraanggrian.openpss.control.popover

import com.hendraanggrian.openpss.R
import com.hendraanggrian.openpss.i18n.Resourced
import com.hendraanggrian.openpss.layout.DateBox
import com.hendraanggrian.openpss.layout.TimeBox
import com.hendraanggrian.openpss.layout.dateBox
import com.hendraanggrian.openpss.layout.timeBox
import com.hendraanggrian.openpss.ui.wage.record.Record
import javafxx.coroutines.onAction
import javafxx.layouts.button
import javafxx.layouts.gridPane
import javafxx.scene.layout.gap
import org.joda.time.DateTime

class DateTimePopover(
    resourced: Resourced,
    titleId: String,
    defaultButtonTextId: String,
    prefill: DateTime
) : ResultablePopover<DateTime>(resourced, titleId) {

    private lateinit var dateBox: DateBox
    private lateinit var timeBox: TimeBox

    init {
        gridPane {
            gap = R.dimen.padding_medium.toDouble()
            dateBox = dateBox(prefill.toLocalDate()) row 0 col 1
            button("-${Record.WORKING_HOURS}") {
                onAction { _ -> repeat(Record.WORKING_HOURS) { timeBox.previousButton.fire() } }
            } row 1 col 0
            timeBox = timeBox(prefill.toLocalTime()) {
                setOnOverlap { plus ->
                    dateBox.picker.value = when {
                        plus -> dateBox.picker.value.plusDays(1)
                        else -> dateBox.picker.value.minusDays(1)
                    }
                }
            } row 1 col 1
            button("+${Record.WORKING_HOURS}") {
                onAction { _ -> repeat(Record.WORKING_HOURS) { timeBox.nextButton.fire() } }
            } row 1 col 2
        }
        defaultButton.text = getString(defaultButtonTextId)
    }

    override val nullableResult: DateTime? get() = dateBox.value.toDateTime(timeBox.value)
}