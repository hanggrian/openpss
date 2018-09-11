package com.hendraanggrian.openpss.ui.wage

import com.hendraanggrian.openpss.R
import com.hendraanggrian.openpss.control.popover.Popover
import com.hendraanggrian.openpss.db.schemas.Recesses
import com.hendraanggrian.openpss.db.transaction
import com.hendraanggrian.openpss.i18n.Resourced
import javafx.scene.control.ChoiceBox
import javafx.scene.control.Separator
import javafxx.beans.value.or
import javafxx.collections.mutableObservableListOf
import javafxx.collections.toObservableList
import javafxx.coroutines.onAction
import javafxx.layouts.button
import javafxx.layouts.choiceBox
import javafxx.layouts.gridPane
import javafxx.layouts.label
import javafxx.scene.layout.gap

class DisableRecessPopover(
    resourced: Resourced,
    private val attendeePanes: List<AttendeePane>
) : Popover(resourced, R.string.disable_recess) {

    private lateinit var recessChoice: ChoiceBox<*>
    private lateinit var roleChoice: ChoiceBox<*>

    init {
        gridPane {
            gap = R.dimen.padding_small.toDouble()
            label(getString(R.string.recess)) col 0 row 0
            recessChoice = choiceBox(
                mutableObservableListOf(getString(R.string.all),
                    Separator(),
                    *transaction { Recesses().toObservableList().toTypedArray() })
            ) { selectionModel.selectFirst() } col 1 row 0
            label(getString(R.string.employee)) col 0 row 1
            roleChoice = choiceBox(mutableObservableListOf(
                *attendees.filter { it.role != null }.map { it.role!! }.distinct().toTypedArray(),
                Separator(),
                *attendees.toTypedArray()
            )
            ) col 1 row 1
        }
        buttonBar.run {
            button(getString(R.string.apply)) {
                disableProperty().bind(recessChoice.valueProperty().isNull or roleChoice.valueProperty().isNull)
                onAction {
                    attendeePanes
                        .filter { pane ->
                            when {
                                roleChoice.value is String -> pane.attendee.role == roleChoice.value
                                else -> pane.attendee == roleChoice.value as Attendee
                            }
                        }.map { pane -> pane.recessChecks }
                        .forEach { pane ->
                            (when {
                                recessChoice.value is String -> pane
                                else -> pane.filter { _pane -> _pane.text == recessChoice.value.toString() }
                            }).forEach { _pane ->
                                _pane.isSelected = false
                            }
                        }
                }
            }
        }
    }

    private inline val attendees: List<Attendee> get() = attendeePanes.map { it.attendee }
}