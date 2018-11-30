package com.hendraanggrian.openpss.ui.wage

import com.hendraanggrian.openpss.R
import com.hendraanggrian.openpss.content.Context
import com.hendraanggrian.openpss.control.TimeBox
import com.hendraanggrian.openpss.popup.popover.ResultablePopover
import javafx.scene.Node
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

    override val focusedNode: Node? get() = startBox

    init {
        gridPane {
            gap = getDouble(R.dimen.padding_medium)
            label(getString(R.string.start)) col 0 row 0
            startBox = TimeBox()() col 1 row 0
            label(getString(R.string.end)) col 0 row 1
            endBox = TimeBox()() col 1 row 1
        }
        defaultButton.disableProperty().bind(buildBooleanBinding(startBox.valueProperty(), endBox.valueProperty()) {
            startBox.value!! >= endBox.value!!
        })
    }

    override val nullableResult: Pair<LocalTime, LocalTime>? get() = startBox.value!! to endBox.value!!
}