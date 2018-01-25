package com.wijayaprinting.ui

import com.wijayaprinting.R
import com.wijayaprinting.ui.scene.control.explicitDatePicker
import com.wijayaprinting.ui.scene.layout.TimeBox
import com.wijayaprinting.ui.scene.layout.timeBox
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
        prefill: DateTime = now()
) : Dialog<DateTime>(), Resourced by resourced {

    private lateinit var datePicker: DatePicker
    private lateinit var timeBox: TimeBox

    init {
        title = header
        headerText = header
        graphic = ImageView(R.png.ic_calendar)
        content = gridPane {
            gap(8)

            datePicker = explicitDatePicker(prefill.toLocalDate()) col 1 row 0
            button(graphic = ImageView(R.png.btn_arrow_left)) {
                setOnAction {
                    datePicker.value = datePicker.value.minusDays(1)
                }
            } col 0 row 0
            button(graphic = ImageView(R.png.btn_arrow_right)) {
                setOnAction {
                    datePicker.value = datePicker.value.plusDays(1)
                }
            } col 2 row 0

            timeBox = timeBox(prefill.toLocalTime()) col 1 row 1
            slider(0, 24, 0) { valueProperty() bindBidirectional timeBox.hourField.valueProperty } col 0 row 2 colSpan 3
            slider(0, 60, 0) { valueProperty() bindBidirectional timeBox.minuteField.valueProperty } col 0 row 3 colSpan 3
        }
        runLater { datePicker.requestFocus() }
        buttons(OK, CANCEL)
        setResultConverter {
            if (it != OK) null
            else DateTime(datePicker.value.year, datePicker.value.monthValue, datePicker.value.dayOfMonth, timeBox.time.hourOfDay, timeBox.time.minuteOfHour)
        }
    }
}