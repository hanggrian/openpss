package com.hendraanggrian.openpss.ui

import com.hendraanggrian.openpss.R
import com.hendraanggrian.openpss.scene.layout.DateBox
import com.hendraanggrian.openpss.scene.layout.dateBox
import javafx.scene.control.ButtonType.OK
import javafx.scene.control.DatePicker
import javafx.scene.control.Dialog
import javafx.scene.image.ImageView
import kotfx.application.later
import kotfx.scene.control.cancelButton
import kotfx.scene.control.graphicIcon
import kotfx.scene.control.headerTitle
import kotfx.scene.control.okButton
import org.joda.time.LocalDate
import org.joda.time.LocalDate.now

class DateDialog(
    resourced: Resourced,
    title: String,
    prefill: LocalDate = now()
) : Dialog<LocalDate>(), Resourced by resourced {

    init {
        headerTitle = title
        graphicIcon = ImageView(R.image.ic_calendar)
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