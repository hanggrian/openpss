package com.hendraanggrian.openpss.ui.wage

import com.hendraanggrian.openpss.R
import com.hendraanggrian.openpss.content.Context
import com.hendraanggrian.openpss.control.TimeBox
import com.hendraanggrian.openpss.popup.popover.ResultablePopover
import javafx.scene.Node
import ktfx.bindings.booleanBindingOf
import ktfx.layouts.gridPane
import ktfx.layouts.label
import org.joda.time.LocalTime

class AddRecessPopover(
    context: Context
) : ResultablePopover<Pair<LocalTime, LocalTime>>(context, R.string.add_reccess) {
    private var startBox: TimeBox
    private var endBox: TimeBox

    override val focusedNode: Node? get() = startBox

    init {
        gridPane {
            hgap = getDouble(R.dimen.padding_medium)
            vgap = getDouble(R.dimen.padding_medium)
            label(getString(R.string.start)).grid(0, 0)
            startBox = addChild(TimeBox()).grid(0, 1)
            label(getString(R.string.end)).grid(1, 0)
            endBox = addChild(TimeBox()).grid(1, 1)
        }
        defaultButton.disableProperty().bind(
            booleanBindingOf(startBox.valueProperty(), endBox.valueProperty()) {
                startBox.value!! >= endBox.value!!
            }
        )
    }

    override val nullableResult: Pair<LocalTime, LocalTime>? get() = startBox.value!! to endBox.value!!
}
