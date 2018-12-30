package com.hendraanggrian.openpss.ui.wage

import com.hendraanggrian.openpss.R
import com.hendraanggrian.openpss.control.TimeBox
import com.hendraanggrian.openpss.ui.FxComponent
import com.hendraanggrian.openpss.ui.ResultablePopover
import javafx.scene.Node
import ktfx.bindings.buildBooleanBinding
import ktfx.controls.gap
import ktfx.layouts.gridPane
import ktfx.layouts.label
import org.joda.time.LocalTime

class AddRecessPopover(
    component: FxComponent
) : ResultablePopover<Pair<LocalTime, LocalTime>>(component, R.string.add_reccess) {

    private lateinit var startBox: TimeBox
    private lateinit var endBox: TimeBox

    override val focusedNode: Node? get() = startBox

    init {
        gridPane {
            gap = getDouble(R.value.padding_medium)
            label(getString(R.string.start)) col 0 row 0
            startBox = TimeBox()() col 1 row 0
            label(getString(R.string.end)) col 0 row 1
            endBox = TimeBox()() col 1 row 1
        }
        defaultButton.disableProperty()
            .bind(buildBooleanBinding(startBox.valueProperty(), endBox.valueProperty()) {
                startBox.value!! >= endBox.value!!
            })
    }

    override val nullableResult: Pair<LocalTime, LocalTime>? get() = startBox.value!! to endBox.value!!
}