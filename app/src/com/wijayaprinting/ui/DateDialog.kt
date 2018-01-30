package com.wijayaprinting.ui

import com.wijayaprinting.R
import com.wijayaprinting.ui.scene.control.forcedDatePicker
import javafx.scene.control.ButtonType.CANCEL
import javafx.scene.control.ButtonType.OK
import javafx.scene.control.DatePicker
import javafx.scene.control.Dialog
import javafx.scene.image.Image
import javafx.scene.image.ImageView
import kotfx.*
import org.joda.time.LocalDate
import org.joda.time.LocalDate.now

class DateDialog @JvmOverloads constructor(
        resourced: Resourced,
        header: String,
        prefill: LocalDate = now()
) : Dialog<LocalDate>(), Resourced by resourced {

    private lateinit var datePicker: DatePicker

    init {
        icon = Image(R.image.ic_launcher)
        title = header
        headerText = header
        graphic = ImageView(R.image.ic_calendar)
        content = gridPane {
            gap(8)
            datePicker = forcedDatePicker(prefill) col 1 row 0
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
        }
        runLater { datePicker.requestFocus() }
        buttons(OK, CANCEL)
        setResultConverter {
            if (it != OK) null
            else LocalDate(datePicker.value.year, datePicker.value.monthValue, datePicker.value.dayOfMonth)
        }
    }
}