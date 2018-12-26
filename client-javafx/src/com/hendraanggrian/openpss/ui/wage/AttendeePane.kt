package com.hendraanggrian.openpss.ui.wage

import com.hendraanggrian.openpss.util.PATTERN_DATETIME_EXTENDED
import com.hendraanggrian.openpss.R
import com.hendraanggrian.openpss.content.FxComponent
import com.hendraanggrian.openpss.control.IntField
import com.hendraanggrian.openpss.popup.popover.DateTimePopover
import com.hendraanggrian.openpss.util.round
import com.hendraanggrian.openpss.util.trimMinutes
import javafx.geometry.Pos.CENTER
import javafx.scene.Node
import javafx.scene.control.CheckBox
import javafx.scene.control.ContentDisplay
import javafx.scene.control.ListView
import javafx.scene.control.MenuItem
import javafx.scene.image.Image
import javafx.scene.image.ImageView
import javafx.scene.input.MouseEvent.MOUSE_CLICKED
import javafx.scene.layout.Priority.ALWAYS
import javafx.scene.layout.StackPane
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.javafx.JavaFx
import kotlinx.coroutines.launch
import ktfx.bindings.buildBinding
import ktfx.collections.sort
import ktfx.controls.find
import ktfx.controls.gap
import ktfx.controls.isSelected
import ktfx.controls.paddingAll
import ktfx.coroutines.eventFilter
import ktfx.coroutines.listener
import ktfx.coroutines.onAction
import ktfx.coroutines.onKeyPressed
import ktfx.coroutines.onMouseClicked
import ktfx.inputs.isDelete
import ktfx.inputs.isDoubleClick
import ktfx.jfoenix.jfxCheckBox
import ktfx.layouts._TitledPane
import ktfx.layouts.contextMenu
import ktfx.layouts.gridPane
import ktfx.layouts.label
import ktfx.layouts.listView
import ktfx.layouts.separatorMenuItem
import ktfx.layouts.text
import ktfx.layouts.vbox
import ktfx.listeners.cellFactory
import ktfx.text.updateFont
import org.joda.time.DateTime
import org.joda.time.DateTime.now
import kotlin.math.absoluteValue

