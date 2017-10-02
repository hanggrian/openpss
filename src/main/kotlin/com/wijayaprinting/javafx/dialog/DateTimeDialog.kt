package com.wijayaprinting.javafx.dialog

import com.wijayaprinting.javafx.R
import com.wijayaprinting.javafx.control.field.TimeField
import com.wijayaprinting.javafx.layout.GridPane
import com.wijayaprinting.javafx.utils.getString
import javafx.geometry.Pos
import javafx.scene.control.ButtonType
import javafx.scene.control.DatePicker
import javafx.scene.control.Dialog
import javafx.scene.control.Label
import javafx.scene.image.ImageView
import kotfx.bindings.not
import kotfx.bindings.or
import kotfx.runLater
import org.joda.time.DateTime

/**
 * @author Hendra Anggrian (hendraanggrian@gmail.com)
 */
class DateTimeDialog : Dialog<DateTime>() {

    val content = Content()

    init {
        title = getString(R.string.record)
        graphic = ImageView(R.png.ic_record)
        headerText = getString(R.string.record)

        dialogPane.content = content
        dialogPane.buttonTypes.addAll(ButtonType.OK, ButtonType.CANCEL)
        dialogPane.lookupButton(ButtonType.OK).disableProperty().bind(content.datePicker.valueProperty().isNull or not(content.timeField.validProperty))
        runLater { content.datePicker.requestFocus() }
        setResultConverter {
            when (it) {
                ButtonType.OK -> DateTime(content.datePicker.value.year, content.datePicker.value.monthValue, content.datePicker.value.dayOfMonth, content.timeField.value!!.hourOfDay, content.timeField.value!!.minuteOfHour)
                else -> null
            }
        }
    }

    inner class Content : GridPane(8.0) {
        val dateLabel = Label(getString(R.string.date))
        val datePicker: DatePicker = DatePicker().apply {
            isEditable = false // force pick from popup
            maxWidth = 128.0
            alignment = Pos.CENTER
        }
        val timeLabel = Label(getString(R.string.time))
        val timeField: TimeField = TimeField().apply {
            maxWidth = 64.0
            alignment = Pos.CENTER
        }

        init {
            add(dateLabel, 0, 0)
            add(datePicker, 1, 0)
            add(timeLabel, 0, 1)
            add(timeField, 1, 1)
        }
    }
}