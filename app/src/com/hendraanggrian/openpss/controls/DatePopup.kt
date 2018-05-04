package com.hendraanggrian.openpss.controls

import com.hendraanggrian.openpss.layouts.DateBox
import com.hendraanggrian.openpss.layouts.dateBox
import com.hendraanggrian.openpss.resources.Resourced
import javafx.scene.Node
import javafx.scene.control.Button
import ktfx.layouts.button
import org.joda.time.LocalDate

class DatePopup(
    resourced: Resourced,
    titleId: String,
    prefill: LocalDate = LocalDate.now()
) : Popup<LocalDate>(resourced, titleId) {

    private val dateBox: DateBox get() = content as DateBox

    override val content: Node = dateBox(prefill)

    override val buttons: List<Button> = listOf(button("OK") {
        isDefaultButton = true
    })

    override fun getResult(): LocalDate = dateBox.valueProperty.value
}