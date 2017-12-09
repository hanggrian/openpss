package com.wijayaprinting.manager.dialog

import com.wijayaprinting.manager.scene.layout.TimeBox
import com.wijayaprinting.manager.scene.utils.setGaps
import com.wijayaprinting.manager.utils.asJava
import javafx.application.Platform
import javafx.scene.Node
import javafx.scene.control.ButtonType.CANCEL
import javafx.scene.control.ButtonType.OK
import javafx.scene.control.DatePicker
import javafx.scene.control.Dialog
import javafx.scene.control.Slider
import javafx.scene.layout.GridPane
import kotfx.bindBidirectional
import org.joda.time.DateTime
import org.joda.time.DateTime.now

class DateTimeDialog @JvmOverloads constructor(
        title: String,
        graphic: Node,
        headerText: String,
        prefill: DateTime? = null
) : Dialog<DateTime>() {

    private val datePicker = DatePicker().apply {
        value = (prefill ?: now()).toLocalDate().asJava()
        prefill?.let { value = it.toLocalDate().asJava() }
        isEditable = false // force pick from popup
        maxWidth = 128.0
    }
    private val timeBox = TimeBox().apply {
        prefill?.let { value = it.toLocalTime() }
    }
    private val hourSlider = Slider(0.0, 24.0, 0.0).apply { valueProperty() bindBidirectional timeBox.hourField.valueProperty }
    private val minuteSlider = Slider(0.0, 60.0, 0.0).apply { valueProperty() bindBidirectional timeBox.minuteField.valueProperty }

    init {
        this.title = title
        this.graphic = graphic
        this.headerText = headerText
        dialogPane.content = GridPane().apply {
            setGaps(8.0)
            add(datePicker, 0, 0)
            add(timeBox, 1, 0)
            add(hourSlider, 0, 1, 2, 1)
            add(minuteSlider, 0, 2, 2, 1)
        }
        dialogPane.buttonTypes.addAll(OK, CANCEL)
        Platform.runLater { datePicker.requestFocus() }
        setResultConverter {
            if (it != OK) null
            else DateTime(datePicker.value.year, datePicker.value.monthValue, datePicker.value.dayOfMonth, timeBox.value.hourOfDay, timeBox.value.minuteOfHour)
        }
    }
}