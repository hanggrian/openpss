package com.wijayaprinting.ui

import com.wijayaprinting.R
import com.wijayaprinting.scene.layout.DateBox
import com.wijayaprinting.scene.layout.dateBox
import javafx.scene.control.ButtonType.OK
import javafx.scene.control.DatePicker
import javafx.scene.control.Dialog
import javafx.scene.image.Image
import javafx.scene.image.ImageView
import kotfx.application.later
import kotfx.scene.control.cancelButton
import kotfx.scene.control.icon
import kotfx.scene.control.okButton
import org.joda.time.LocalDate
import org.joda.time.LocalDate.now

class DateDialog(
    resourced: Resourced,
    header: String,
    prefill: LocalDate = now()
) : Dialog<LocalDate>(), Resourced by resourced {

    init {
        icon = Image(R.image.ic_launcher)
        title = header
        headerText = header
        graphic = ImageView(R.image.ic_calendar)
        dialogPane.content = dateBox(prefill)
        later { datePicker.requestFocus() }
        cancelButton()
        okButton()
        setResultConverter {
            if (it != OK) null
            else LocalDate(datePicker.value.year, datePicker.value.monthValue, datePicker.value.dayOfMonth)
        }
    }

    private inline val datePicker: DatePicker get() = (dialogPane.content as DateBox).picker
}