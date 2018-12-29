package com.hendraanggrian.openpss.ui.wage

import com.hendraanggrian.openpss.R
import com.hendraanggrian.openpss.content.FxComponent
import com.hendraanggrian.openpss.popup.popover.Popover
import com.jfoenix.controls.JFXButton
import javafx.scene.Node
import javafx.scene.control.ComboBox
import javafx.scene.control.Separator
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.javafx.JavaFx
import kotlinx.coroutines.launch
import ktfx.bindings.or
import ktfx.collections.mutableObservableListOf
import ktfx.controls.gap
import ktfx.coroutines.onAction
import ktfx.jfoenix.jfxButton
import ktfx.jfoenix.jfxComboBox
import ktfx.layouts.gridPane
import ktfx.layouts.label

class DisableRecessPopover(
    component: FxComponent,
    private val attendeePanes: List<AttendeePane>
) : Popover(component, R.string.disable_recess) {

    private lateinit var recessChoice: ComboBox<*>
    private lateinit var roleChoice: ComboBox<*>

    override val focusedNode: Node? get() = recessChoice

    init {
        gridPane {
            gap = getDouble(R.value.padding_medium)
            label(getString(R.string.recess)) col 0 row 0
            GlobalScope.launch(Dispatchers.JavaFx) {
                recessChoice = jfxComboBox(
                    mutableObservableListOf(
                        getString(R.string.all),
                        Separator(),
                        *api.getRecesses().toTypedArray()
                    )
                ) { selectionModel.selectFirst() } col 1 row 0
            }
            label(getString(R.string.employee)) col 0 row 1
            roleChoice = jfxComboBox(mutableObservableListOf(
                *attendees.asSequence().filter { it.role != null }.map { it.role!! }.distinct().toList().toTypedArray(),
                Separator(),
                *attendees.toTypedArray()
            )
            ) col 1 row 1
        }
        buttonInvokable.run {
            jfxButton(getString(R.string.apply)) {
                isDefaultButton = true
                buttonType = JFXButton.ButtonType.RAISED
                styleClass += R.style.raised
                disableProperty().bind(recessChoice.valueProperty().isNull or roleChoice.valueProperty().isNull)
                onAction {
                    attendeePanes
                        .asSequence()
                        .filter { pane ->
                            when {
                                roleChoice.value is String -> pane.attendee.role == roleChoice.value
                                else -> pane.attendee == roleChoice.value as Attendee
                            }
                        }.map { pane -> pane.recessChecks }
                        .toList()
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