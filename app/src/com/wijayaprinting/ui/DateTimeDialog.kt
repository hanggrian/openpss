package com.wijayaprinting.ui

import com.wijayaprinting.R
import com.wijayaprinting.ui.scene.layout.TimeBox
import com.wijayaprinting.ui.scene.layout.timeBox
import com.wijayaprinting.util.asJava
import javafx.scene.control.ButtonType.CANCEL
import javafx.scene.control.ButtonType.OK
import javafx.scene.control.DatePicker
import javafx.scene.control.Dialog
import javafx.scene.image.ImageView
import kotfx.*
import org.joda.time.DateTime
import org.joda.time.DateTime.now

class DateTimeDialog @JvmOverloads constructor(
        resourced: Resourced,
        header: String,
        prefill: DateTime? = null
) : Dialog<DateTime>(), Resourced by resourced {

    private lateinit var datePicker: DatePicker
    private lateinit var timeBox: TimeBox

    init {
        title = header
        headerText = header
        graphic = ImageView(R.png.ic_calendar)
        content = gridPane {
            gap(8)
            datePicker = datePicker {
                value = (prefill ?: now()).toLocalDate().asJava()
                isEditable = false // force pick from popup
                maxWidth = 128.0
                runFX { requestFocus() }
            } col 0 row 0
            timeBox = timeBox { prefill?.let { time = it.toLocalTime() } } col 1 row 0
            slider(0, 24, 0) { valueProperty() bindBidirectional timeBox.hourField.valueProperty } col 0 row 1 colSpan 2
            slider(0, 60, 0) { valueProperty() bindBidirectional timeBox.minuteField.valueProperty } col 0 row 2 colSpan 2
        }
        buttons(OK, CANCEL)
        setResultConverter {
            if (it != OK) null
            else DateTime(datePicker.value.year, datePicker.value.monthValue, datePicker.value.dayOfMonth, timeBox.time.hourOfDay, timeBox.time.minuteOfHour)
        }
    }
}