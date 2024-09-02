package com.hanggrian.openpss.ui.wage

import com.hanggrian.openpss.Context
import com.hanggrian.openpss.PATTERN_DATETIME_EXTENDED
import com.hanggrian.openpss.R
import com.hanggrian.openpss.control.IntField
import com.hanggrian.openpss.db.schemas.Recesses
import com.hanggrian.openpss.db.transaction
import com.hanggrian.openpss.popup.popover.DateTimePopover
import com.hanggrian.openpss.util.round
import com.hanggrian.openpss.util.trimMinutes
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
import ktfx.bindings.bindingBy
import ktfx.cells.cellFactory
import ktfx.collections.sort
import ktfx.controls.CENTER
import ktfx.controls.find
import ktfx.controls.insetsOf
import ktfx.controls.isSelected
import ktfx.controls.notSelectedBinding
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

class AttendeePane(context: Context, val attendee: Attendee) :
    KtfxTitledPane(attendee.toString()),
    Context by context {
    val recessChecks: MutableList<CheckBox> = mutableListOf()
    val deleteMenu: MenuItem
    val deleteOthersMenu: MenuItem
    val deleteToTheRightMenu: MenuItem
    lateinit var attendanceList: ListView<DateTime>

    init {
        isCollapsible = false
        vbox {
            isFillWidth = true
            styledGridPane(R.style_white_background) {
                hgap = getDouble(R.dimen_padding_small)
                vgap = getDouble(R.dimen_padding_small)
                padding = insetsOf(getDouble(R.dimen_padding_medium))
                attendee.role?.let { role ->
                    label(getString(R.string_role))
                        .grid(0, 0)
                        .margin(insetsOf(right = 4))
                    label(role)
                        .grid(0, 1 to 2)
                }
                label(getString(R.string_income))
                    .grid(1, 0)
                    .margin(insetsOf(right = 4))
                addChild(
                    IntField().apply {
                        prefWidth = 80.0
                        promptText = getString(R.string_income)
                        valueProperty.bindBidirectional(attendee.dailyProperty)
                    },
                ).grid(1, 1)
                label("@${getString(R.string_day)}") { font = 10.pt }
                    .grid(1, 2)
                label(getString(R.string_overtime))
                    .grid(2, 0)
                    .margin(insetsOf(right = 4))
                addChild(
                    IntField().apply {
                        prefWidth = 80.0
                        promptText = getString(R.string_overtime)
                        valueProperty.bindBidirectional(attendee.hourlyOvertimeProperty)
                    },
                ).grid(2, 1)
                label("@${getString(R.string_hour)}") { font = 10.pt }
                    .grid(2, 2)
                label(getString(R.string_recess))
                    .grid(3, 0)
                    .margin(insetsOf(right = 4))
                vbox {
                    transaction {
                        Recesses().forEach { recess ->
                            recessChecks +=
                                jfxCheckBox(recess.toString()) {
                                    selectedProperty().listener { _, _, selected ->
                                        when {
                                            selected -> attendee.recesses += recess
                                            else -> attendee.recesses -= recess
                                        }
                                        attendanceList.forceRefresh()
                                    }
                                    isSelected = true
                                }.margin(insetsOf(top = if (children.size > 1) 4 else 0))
                        }
                    }
                }.grid(3, 1 to 2)
            }
            attendanceList =
                styledListView(attendee.attendances, R.style_list_view_no_scrollbar_horizontal) {
                    prefWidth = 150.0
                    maxHeight = 360.0 // just enough for 7 days attendance
                    cellFactory {
                        onUpdate { dateTime, empty ->
                            text = null
                            graphic = null
                            if (dateTime == null || empty) {
                                return@onUpdate
                            }
                            graphic =
                                ktfx.layouts.hbox {
                                    alignment = CENTER
                                    val itemLabel =
                                        label(dateTime.toString(PATTERN_DATETIME_EXTENDED)) {
                                            maxWidth = Double.MAX_VALUE
                                        }.hgrow()
                                    if (index % 2 != 0) {
                                        return@hbox
                                    }
                                    listView.items.getOrNull(index + 1).let { nextItem ->
                                        when (nextItem) {
                                            null -> itemLabel.textFill = getColor(R.color_red)
                                            else -> {
                                                val interval =
                                                    SafeInterval.of(dateTime, nextItem)
                                                var minutes = interval.minutes
                                                attendee.recesses
                                                    .map { it.getInterval(dateTime) }
                                                    .forEach {
                                                        minutes -=
                                                            interval
                                                                .overlap(it)
                                                                ?.toDuration()
                                                                ?.toStandardMinutes()
                                                                ?.minutes
                                                                ?.absoluteValue
                                                                ?: 0
                                                    }
                                                val hours = (minutes / 60.0).round()
                                                text(hours.toString()) {
                                                    font = 10.pt
                                                    if (hours > 12) {
                                                        style = "-fx-fill: #F44336;"
                                                    }
                                                }.margin(
                                                    insetsOf(
                                                        left = getDouble(R.dimen_padding_medium),
                                                    ),
                                                )
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
            getString(R.string_add)(ImageView(R.image_menu_add)) {
                onAction { addAttendance() }
            }
            getString(R.string_copy)(ImageView(R.image_menu_copy)) {
                disableProperty().bind(attendanceList.selectionModel.notSelectedBinding)
                onAction { copyAttendance() }
            }
            getString(R.string_edit)(ImageView(R.image_menu_edit)) {
                disableProperty().bind(attendanceList.selectionModel.notSelectedBinding)
                onAction { editAttendance() }
            }
            getString(R.string_delete)(ImageView(R.image_menu_delete)) {
                disableProperty().bind(attendanceList.selectionModel.notSelectedBinding)
                onAction { attendanceList.items.remove(attendanceList.selectionModel.selectedItem) }
            }
            separatorMenuItem()
            getString(R.string_revert)(ImageView(R.image_menu_undo)) {
                onAction { attendee.attendances.revert() }
            }
            separatorMenuItem()
            deleteMenu = menuItem("${getString(R.string_delete)} ${attendee.name}")
            deleteOthersMenu = menuItem(getString(R.string_delete_others))
            deleteToTheRightMenu = menuItem(getString(R.string_delete_employees_to_the_right))
        }
        contentDisplay = ContentDisplay.RIGHT
        graphic =
            ktfx.layouts.imageView {
                imageProperty().bind(
                    hoverProperty().bindingBy {
                        Image(if (it) R.image_btn_clear_active else R.image_btn_clear_inactive)
                    },
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

    private fun addAttendance() =
        DateTimePopover(
            this,
            R.string_add_record,
            R.string_add,
            now().trimMinutes(),
        ).show(attendanceList) {
            attendanceList.run {
                items.add(it)
                items.sort()
            }
        }

    private fun copyAttendance() =
        DateTimePopover(
            this,
            R.string_add_record,
            R.string_add,
            attendanceList.selectionModel.selectedItem.trimMinutes(),
        ).show(attendanceList) {
            attendanceList.run {
                items.add(it)
                items.sort()
            }
        }

    private fun editAttendance() =
        DateTimePopover(
            this,
            R.string_edit_record,
            R.string_edit,
            attendanceList.selectionModel.selectedItem,
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
