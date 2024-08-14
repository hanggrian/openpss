package com.hanggrian.openpss.ui.wage

import com.hanggrian.openpss.Context
import com.hanggrian.openpss.R
import com.hanggrian.openpss.control.TimeBox
import com.hanggrian.openpss.popup.popover.ResultablePopover
import javafx.scene.Node
import ktfx.bindings.booleanBindingOf
import ktfx.layouts.gridPane
import ktfx.layouts.label
import org.joda.time.LocalTime

class AddRecessPopover(context: Context) :
    ResultablePopover<Pair<LocalTime, LocalTime>>(context, R.string_add_reccess) {
    private var startBox: TimeBox
    private var endBox: TimeBox

    init {
        gridPane {
            hgap = getDouble(R.dimen_padding_medium)
            vgap = getDouble(R.dimen_padding_medium)
            label(getString(R.string_start)).grid(0, 0)
            startBox = addChild(TimeBox()).grid(0, 1)
            label(getString(R.string_end)).grid(1, 0)
            endBox = addChild(TimeBox()).grid(1, 1)
        }
        defaultButton.disableProperty().bind(
            booleanBindingOf(startBox.valueProperty, endBox.valueProperty) {
                startBox.value!! >= endBox.value!!
            },
        )
    }

    override val focusedNode: Node get() = startBox

    override val nullableResult: Pair<LocalTime, LocalTime>
        get() = startBox.value!! to endBox.value!!
}
