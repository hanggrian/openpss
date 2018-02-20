package com.wijayaprinting.ui

import com.wijayaprinting.R
import com.wijayaprinting.scene.layout.DateBox
import com.wijayaprinting.scene.layout.TimeBox
import com.wijayaprinting.scene.layout.dateBox
import com.wijayaprinting.scene.layout.timeBox
import javafx.scene.control.ButtonType.CANCEL
import javafx.scene.control.ButtonType.OK
import javafx.scene.control.Dialog
import javafx.scene.image.Image
import javafx.scene.image.ImageView
import kotfx.coroutines.resultConverter
import kotfx.dialogs.addButtons
import kotfx.dialogs.content
import kotfx.dialogs.icon
import kotfx.layout.vbox
import kotfx.runLater
import org.joda.time.DateTime
import org.joda.time.DateTime.now

class DateTimeDialog(
    resourced: Resourced,
    header: String,
    prefill: DateTime = now()
) : Dialog<DateTime>(), Resourced by resourced {

    private lateinit var dateBox: DateBox
    private lateinit var timeBox: TimeBox

    init {
        icon = Image(R.image.ic_launcher)
        title = header
        headerText = header
        graphic = ImageView(R.image.ic_calendar)
        content = vbox {
            spacing = 8.0
            dateBox = dateBox(prefill.toLocalDate())
            timeBox = timeBox(prefill.toLocalTime())
        }
        runLater { dateBox.requestFocus() }
        addButtons(OK, CANCEL)
        resultConverter {
            if (it != OK) null
            else DateTime(dateBox.picker.value.year, dateBox.picker.value.monthValue, dateBox.picker.value.dayOfMonth, timeBox.time.hourOfDay, timeBox.time.minuteOfHour)
        }
    }
}