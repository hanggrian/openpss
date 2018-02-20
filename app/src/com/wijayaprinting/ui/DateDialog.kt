package com.wijayaprinting.ui

import com.wijayaprinting.R
import com.wijayaprinting.scene.layout.DateBox
import com.wijayaprinting.scene.layout.dateBox
import javafx.scene.control.ButtonType.CANCEL
import javafx.scene.control.ButtonType.OK
import javafx.scene.control.DatePicker
import javafx.scene.control.Dialog
import javafx.scene.image.Image
import javafx.scene.image.ImageView
import kotfx.coroutines.resultConverter
import kotfx.dialogs.addButtons
import kotfx.dialogs.content
import kotfx.dialogs.icon
import kotfx.runLater
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
        content = dateBox(prefill)
        runLater { datePicker.requestFocus() }
        addButtons(OK, CANCEL)
        resultConverter {
            if (it != OK) null
            else LocalDate(datePicker.value.year, datePicker.value.monthValue, datePicker.value.dayOfMonth)
        }
    }

    private inline val datePicker: DatePicker get() = (content as DateBox).picker
}