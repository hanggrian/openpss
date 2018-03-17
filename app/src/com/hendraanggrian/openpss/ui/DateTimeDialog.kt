package com.hendraanggrian.openpss.ui

import com.hendraanggrian.openpss.R
import com.hendraanggrian.openpss.scene.layout.DateBox
import com.hendraanggrian.openpss.scene.layout.TimeBox
import com.hendraanggrian.openpss.scene.layout.dateBox
import com.hendraanggrian.openpss.scene.layout.timeBox
import com.hendraanggrian.openpss.ui.wage.Record.Companion.WORKING_HOURS
import javafx.scene.control.ButtonType.OK
import javafx.scene.control.Dialog
import javafx.scene.image.ImageView
import ktfx.application.later
import ktfx.coroutines.onAction
import ktfx.layouts.button
import ktfx.layouts.gridPane
import ktfx.scene.control.cancelButton
import ktfx.scene.control.graphicIcon
import ktfx.scene.control.headerTitle
import ktfx.scene.control.okButton
import ktfx.scene.layout.gaps
import org.joda.time.DateTime
import org.joda.time.DateTime.now

class DateTimeDialog(
    resourced: Resourced,
    title: String,
    prefill: DateTime = now()
) : Dialog<DateTime>(), Resourced by resourced {

    private lateinit var dateBox: DateBox
    private lateinit var timeBox: TimeBox

    init {
        headerTitle = title
        graphicIcon = ImageView(R.image.ic_calendar)
        dialogPane.content = gridPane {
            gaps = 8
            dateBox = dateBox(prefill.toLocalDate()) row 0 col 1
            button("-$WORKING_HOURS") {
                onAction {
                    when {
                        timeBox.hourField.value < 8 -> timeBox.hourField.value = 0
                        else -> timeBox.hourField.value -= WORKING_HOURS
                    }
                }
            } row 1 col 0
            timeBox = timeBox(prefill.toLocalTime()) row 1 col 1
            button("+$WORKING_HOURS") {
                onAction {
                    when {
                        timeBox.hourField.value > 15 -> timeBox.hourField.value = 23
                        else -> timeBox.hourField.value += WORKING_HOURS
                    }
                }
            } row 1 col 2
        }
        later { dateBox.requestFocus() }
        cancelButton()
        okButton()
        setResultConverter {
            if (it != OK) null else DateTime(
                dateBox.picker.value.year,
                dateBox.picker.value.monthValue,
                dateBox.picker.value.dayOfMonth,
                timeBox.time.hourOfDay,
                timeBox.time.minuteOfHour
            )
        }
    }
}