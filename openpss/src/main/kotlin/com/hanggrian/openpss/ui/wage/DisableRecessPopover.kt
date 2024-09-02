package com.hanggrian.openpss.ui.wage

import com.hanggrian.openpss.Context
import com.hanggrian.openpss.R
import com.hanggrian.openpss.db.schemas.Recesses
import com.hanggrian.openpss.db.transaction
import com.hanggrian.openpss.popup.popover.Popover
import com.jfoenix.controls.JFXButton
import javafx.scene.Node
import javafx.scene.control.ComboBox
import javafx.scene.control.Separator
import ktfx.bindings.or
import ktfx.collections.mutableObservableListOf
import ktfx.collections.toObservableList
import ktfx.coroutines.onAction
import ktfx.jfoenix.layouts.jfxComboBox
import ktfx.jfoenix.layouts.styledJfxButton
import ktfx.layouts.gridPane
import ktfx.layouts.label

class DisableRecessPopover(context: Context, private val attendeePanes: List<AttendeePane>) :
    Popover(context, R.string_disable_recess) {
    private val recessChoice: ComboBox<*>
    private val roleChoice: ComboBox<*>

    init {
        gridPane {
            hgap = getDouble(R.dimen_padding_medium)
            vgap = getDouble(R.dimen_padding_medium)
            label(getString(R.string_recess)).grid(0, 0)
            recessChoice =
                jfxComboBox(
                    mutableObservableListOf(
                        getString(R.string_all),
                        Separator(),
                        *transaction { Recesses().toObservableList().toTypedArray() },
                    ),
                ) { selectionModel.selectFirst() }
                    .grid(0, 1)
            label(getString(R.string_employee)).grid(1, 0)
            roleChoice =
                jfxComboBox(
                    mutableObservableListOf(
                        *attendees
                            .asSequence()
                            .filter { it.role != null }
                            .map { it.role!! }
                            .distinct()
                            .toList()
                            .toTypedArray(),
                        Separator(),
                        *attendees.toTypedArray(),
                    ),
                ).grid(1, 1)
        }
        buttonManager.run {
            styledJfxButton(getString(R.string_apply), null, R.style_raised) {
                isDefaultButton = true
                buttonType = JFXButton.ButtonType.RAISED
                disableProperty()
                    .bind(recessChoice.valueProperty().isNull or roleChoice.valueProperty().isNull)
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
                            when {
                                recessChoice.value is String -> pane
                                else -> pane.filter { p -> p.text == recessChoice.value.toString() }
                            }.forEach { p -> p.isSelected = false }
                        }
                }
            }
        }
    }

    override val focusedNode: Node get() = recessChoice

    private inline val attendees: List<Attendee> get() = attendeePanes.map { it.attendee }
}
