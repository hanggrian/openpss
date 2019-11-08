package com.hendraanggrian.openpss.ui.wage

import com.hendraanggrian.openpss.FxComponent
import com.hendraanggrian.openpss.R
import com.hendraanggrian.openpss.R2
import com.hendraanggrian.openpss.control.TimeBox
import com.hendraanggrian.openpss.ui.ResultablePopOver
import javafx.scene.Node
import ktfx.bindings.buildBooleanBinding
import ktfx.layouts.addNode
import ktfx.layouts.gap
import ktfx.layouts.gridPane
import ktfx.layouts.label
import org.joda.time.LocalTime

class AddRecessPopOver(
    component: FxComponent
) : ResultablePopOver<Pair<LocalTime, LocalTime>>(component, R2.string.add_reccess) {

    private val startBox: TimeBox
    private val endBox: TimeBox

    override val focusedNode: Node? get() = startBox

    init {
        gridPane {
            gap = getDouble(R.value.padding_medium)
            label(getString(R2.string.start)) {
                gridAt(0, 0)
            }
            startBox = addNode(TimeBox()) {
                gridAt(0, 1)
            }
            label(getString(R2.string.end)) {
                gridAt(1, 0)
            }
            endBox = addNode(TimeBox()) {
                gridAt(1, 1)
            }
        }
        defaultButton.disableProperty()
            .bind(buildBooleanBinding(startBox.valueProperty(), endBox.valueProperty()) {
                startBox.value!! >= endBox.value!!
            })
    }

    override val nullableResult: Pair<LocalTime, LocalTime>? get() = startBox.value!! to endBox.value!!
}
