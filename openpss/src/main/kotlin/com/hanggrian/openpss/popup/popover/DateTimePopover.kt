package com.hanggrian.openpss.popup.popover

import com.hanggrian.openpss.Context
import com.hanggrian.openpss.R
import com.hanggrian.openpss.control.DateBox
import com.hanggrian.openpss.control.TimeBox
import com.hanggrian.openpss.ui.wage.record.Record
import ktfx.coroutines.onAction
import ktfx.jfoenix.layouts.jfxButton
import ktfx.layouts.gridPane
import org.joda.time.DateTime

class DateTimePopover(
    context: Context,
    titleId: String,
    defaultButtonTextId: String,
    prefill: DateTime,
) : ResultablePopover<DateTime>(context, titleId) {
    private val dateBox: DateBox
    private lateinit var timeBox: TimeBox

    init {
        gridPane {
            hgap = getDouble(R.dimen_padding_medium)
            vgap = getDouble(R.dimen_padding_medium)
            dateBox = addChild(DateBox(prefill.toLocalDate())).grid(row = 0, col = 1)
            jfxButton("-${Record.WORKING_HOURS}") {
                onAction {
                    repeat(Record.WORKING_HOURS) {
                        timeBox.previousButton.fire()
                    }
                }
            }.grid(row = 1, col = 0)
            timeBox =
                addChild(
                    TimeBox(prefill.toLocalTime()).apply {
                        onOverlap = { plus ->
                            dateBox.picker.value =
                                when {
                                    plus -> dateBox.picker.value.plusDays(1)
                                    else -> dateBox.picker.value.minusDays(1)
                                }
                        }
                    },
                ).grid(row = 1, col = 1)
            jfxButton("+${Record.WORKING_HOURS}") {
                onAction {
                    repeat(Record.WORKING_HOURS) {
                        timeBox.nextButton.fire()
                    }
                }
            }.grid(row = 1, col = 2)
        }
        defaultButton.text = getString(defaultButtonTextId)
    }

    override val nullableResult: DateTime? get() = dateBox.value!!.toDateTime(timeBox.value)
}
