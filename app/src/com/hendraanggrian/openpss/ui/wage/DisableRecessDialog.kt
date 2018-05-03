package com.hendraanggrian.openpss.ui.wage

import com.hendraanggrian.openpss.R
import com.hendraanggrian.openpss.db.schemas.Recesses
import com.hendraanggrian.openpss.db.transaction
import com.hendraanggrian.openpss.resources.Resourced
import com.hendraanggrian.openpss.util.getStyle
import com.hendraanggrian.openpss.util.onActionFilter
import javafx.scene.control.ChoiceBox
import javafx.scene.control.Dialog
import javafx.scene.control.Separator
import javafx.scene.image.ImageView
import ktfx.beans.value.or
import ktfx.collections.mutableObservableListOf
import ktfx.collections.toObservableList
import ktfx.layouts.choiceBox
import ktfx.layouts.gridPane
import ktfx.layouts.label
import ktfx.scene.control.applyButton
import ktfx.scene.control.closeButton
import ktfx.scene.control.graphicIcon
import ktfx.scene.control.headerTitle
import ktfx.scene.layout.gap

class DisableRecessDialog(
    resourced: Resourced,
    private val attendeePanes: List<AttendeePane>
) : Dialog<Nothing>(), Resourced by resourced {

    private lateinit var recessChoice: ChoiceBox<*>
    private lateinit var roleChoice: ChoiceBox<*>

    init {
        headerTitle = getString(R.string.disable_recess)
        graphicIcon = ImageView(R.image.header_time)
        dialogPane.run {
            stylesheets += getStyle(R.style.openpss)
            content = gridPane {
                gap = 8.0
                label(getString(R.string.recess)) col 0 row 0
                transaction {
                    recessChoice = choiceBox(mutableObservableListOf(getString(R.string.all),
                        Separator(),
                        *Recesses().toObservableList().toTypedArray())
                    ) { selectionModel.selectFirst() } col 1 row 0
                }
                label(getString(R.string.employee)) col 0 row 1
                roleChoice = choiceBox(mutableObservableListOf(
                    *attendees.filter { it.role != null }.map { it.role!! }.distinct().toTypedArray(),
                    Separator(),
                    *attendees.toTypedArray())) col 1 row 1
            }
        }
        closeButton()
        applyButton {
            disableProperty().bind(recessChoice.valueProperty().isNull or roleChoice.valueProperty().isNull)
            onActionFilter {
                attendeePanes.filter {
                    when {
                        roleChoice.value is String -> it.attendee.role == roleChoice.value
                        else -> it.attendee == roleChoice.value as Attendee
                    }
                }.map { it.recessChecks }.forEach {
                    (when {
                        recessChoice.value is String -> it
                        else -> it.filter { it.text == recessChoice.value.toString() }
                    }).forEach { it.isSelected = false }
                }
            }
        }
    }

    private inline val attendees: List<Attendee> get() = attendeePanes.map { it.attendee }
}