package com.hendraanggrian.openpss.controls

import com.hendraanggrian.openpss.layouts.DateBox
import com.hendraanggrian.openpss.layouts.dateBox
import com.hendraanggrian.openpss.resources.Resourced
import javafx.scene.Node
import ktfx.layouts.LayoutManager
import org.joda.time.LocalDate

class DatePopup(
    resourced: Resourced,
    titleId: String,
    prefill: LocalDate = LocalDate.now()
) : Popup<LocalDate>(resourced, titleId) {

    private val dateBox: DateBox get() = content as DateBox

    override val content: Node = dateBox(prefill)

    override fun LayoutManager<Node>.buttons() {
        defaultButton()
    }

    override fun getResult(): LocalDate = dateBox.valueProperty.value
}