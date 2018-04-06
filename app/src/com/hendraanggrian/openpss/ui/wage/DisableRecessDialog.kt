package com.hendraanggrian.openpss.ui.wage

import com.hendraanggrian.openpss.R
import com.hendraanggrian.openpss.db.schema.Recesses
import com.hendraanggrian.openpss.db.transaction
import com.hendraanggrian.openpss.ui.Resourced
import javafx.scene.control.ButtonType.OK
import javafx.scene.control.ChoiceBox
import javafx.scene.control.Dialog
import javafx.scene.control.Separator
import javafx.scene.image.ImageView
import ktfx.beans.binding.or
import ktfx.collections.mutableObservableListOf
import ktfx.collections.toObservableList
import ktfx.layouts.choiceBox
import ktfx.layouts.gridPane
import ktfx.layouts.label
import ktfx.scene.control.cancelButton
import ktfx.scene.control.graphicIcon
import ktfx.scene.control.headerTitle
import ktfx.scene.control.okButton
import ktfx.scene.layout.gap

class DisableRecessDialog(
    resourced: Resourced,
    attendees: List<Attendee>
) : Dialog<Pair<Any, Any>>(), Resourced by resourced {

    private lateinit var recessChoice: ChoiceBox<*>
    private lateinit var roleChoice: ChoiceBox<*>

    init {
        headerTitle = getString(R.string.disable_recess)
        graphicIcon = ImageView(R.image.ic_time)
        dialogPane.content = gridPane {
            gap = 8.0
            label(getString(R.string.recess)) col 0 row 0
            transaction {
                recessChoice = choiceBox(mutableObservableListOf(getString(R.string.all),
                    Separator(),
                    *Recesses.find().toObservableList().toTypedArray())
                ) { selectionModel.selectFirst() } col 1 row 0
            }
            label(getString(R.string.employee)) col 0 row 1
            roleChoice = choiceBox(mutableObservableListOf(
                *attendees.filter { it.role != null }.map { it.role!! }.distinct().toTypedArray(),
                Separator(),
                *attendees.toTypedArray())) col 1 row 1
        }
        cancelButton()
        okButton { disableProperty().bind(recessChoice.valueProperty().isNull or roleChoice.valueProperty().isNull) }
        setResultConverter { if (it == OK) recessChoice.value to roleChoice.value else null }
    }
}