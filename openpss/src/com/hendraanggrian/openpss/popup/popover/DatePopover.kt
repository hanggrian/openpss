package com.hendraanggrian.openpss.popup.popover

import com.hendraanggrian.openpss.control.DateBox
import com.hendraanggrian.openpss.control.dateBox
import com.hendraanggrian.openpss.i18n.Resourced
import ktfx.NodeManager
import org.joda.time.LocalDate

class DatePopover(
    resourced: Resourced,
    titleId: String,
    private val prefill: LocalDate = LocalDate.now()
) : ResultablePopover<LocalDate>(resourced, titleId) {

    private lateinit var dateBox: DateBox

    override fun onCreate(manager: NodeManager) {
        super.onCreate(manager)
        manager.run {
            dateBox = dateBox(prefill)
        }
    }

    override val nullableResult: LocalDate? get() = dateBox.valueProperty().value
}