class AttendeePane(
    component: FxComponent,
    val attendee: Attendee
) : _TitledPane(attendee.toString()), FxComponent by component {

    val recessChecks: MutableList<CheckBox> = mutableListOf()
    lateinit var deleteMenu: MenuItem
    lateinit var deleteOthersMenu: MenuItem
    lateinit var deleteToTheRightMenu: MenuItem
    lateinit var attendanceList: ListView<DateTime>

    init {
        isCollapsible = false
        vbox {
            isFillWidth = true
            gridPane {
                styleClass += R.style.white_background
                gap = getDouble(R.dimen.padding_small)
                paddingAll = getDouble(R.dimen.padding_medium)
                attendee.role?.let { role ->
                    label(getString(R.string.role)) col 0 row 0 marginRight 4
                    label(role) col 1 row 0 colSpans 2
                }
                label(getString(R.string.income)) col 0 row 1 marginRight 4
                IntField().apply {
                    prefWidth = 80.0
                    promptText = getString(R.string.income)
                    valueProperty().bindBidirectional(attendee.dailyProperty)
                }() col 1 row 1
                label("@${getString(R.string.day)}") {
                    updateFont(10)
                } col 2 row 1
                label(getString(R.string.overtime)) col 0 row 2 marginRight 4
                IntField().apply {
                    prefWidth = 80.0
                    promptText = getString(R.string.overtime)
                    valueProperty().bindBidirectional(attendee.hourlyOvertimeProperty)
                }() col 1 row 2
                label("@${getString(R.string.hour)}") {
                    updateFont(10)
                } col 2 row 2
                label(getString(R.string.recess)) col 0 row 3 marginRight 4
                vbox {
                    GlobalScope.launch(Dispatchers.JavaFx) {
                        api.getRecesses().forEach { recess ->
                            recessChecks += jfxCheckBox(recess.toString()) {
                                selectedProperty().listener { _, _, selected ->
                                    if (selected) attendee.recesses += recess else attendee.recesses -= recess
                                    attendanceList.forceRefresh()
                                }
                                isSelected = true
                            } marginTop if (children.size > 1) 4 else 0
                        }
                    }
                } col 1 row 3 colSpans 2
            }
            attendanceList = listView(attendee.attendances) {
                styleClass += R.style.list_view_no_scrollbar_horizontal
                prefWidth = 150.0
                maxHeight = 360.0 // just enough for 7 days attendance
                cellFactory {
                    onUpdate { dateTime, empty ->
                        text = null
                        graphic = null
                        if (dateTime != null && !empty) graphic = ktfx.layouts.hbox {
                            alignment = CENTER
                            val itemLabel = label(dateTime.toString(PATTERN_DATETIME_EXTENDED)) {
                                maxWidth = Double.MAX_VALUE
                            } hpriority ALWAYS
                            if (index % 2 == 0) {
                                listView.items.getOrNull(index + 1).let { nextItem ->
                                    when (nextItem) {
                                        null -> itemLabel.textFill = getColor(R.color.red)
                                        else -> {
                                            val interval = IntervalWrapper.of(dateTime, nextItem)
                                            var minutes = interval.minutes
                                            attendee.recesses
                                                .map { it.getInterval(dateTime) }
                                                .forEach {
                                                    minutes -= interval.overlap(it)?.toDuration()?.toStandardMinutes()
                                                        ?.minutes?.absoluteValue ?: 0
                                                }
                                            val hours = (minutes / 60.0).round()
                                            text(hours.toString()) {
                                                updateFont(10)
                                                if (hours > 12) {
                                                    style = "-fx-fill: #F44336;"
                                                }
                                            } marginLeft getDouble(R.dimen.padding_medium)
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
                onKeyPressed {
                    if (it.code.isDelete() && attendanceList.selectionModel.isSelected()) {
                        items.remove(attendanceList.selectionModel.selectedItem)
                    }
                }
                onMouseClicked {
                    if (it.isDoubleClick() && attendanceList.selectionModel.isSelected()) {
                        editAttendance()
                    }
                }
            }
        }
        contextMenu {
            getString(R.string.add)(ImageView(R.image.menu_add)) {
                onAction { addAttendance() }
            }
            separatorMenuItem()
            getString(R.string.copy)(ImageView(R.image.menu_copy)) {
                disableProperty().bind(attendanceList.selectionModel.selectedItemProperty().isNull)
                onAction { copyAttendance() }
            }
            getString(R.string.edit)(ImageView(R.image.menu_edit)) {
                disableProperty().bind(!attendanceList.selectionModel.selectedItemProperty().isNull)
                onAction { editAttendance() }
            }
            getString(R.string.delete)(ImageView(R.image.menu_delete)) {
                disableProperty().bind(!attendanceList.selectionModel.selectedItemProperty().isNull)
                onAction { attendanceList.items.remove(attendanceList.selectionModel.selectedItem) }
            }
            separatorMenuItem()
            getString(R.string.revert)(ImageView(R.image.menu_undo)) {
                onAction { attendee.attendances.revert() }
            }
            separatorMenuItem()
            deleteMenu = "${getString(R.string.delete)} ${attendee.name}"()
            deleteOthersMenu = getString(R.string.delete_others)()
            deleteToTheRightMenu = getString(R.string.delete_employees_to_the_right)()
        }
        contentDisplay = ContentDisplay.RIGHT
        graphic = ktfx.layouts.imageView {
            imageProperty().bind(buildBinding(hoverProperty()) {
                Image(
                    when {
                        isHover -> R.image.btn_clear_active
                        else -> R.image.btn_clear_inactive
                    }
                )
            })
            eventFilter(type = MOUSE_CLICKED) { deleteMenu.fire() }
        }
        GlobalScope.launch(Dispatchers.JavaFx) {
            delay(250)
            applyCss()
            layout()
            val titleRegion = find<Node>(".title")
            val padding = (titleRegion as StackPane).padding
            val graphicWidth = graphic.layoutBounds.width
            val labelWidth = titleRegion.find<Node>(".text").layoutBounds.width
            graphicTextGap = width - graphicWidth - padding.left - padding.right - labelWidth
        }
    }

    private fun addAttendance() = DateTimePopover(
        this,
        R.string.add_record,
        R.string.add,
        now().trimMinutes()
    ).show(attendanceList) {
        attendanceList.run {
            items.add(it)
            items.sort()
        }
    }

    private fun copyAttendance() = DateTimePopover(
        this,
        R.string.add_record,
        R.string.add,
        attendanceList.selectionModel.selectedItem.trimMinutes()
    ).show(attendanceList) {
        attendanceList.run {
            items.add(it)
            items.sort()
        }
    }

    private fun editAttendance() = DateTimePopover(
        this,
        R.string.edit_record,
        R.string.edit,
        attendanceList.selectionModel.selectedItem
    ).show(attendanceList) {
        attendanceList.run {
            items[attendanceList.selectionModel.selectedIndex] = it
            items.sort()
        }
    }

    private companion object {

        fun <T> ListView<T>.forceRefresh() {
            val temp = items
            items = null
            items = temp
        }
    }
}