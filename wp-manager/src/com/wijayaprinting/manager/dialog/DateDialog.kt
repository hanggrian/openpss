package com.wijayaprinting.manager.dialog

import com.wijayaprinting.manager.R
import com.wijayaprinting.manager.utils.asJava
import javafx.scene.control.ButtonType.CANCEL
import javafx.scene.control.ButtonType.OK
import javafx.scene.control.DatePicker
import javafx.scene.control.Dialog
import javafx.scene.image.ImageView
import kotfx.*
import org.joda.time.LocalDate
import org.joda.time.LocalDate.now

class DateDialog @JvmOverloads constructor(
        title: String,
        headerText: String,
        prefill: LocalDate? = null
) : Dialog<LocalDate>() {

    init {
        this.title = title
        this.headerText = headerText
        this.graphic = ImageView(R.png.ic_calendar)

        lateinit var datePicker: DatePicker
        content = anchorPane {
            datePicker = datePicker {
                value = (prefill ?: now()).asJava()
                prefill?.let { value = it.asJava() }
                isEditable = false // force pick from popup
                maxWidth = 128.0
                runFX { requestFocus() }
            } anchor 0
        }
        buttons(OK, CANCEL)

        setResultConverter {
            if (it != OK) null
            else LocalDate(datePicker.value.year, datePicker.value.monthValue, datePicker.value.dayOfMonth)
        }
    }
}