package com.hendraanggrian.openpss.ui.wage

import com.hendraanggrian.openpss.R
import com.hendraanggrian.openpss.control.popover.ResultablePopover
import com.hendraanggrian.openpss.layout.TimeBox
import com.hendraanggrian.openpss.layout.timeBox
import com.hendraanggrian.openpss.i18n.Resourced
import javafxx.beans.binding.booleanBindingOf
import javafxx.layouts.gridPane
import javafxx.layouts.label
import javafxx.scene.layout.gap
import org.joda.time.LocalTime

class AddRecessPopover(
    resourced: Resourced
) : ResultablePopover<Pair<LocalTime, LocalTime>>(resourced, R.string.add_reccess) {

    private lateinit var startBox: TimeBox
    private lateinit var endBox: TimeBox

    init {
        gridPane {
            gap = R.dimen.padding_small.toDouble()
            label(getString(R.string.start)) col 0 row 0
            startBox = timeBox() col 1 row 0
            label(getString(R.string.end)) col 0 row 1
            endBox = timeBox() col 1 row 1
        }
        defaultButton.disableProperty().bind(booleanBindingOf(startBox.valueProperty(), endBox.valueProperty()) {
            startBox.value >= endBox.value
        })
    }

    override val optionalResult: Pair<LocalTime, LocalTime>? get() = startBox.value to endBox.value
}