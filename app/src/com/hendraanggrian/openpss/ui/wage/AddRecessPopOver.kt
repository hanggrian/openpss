package com.hendraanggrian.openpss.ui.wage

import com.hendraanggrian.openpss.R
import com.hendraanggrian.openpss.control.DefaultPopOver
import com.hendraanggrian.openpss.layout.TimeBox
import com.hendraanggrian.openpss.layout.timeBox
import com.hendraanggrian.openpss.localization.Resourced
import ktfx.beans.binding.booleanBindingOf
import ktfx.layouts.gridPane
import ktfx.layouts.label
import ktfx.scene.layout.gap
import org.joda.time.LocalTime

class AddRecessPopOver(
    resourced: Resourced
) : DefaultPopOver<Pair<LocalTime, LocalTime>>(resourced, R.string.add_reccess) {

    private lateinit var startBox: TimeBox
    private lateinit var endBox: TimeBox

    init {
        gridPane {
            gap = 8.0
            label(getString(R.string.start)) col 0 row 0
            startBox = timeBox() col 1 row 0
            label(getString(R.string.end)) col 0 row 1
            endBox = timeBox() col 1 row 1
        }
        defaultButton.disableProperty().bind(booleanBindingOf(startBox.valueProperty(), endBox.valueProperty()) {
            startBox.value >= endBox.value
        })
    }

    override fun getResult(): Pair<LocalTime, LocalTime> = startBox.value to endBox.value
}