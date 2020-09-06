package com.hendraanggrian.openpss.ui.wage

import com.hendraanggrian.openpss.R
import com.hendraanggrian.openpss.content.Context
import com.hendraanggrian.openpss.db.schemas.Recesses
import com.hendraanggrian.openpss.db.transaction
import com.hendraanggrian.openpss.popup.popover.Popover
import com.jfoenix.controls.JFXButton
import javafx.scene.Node
import javafx.scene.control.ComboBox
import javafx.scene.control.Separator
import ktfx.bindings.or
import ktfx.collections.mutableObservableListOf
import ktfx.collections.toObservableList
import ktfx.coroutines.onAction
import ktfx.jfoenix.layouts.jfxComboBox
import ktfx.jfoenix.layouts.styledJFXButton
import ktfx.layouts.gridPane
import ktfx.layouts.label

class DisableRecessPopover(
    context: Context,
    private val attendeePanes: List<AttendeePane>
) : Popover(context, R.string.disable_recess) {

    private var recessChoice: ComboBox<*>
    private var roleChoice: ComboBox<*>

    override val focusedNode: Node? get() = recessChoice

    init {
        gridPane {
            hgap = getDouble(R.dimen.padding_medium)
            vgap = getDouble(R.dimen.padding_medium)
            label(getString(R.string.recess)).grid(0, 0)
            recessChoice = jfxComboBox(
                mutableObservableListOf(
                    getString(R.string.all),
                    Separator(),
                    *transaction { Recesses().toObservableList().toTypedArray() }
                )
            ) { selectionModel.selectFirst() }.grid(0, 1)
            label(getString(R.string.employee)).grid(1, 0)
            roleChoice = jfxComboBox(
                mutableObservableListOf(
                    *attendees.asSequence().filter { it.role != null }.map { it.role!! }.distinct().toList()
                        .toTypedArray(),
                    Separator(),
                    *attendees.toTypedArray()
                )
            ).grid(1, 1)
        }
        buttonManager.run {
            styledJFXButton(getString(R.string.apply), null, R.style.raised) {
                isDefaultButton = true
                buttonType = JFXButton.ButtonType.RAISED
                disableProperty().bind(recessChoice.valueProperty().isNull or roleChoice.valueProperty().isNull)
                onAction {
                    attendeePanes
                        .asSequence()
                        .filter { pane ->
                            when (roleChoice.value) {
                                is String -> pane.attendee.role == roleChoice.value
                                else -> pane.attendee == roleChoice.value as Attendee
                            }
                        }.map { pane -> pane.recessChecks }
                        .toList()
                        .forEach { pane ->
                            (
                                when {
                                    recessChoice.value is String -> pane
                                    else -> pane.filter { _pane -> _pane.text == recessChoice.value.toString() }
                                }
                                ).forEach { _pane ->
                                _pane.isSelected = false
                            }
                        }
                }
            }
        }
    }

    private inline val attendees: List<Attendee> get() = attendeePanes.map { it.attendee }
}
