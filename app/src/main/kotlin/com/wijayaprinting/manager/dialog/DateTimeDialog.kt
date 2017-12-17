package com.wijayaprinting.manager.dialog

import com.wijayaprinting.manager.scene.layout.TimeBox
import com.wijayaprinting.manager.scene.layout.timeBox
import com.wijayaprinting.manager.scene.utils.setGaps
import com.wijayaprinting.manager.utils.asJava
import javafx.scene.Node
import javafx.scene.control.ButtonType.CANCEL
import javafx.scene.control.ButtonType.OK
import javafx.scene.control.DatePicker
import javafx.scene.control.Dialog
import kotfx.controls.datePicker
import kotfx.controls.slider
import kotfx.dialogs.addButtons
import kotfx.dialogs.content
import kotfx.layouts.gridPaneOf
import kotfx.properties.bindBidirectional
import kotfx.runFX
import org.joda.time.DateTime
import org.joda.time.DateTime.now

class DateTimeDialog @JvmOverloads constructor(
        title: String,
        graphic: Node,
        headerText: String,
        prefill: DateTime? = null
) : Dialog<DateTime>() {

    init {
        this.title = title
        this.graphic = graphic
        this.headerText = headerText

        lateinit var datePicker: DatePicker
        lateinit var timeBox: TimeBox
        content = gridPaneOf {
            setGaps(8.0)
            datePicker = datePicker {
                value = (prefill ?: now()).toLocalDate().asJava()
                prefill?.let { value = it.toLocalDate().asJava() }
                isEditable = false // force pick from popup
                maxWidth = 128.0
            } col 0 row 0
            timeBox = timeBox { prefill?.let { value = it.toLocalTime() } } col 1 row 0
            slider(0, 24, 0) { valueProperty() bindBidirectional timeBox.hourField.valueProperty } col 0 row 1 colSpan 2
            slider(0, 60, 0) { valueProperty() bindBidirectional timeBox.minuteField.valueProperty } col 0 row 2 colSpan 2
        }
        addButtons(OK, CANCEL)

        runFX { datePicker.requestFocus() }
        setResultConverter {
            if (it != OK) null
            else DateTime(datePicker.value.year, datePicker.value.monthValue, datePicker.value.dayOfMonth, timeBox.value.hourOfDay, timeBox.value.minuteOfHour)
        }
    }
}