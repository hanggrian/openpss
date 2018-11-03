package com.hendraanggrian.openpss.ui.wage

import com.hendraanggrian.openpss.Context
import com.hendraanggrian.openpss.R
import com.hendraanggrian.openpss.control.TimeBox
import com.hendraanggrian.openpss.control.timeBox
import com.hendraanggrian.openpss.popup.popover.ResultablePopover
import ktfx.beans.binding.buildBooleanBinding
import ktfx.layouts.gridPane
import ktfx.layouts.label
import ktfx.scene.layout.gap
import org.joda.time.LocalTime

class AddRecessPopover(
    context: Context
) : ResultablePopover<Pair<LocalTime, LocalTime>>(context, R.string.add_reccess) {

    private lateinit var startBox: TimeBox
    private lateinit var endBox: TimeBox

    init {
        gridPane {
            gap = R.dimen.padding_medium.toDouble()
            label(getString(R.string.start)) col 0 row 0
            startBox = timeBox() col 1 row 0
            label(getString(R.string.end)) col 0 row 1
            endBox = timeBox() col 1 row 1
        }
        defaultButton.disableProperty().bind(buildBooleanBinding(startBox.valueProperty(), endBox.valueProperty()) {
            startBox.value!! >= endBox.value!!
        })
    }

    override val nullableResult: Pair<LocalTime, LocalTime>? get() = startBox.value!! to endBox.value!!
}