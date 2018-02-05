package com.wijayaprinting.ui

import com.wijayaprinting.R
import com.wijayaprinting.ui.scene.control.forcedDatePicker
import com.wijayaprinting.ui.scene.layout.TimeBox
import com.wijayaprinting.ui.scene.layout.timeBox
import javafx.scene.control.ButtonType.CANCEL
import javafx.scene.control.ButtonType.OK
import javafx.scene.control.DatePicker
import javafx.scene.control.Dialog
import javafx.scene.image.Image
import javafx.scene.image.ImageView
import kotfx.dialogs.buttons
import kotfx.dialogs.content
import kotfx.dialogs.icon
import kotfx.gap
import kotfx.runLater
import kotfx.scene.button
import kotfx.scene.gridPane
import org.joda.time.DateTime
import org.joda.time.DateTime.now
import org.joda.time.LocalTime.MIDNIGHT

class DateTimeDialog @JvmOverloads constructor(
        resourced: Resourced,
        header: String,
        prefill: DateTime = now().toLocalDate().toDateTime(MIDNIGHT)
) : Dialog<DateTime>(), Resourced by resourced {

    private lateinit var datePicker: DatePicker
    private lateinit var timeBox: TimeBox

    init {
        icon = Image(R.image.ic_launcher)
        title = header
        headerText = header
        graphic = ImageView(R.image.ic_calendar)
        content = gridPane {
            gap(8)

            datePicker = forcedDatePicker(prefill.toLocalDate()) col 1 row 0
            button(graphic = ImageView(R.image.btn_arrow_left)) {
                setOnAction {
                    datePicker.value = datePicker.value.minusDays(1)
                }
            } col 0 row 0
            button(graphic = ImageView(R.image.btn_arrow_right)) {
                setOnAction {
                    datePicker.value = datePicker.value.plusDays(1)
                }
            } col 2 row 0

            timeBox = timeBox(prefill.toLocalTime()) col 1 row 1
            button(graphic = ImageView(R.image.btn_arrow_left)) {
                setOnAction {
                    timeBox.hourField.value--
                }
            } col 0 row 1
            button(graphic = ImageView(R.image.btn_arrow_right)) {
                setOnAction {
                    timeBox.hourField.value++
                }
            } col 2 row 1
            //slider(0, 24, 0) { valueProperty().bindBidirectional(timeBox.hourField.valueProperty) } col 0 row 2 colSpan 3
            //slider(0, 60, 0) { valueProperty().bindBidirectional(timeBox.minuteField.valueProperty) } col 0 row 3 colSpan 3
        }
        runLater { datePicker.requestFocus() }
        buttons(OK, CANCEL)
        setResultConverter {
            if (it != OK) null
            else DateTime(datePicker.value.year, datePicker.value.monthValue, datePicker.value.dayOfMonth, timeBox.time.hourOfDay, timeBox.time.minuteOfHour)
        }
    }
}