package com.hendraanggrian.openpss.ui

import com.hendraanggrian.openpss.R
import com.hendraanggrian.openpss.scene.layout.DateBox
import com.hendraanggrian.openpss.scene.layout.TimeBox
import com.hendraanggrian.openpss.scene.layout.dateBox
import com.hendraanggrian.openpss.scene.layout.timeBox
import javafx.scene.control.ButtonType.OK
import javafx.scene.control.Dialog
import javafx.scene.image.ImageView
import kotfx.application.later
import kotfx.layouts.vbox
import kotfx.scene.control.cancelButton
import kotfx.scene.control.graphicIcon
import kotfx.scene.control.headerTitle
import kotfx.scene.control.okButton
import kotfx.scene.layout.spacings
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
        dialogPane.content = vbox {
            spacings = 8
            dateBox = dateBox(prefill.toLocalDate())
            timeBox = timeBox(prefill.toLocalTime())
        }
        later { dateBox.requestFocus() }
        cancelButton()
        okButton()
        setResultConverter {
            if (it != OK) null
            else DateTime(dateBox.picker.value.year, dateBox.picker.value.monthValue, dateBox.picker.value.dayOfMonth, timeBox.time.hourOfDay, timeBox.time.minuteOfHour)
        }
    }
}