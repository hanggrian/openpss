package com.hendraanggrian.openpss.ui.wage

import com.hendraanggrian.openpss.R
import com.hendraanggrian.openpss.content.Context
import com.hendraanggrian.openpss.content.PATTERN_DATETIME_EXTENDED
import com.hendraanggrian.openpss.control.IntField
import com.hendraanggrian.openpss.db.schemas.Recesses
import com.hendraanggrian.openpss.db.transaction
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
import javafx.scene.layout.StackPane
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.javafx.JavaFx
import kotlinx.coroutines.launch
import ktfx.bindings.asAny
import ktfx.cells.cellFactory
import ktfx.collections.sort
import ktfx.controls.find
import ktfx.controls.insetsOf
import ktfx.controls.isSelected
import ktfx.coroutines.eventFilter
import ktfx.coroutines.listener
import ktfx.coroutines.onAction
import ktfx.coroutines.onKeyPressed
import ktfx.coroutines.onMouseClicked
import ktfx.inputs.isDelete
import ktfx.inputs.isDoubleClick
import ktfx.jfoenix.layouts.jfxCheckBox
import ktfx.layouts.KtfxTitledPane
import ktfx.layouts.contextMenu
import ktfx.layouts.label
import ktfx.layouts.listView
import ktfx.layouts.menuItem
import ktfx.layouts.separatorMenuItem
import ktfx.layouts.styledGridPane
import ktfx.layouts.styledListView
import ktfx.layouts.text
import ktfx.layouts.vbox
import ktfx.text.pt
import org.joda.time.DateTime
import org.joda.time.DateTime.now
import kotlin.math.absoluteValue

class AttendeePane(
    context: Context,
    val attendee: Attendee
) : KtfxTitledPane(attendee.toString()), Context by context {

    val recessChecks: MutableList<CheckBox> = mutableListOf()
    var deleteMenu: MenuItem
    var deleteOthersMenu: MenuItem
    var deleteToTheRightMenu: MenuItem
    lateinit var attendanceList: ListView<DateTime>

    init {
        isCollapsible = false
        vbox {
            isFillWidth = true
            styledGridPane(R.style.white_background) {
                hgap = getDouble(R.dimen.padding_small)
                vgap = getDouble(R.dimen.padding_small)
                padding = insetsOf(getDouble(R.dimen.padding_medium))
                attendee.role?.let { role ->
                    label(getString(R.string.role)).grid(0, 0).margin(insetsOf(right = 4))
                    label(role).grid(0, 1 to 2)
                }
                label(getString(R.string.income)).grid(1, 0).margin(insetsOf(right = 4))
                addChild(
                    IntField().apply {
                        prefWidth = 80.0
                        promptText = getString(R.string.income)
                        valueProperty().bindBidirectional(attendee.dailyProperty)
                    }
                ).grid(1, 1)
                label("@${getString(R.string.day)}") { font = 10.pt }.grid(1, 2)
                label(getString(R.string.overtime)).grid(2, 0).margin(insetsOf(right = 4))
                addChild(
                    IntField().apply {
                        prefWidth = 80.0
                        promptText = getString(R.string.overtime)
                        valueProperty().bindBidirectional(attendee.hourlyOvertimeProperty)
                    }
                ).grid(2, 1)
                label("@${getString(R.string.hour)}") { font = 10.pt }.grid(2, 2)
                label(getString(R.string.recess)).grid(3, 0).margin(insetsOf(right = 4))
                vbox {
                    transaction {
                        Recesses().forEach { recess ->
                            recessChecks += jfxCheckBox(recess.toString()) {
                                selectedProperty().listener { _, _, selected ->
                                    if (selected) attendee.recesses += recess else attendee.recesses -= recess
                                    attendanceList.forceRefresh()
                                }
                                isSelected = true
                            }.margin(insetsOf(top = if (children.size > 1) 4 else 0))
                        }
                    }
                }.grid(3, 1 to 2)
            }
            attendanceList = styledListView(attendee.attendances, R.style.list_view_no_scrollbar_horizontal) {
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
                            }.hgrow()
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
                                                font = 10.pt
                                                if (hours > 12) {
                                                    style = "-fx-fill: #F44336;"
                                                }
                                            }.margin(insetsOf(left = getDouble(R.dimen.padding_medium)))
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
                onKeyPressed {
                    if (it.isDelete() && attendanceList.selectionModel.isSelected()) {
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
            deleteMenu = menuItem("${getString(R.string.delete)} ${attendee.name}")
            deleteOthersMenu = menuItem(getString(R.string.delete_others))
            deleteToTheRightMenu = menuItem(getString(R.string.delete_employees_to_the_right))
        }
        contentDisplay = ContentDisplay.RIGHT
        graphic = ktfx.layouts.imageView {
            imageProperty().bind(
                hoverProperty().asAny {
                    Image(if (it) R.image.btn_clear_active else R.image.btn_clear_inactive)
                }
            )
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
