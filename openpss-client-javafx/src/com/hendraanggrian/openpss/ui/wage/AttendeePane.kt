package com.hendraanggrian.openpss.ui.wage

import com.hendraanggrian.openpss.FxComponent
import com.hendraanggrian.openpss.PATTERN_DATETIMEEXT
import com.hendraanggrian.openpss.R
import com.hendraanggrian.openpss.R2
import com.hendraanggrian.openpss.api.OpenPSSApi
import com.hendraanggrian.openpss.control.IntField
import com.hendraanggrian.openpss.ui.DateTimePopOver
import com.hendraanggrian.openpss.util.round
import com.hendraanggrian.openpss.util.trimMinutes
import javafx.geometry.Pos
import javafx.scene.Node
import javafx.scene.control.CheckBox
import javafx.scene.control.ContentDisplay
import javafx.scene.control.ListView
import javafx.scene.control.MenuItem
import javafx.scene.image.Image
import javafx.scene.image.ImageView
import javafx.scene.input.MouseEvent
import javafx.scene.layout.StackPane
import kotlin.math.absoluteValue
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.javafx.JavaFx
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import ktfx.cells.cellFactory
import ktfx.collections.sort
import ktfx.controls.find
import ktfx.controls.gap
import ktfx.controls.isSelected
import ktfx.controls.notSelectedBinding
import ktfx.controls.paddingAll
import ktfx.coroutines.eventFilter
import ktfx.coroutines.onAction
import ktfx.coroutines.onKeyPressed
import ktfx.coroutines.onMouseClicked
import ktfx.inputs.isDelete
import ktfx.inputs.isDoubleClick
import ktfx.jfoenix.layouts.jfxCheckBox
import ktfx.layouts.KtfxTitledPane
import ktfx.layouts.addNode
import ktfx.layouts.contextMenu
import ktfx.layouts.gridPane
import ktfx.layouts.label
import ktfx.layouts.listView
import ktfx.layouts.menuItem
import ktfx.layouts.separatorMenuItem
import ktfx.layouts.text
import ktfx.layouts.vbox
import ktfx.listeners.listener
import ktfx.text.pt
import ktfx.toAny
import org.joda.time.DateTime
import org.joda.time.DateTime.now

class AttendeePane(
    component: FxComponent,
    val attendee: Attendee
) : KtfxTitledPane(attendee.toString()), FxComponent by component {

    val recessChecks: MutableList<CheckBox> = mutableListOf()
    val deleteMenu: MenuItem
    val deleteOthersMenu: MenuItem
    val deleteToTheRightMenu: MenuItem
    lateinit var attendanceList: ListView<DateTime>

    init {
        isCollapsible = false
        vbox {
            isFillWidth = true
            gridPane {
                styleClass += R.style.white_background
                gap = getDouble(R.value.padding_small)
                paddingAll = getDouble(R.value.padding_medium)
                attendee.role?.let { role ->
                    label(getString(R2.string.role)) col 0 row 0 marginRight 4.0
                    label(role) col (1 to 2) row 0
                }
                label(getString(R2.string.income)) col 0 row 1 marginRight 4.0
                addNode(IntField()) {
                    prefWidth = 80.0
                    promptText = getString(R2.string.income)
                    valueProperty().bindBidirectional(attendee.dailyProperty)
                } col 1 row 1
                label("@${getString(R2.string.day)}") { font = 10.pt } col 2 row 1
                label(getString(R2.string.overtime)) col 0 row 2 marginRight 4.0
                addNode(IntField()) {
                    prefWidth = 80.0
                    promptText = getString(R2.string.overtime)
                    valueProperty().bindBidirectional(attendee.hourlyOvertimeProperty)
                } col 1 row 2
                label("@${getString(R2.string.hour)}") { font = 10.pt } col 2 row 2
                label(getString(R2.string.recess)) col 0 row 3 marginRight 4.0
                vbox {
                    runBlocking { OpenPSSApi.getRecesses() }.forEach { recess ->
                        recessChecks += jfxCheckBox(recess.toString()) {
                            selectedProperty().listener { _, _, selected ->
                                if (selected) attendee.recesses += recess else attendee.recesses -= recess
                                attendanceList.forceRefresh()
                            }
                            isSelected = true
                        } marginTop if (children.size > 1) 4.0 else 0.0
                    }
                } col (1 to 2) row 3
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
                            alignment = Pos.CENTER
                            val itemLabel = label(dateTime.toString(PATTERN_DATETIMEEXT)) {
                                maxWidth = Double.MAX_VALUE
                            } hgrow true
                            if (index % 2 == 0) {
                                listView.items.getOrNull(index + 1).let { nextItem ->
                                    when (nextItem) {
                                        null -> itemLabel.textFill = getColor(R.value.color_red)
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
                                                font = 10.pt
                                                if (hours > 12) {
                                                    style = "-fx-fill: #F44336;"
                                                }
                                            } marginLeft getDouble(R.value.padding_medium)
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
            getString(R2.string.add)(ImageView(R.image.menu_add)) {
                onAction { addAttendance() }
            }
            separatorMenuItem()
            getString(R2.string.copy)(ImageView(R.image.menu_copy)) {
                disableProperty().bind(attendanceList.selectionModel.notSelectedBinding)
                onAction { copyAttendance() }
            }
            getString(R2.string.edit)(ImageView(R.image.menu_edit)) {
                disableProperty().bind(!attendanceList.selectionModel.notSelectedBinding)
                onAction { editAttendance() }
            }
            getString(R2.string.delete)(ImageView(R.image.menu_delete)) {
                disableProperty().bind(!attendanceList.selectionModel.notSelectedBinding)
                onAction { attendanceList.items.remove(attendanceList.selectionModel.selectedItem) }
            }
            separatorMenuItem()
            getString(R2.string.revert)(ImageView(R.image.menu_undo)) {
                onAction { attendee.attendances.revert() }
            }
            separatorMenuItem()
            deleteMenu = menuItem("${getString(R2.string.delete)} ${attendee.name}")
            deleteOthersMenu = menuItem(getString(R2.string.delete_others))
            deleteToTheRightMenu = menuItem(getString(R2.string.delete_employees_to_the_right))
        }
        contentDisplay = ContentDisplay.RIGHT
        graphic = ktfx.layouts.imageView {
            imageProperty().bind(hoverProperty().toAny {
                Image(
                    when {
                        it -> R.image.graphic_clear_active
                        else -> R.image.graphic_clear_inactive
                    }
                )
            })
            eventFilter(type = MouseEvent.MOUSE_CLICKED) { deleteMenu.fire() }
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

    private fun addAttendance() = DateTimePopOver(
        this,
        R2.string.add_record,
        R2.string.add,
        now().trimMinutes()
    ).show(attendanceList) {
        attendanceList.run {
            items.add(it)
            items.sort()
        }
    }

    private fun copyAttendance() = DateTimePopOver(
        this,
        R2.string.add_record,
        R2.string.add,
        attendanceList.selectionModel.selectedItem.trimMinutes()
    ).show(attendanceList) {
        attendanceList.run {
            items.add(it)
            items.sort()
        }
    }

    private fun editAttendance() = DateTimePopOver(
        this,
        R2.string.edit_record,
        R2.string.edit,
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
