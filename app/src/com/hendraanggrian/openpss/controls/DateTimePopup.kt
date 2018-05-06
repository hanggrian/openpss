package com.hendraanggrian.openpss.controls

import com.hendraanggrian.openpss.layouts.DateBox
import com.hendraanggrian.openpss.layouts.TimeBox
import com.hendraanggrian.openpss.layouts.dateBox
import com.hendraanggrian.openpss.layouts.timeBox
import com.hendraanggrian.openpss.resources.Resourced
import com.hendraanggrian.openpss.ui.wage.record.Record
import javafx.scene.Node
import ktfx.coroutines.onAction
import ktfx.layouts.LayoutManager
import ktfx.layouts.button
import ktfx.layouts.gridPane
import ktfx.scene.layout.gap
import org.joda.time.DateTime

class DateTimePopup(
    resourced: Resourced,
    titleId: String,
    private val defaultButtonTextId: String,
    prefill: DateTime
) : Popup<DateTime>(resourced, titleId) {

    private lateinit var dateBox: DateBox
    private lateinit var timeBox: TimeBox

    override val content: Node = gridPane {
        gap = 8.0
        dateBox = dateBox(prefill.toLocalDate()) row 0 col 1
        button("-${Record.WORKING_HOURS}") {
            onAction { repeat(Record.WORKING_HOURS) { timeBox.previousButton.fire() } }
        } row 1 col 0
        timeBox = timeBox(prefill.toLocalTime()) {
            setOnOverlap { plus ->
                dateBox.picker.value = when {
                    plus -> dateBox.picker.value.plusDays(1)
                    else -> dateBox.picker.value.minusDays(1)
                }
            }
        } row 1 col 1
        button("+${Record.WORKING_HOURS}") {
            onAction { repeat(Record.WORKING_HOURS) { timeBox.nextButton.fire() } }
        } row 1 col 2
    }

    override fun LayoutManager<Node>.buttons() {
        defaultButton(defaultButtonTextId)
    }

    override fun getResult(): DateTime = dateBox.value.toDateTime(timeBox.value)
}