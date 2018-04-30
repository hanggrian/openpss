package com.hendraanggrian.openpss.controls

import com.hendraanggrian.openpss.R
import com.hendraanggrian.openpss.layouts.dateBox
import com.hendraanggrian.openpss.resources.Resourced
import com.hendraanggrian.openpss.util.getStyle
import javafx.scene.control.ButtonType.OK
import javafx.scene.control.Dialog
import javafx.scene.image.ImageView
import ktfx.application.later
import ktfx.scene.control.cancelButton
import ktfx.scene.control.graphicIcon
import ktfx.scene.control.headerTitle
import ktfx.scene.control.okButton
import org.joda.time.LocalDate
import org.joda.time.LocalDate.now

class DateDialog(
    resourced: Resourced,
    headerId: String,
    prefill: LocalDate = now()
) : Dialog<LocalDate>(), Resourced by resourced {

    private val dateBox = dateBox(prefill)

    init {
        headerTitle = getString(headerId)
        graphicIcon = ImageView(R.image.header_date)
        dialogPane.run {
            stylesheets += getStyle(R.style.openpss)
            content = dateBox
        }
        dialogPane.content = dateBox
        later { dateBox.requestFocus() }
        cancelButton()
        okButton()
        setResultConverter { if (it != OK) null else dateBox.valueProperty.value }
    }
}