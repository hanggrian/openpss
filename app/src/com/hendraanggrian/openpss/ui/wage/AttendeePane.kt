package com.hendraanggrian.openpss.ui.wage

import com.hendraanggrian.openpss.R
import com.hendraanggrian.openpss.controls.DateTimeDialog
import com.hendraanggrian.openpss.controls.intField
import com.hendraanggrian.openpss.db.schemas.Recesses
import com.hendraanggrian.openpss.db.transaction
import com.hendraanggrian.openpss.resources.Resourced
import com.hendraanggrian.openpss.ui.Selectable
import com.hendraanggrian.openpss.util.PATTERN_DATETIME_EXTENDED
import com.hendraanggrian.openpss.util.forceRefresh
import com.hendraanggrian.openpss.util.getColor
import com.hendraanggrian.openpss.util.round
import javafx.geometry.Pos.CENTER
import javafx.scene.control.CheckBox
import javafx.scene.control.ContentDisplay.RIGHT
import javafx.scene.control.ListView
import javafx.scene.control.MenuItem
import javafx.scene.control.SelectionModel
import javafx.scene.control.TitledPane
import javafx.scene.image.Image
import javafx.scene.image.ImageView
import javafx.scene.input.MouseEvent.MOUSE_CLICKED
import javafx.scene.layout.Priority.ALWAYS
import javafx.scene.layout.StackPane
import javafx.scene.text.Font.font
import kotlinx.coroutines.experimental.delay
import kotlinx.coroutines.experimental.launch
import ktfx.beans.binding.bindingOf
import ktfx.collections.sort
import ktfx.coroutines.FX
import ktfx.coroutines.eventFilter
import ktfx.coroutines.listener
import ktfx.coroutines.onAction
import ktfx.coroutines.onKeyPressed
import ktfx.coroutines.onMouseClicked
import ktfx.layouts.LayoutDsl
import ktfx.layouts.checkBox
import ktfx.layouts.contextMenu
import ktfx.layouts.gridPane
import ktfx.layouts.imageView
import ktfx.layouts.label
import ktfx.layouts.listView
import ktfx.layouts.separatorMenuItem
import ktfx.layouts.vbox
import ktfx.listeners.cellFactory
import ktfx.scene.input.isDelete
import ktfx.scene.input.isDoubleClick
import ktfx.scene.layout.gap
import ktfx.scene.layout.paddingAll
import org.joda.time.DateTime
import org.joda.time.DateTime.now
import kotlin.math.absoluteValue

class AttendeePane(
    resourced: Resourced,
    val attendee: Attendee
) : TitledPane(attendee.toString(), null), Resourced by resourced, Selectable<DateTime> {

    val recessChecks: MutableList<CheckBox> = mutableListOf()
    lateinit var deleteMenu: MenuItem
    lateinit var deleteOthersMenu: MenuItem
    lateinit var deleteToTheRightMenu: MenuItem
    lateinit var attendanceList: ListView<DateTime>

    init {
        isCollapsible = false
        content = vbox {
            isFillWidth = true
            gridPane {
                gap = 4.0
                paddingAll = 8.0
                attendee.role?.let { role ->
                    label(getString(R.string.role)) col 0 row 0 marginRight 4.0
                    label(role) col 1 row 0 colSpans 2
                }
                label(getString(R.string.income)) col 0 row 1 marginRight 4.0
                intField {
                    prefWidth = 80.0
                    promptText = getString(R.string.income)
                    valueProperty.bindBidirectional(attendee.dailyProperty)
                } col 1 row 1
                label("@${getString(R.string.day)}") { font = font(10.0) } col 2 row 1
                label(getString(R.string.overtime)) col 0 row 2 marginRight 4.0
                intField {
                    prefWidth = 80.0
                    promptText = getString(R.string.overtime)
                    valueProperty.bindBidirectional(attendee.hourlyOvertimeProperty)
                } col 1 row 2
                label("@${getString(R.string.hour)}") { font = font(10.0) } col 2 row 2
                label(getString(R.string.recess)) col 0 row 3 marginRight 4.0
                vbox {
                    transaction {
                        Recesses().forEach { recess ->
                            recessChecks += checkBox(recess.toString()) {
                                selectedProperty().listener { _, _, selected ->
                                    if (selected) attendee.recesses += recess else attendee.recesses -= recess
                                    attendanceList.forceRefresh()
                                }
                                isSelected = true
                            } marginTop if (children.size > 1) 4.0 else 0.0
                        }
                    }
                } col 1 row 3 colSpans 2
            }
            attendanceList = listView(attendee.attendances) {
                prefWidth = 128.0
                maxHeight = 352.0 // just enough for 7 days attendance
                cellFactory {
                    onUpdate { dateTime, empty ->
                        text = null
                        graphic = null
                        if (dateTime != null && !empty) graphic = ktfx.layouts.hbox {
                            alignment = CENTER
                            val itemLabel = label(dateTime.toString(PATTERN_DATETIME_EXTENDED)) {
                                maxWidth = Double.MAX_VALUE
                            } hpriority ALWAYS
                            if (index % 2 == 0) listView.items.getOrNull(index + 1).let { nextItem ->
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
                                        label(hours.toString()) {
                                            font = font(10.0)
                                            if (hours > 12) textFill = getColor(R.color.red)
                                        } marginLeft 8.0
                                    }
                                }
                            }
                        }
                    }
                }
                onKeyPressed { if (it.code.isDelete() && selected != null) items.remove(selected) }
                onMouseClicked { if (it.isDoubleClick() && selected != null) editAttendance() }
            }
        }
        contextMenu {
            getString(R.string.add)(ImageView(R.image.menu_add)) {
                onAction { addAttendance() }
            }
            separatorMenuItem()
            getString(R.string.copy)(ImageView(R.image.menu_copy)) {
                disableProperty().bind(!selectedBinding)
                onAction { cloneAttendance() }
            }
            getString(R.string.edit)(ImageView(R.image.menu_edit)) {
                disableProperty().bind(!selectedBinding)
                onAction { editAttendance() }
            }
            getString(R.string.delete)(ImageView(R.image.menu_delete)) {
                disableProperty().bind(!selectedBinding)
                onAction { attendanceList.items.remove(selected) }
            }
            separatorMenuItem()
            getString(R.string.revert)(ImageView(R.image.menu_revert)) {
                onAction { attendee.attendances.revert() }
            }
            separatorMenuItem()
            deleteMenu = "${getString(R.string.delete)} ${attendee.name}"()
            deleteOthersMenu = getString(R.string.delete_others)()
            deleteToTheRightMenu = getString(R.string.delete_employees_to_the_right)()
        }
        contentDisplay = RIGHT
        graphic = imageView {
            imageProperty().bind(bindingOf(hoverProperty()) {
                Image(when {
                    isHover -> R.image.button_clear_active
                    else -> R.image.button_clear_inactive
                })
            })
            eventFilter(type = MOUSE_CLICKED) { deleteMenu.fire() }
        }
        launch(FX) {
            delay(250)
            applyCss()
            layout()
            val titleRegion = lookup(".title")
            val padding = (titleRegion as StackPane).padding
            val graphicWidth = graphic.layoutBounds.width
            val labelWidth = titleRegion.lookup(".text").layoutBounds.width
            graphicTextGap = width - graphicWidth - padding.left - padding.right - labelWidth
        }
    }

    override val selectionModel: SelectionModel<DateTime> get() = attendanceList.selectionModel

    private fun addAttendance() = DateTimeDialog(this@AttendeePane, R.string.add_record,
        now().run { minusMinutes(minuteOfHour) })
        .showAndWait()
        .ifPresent {
            attendanceList.run {
                items.add(it)
                items.sort()
            }
        }

    private fun cloneAttendance() = DateTimeDialog(this@AttendeePane, R.string.add_record,
        selected!!.run { minusMinutes(minuteOfHour) })
        .showAndWait()
        .ifPresent {
            attendanceList.run {
                items.add(it)
                items.sort()
            }
        }

    private fun editAttendance() = DateTimeDialog(this@AttendeePane, R.string.edit_record, selected!!)
        .showAndWait()
        .ifPresent {
            attendanceList.run {
                items[attendanceList.selectionModel.selectedIndex] = it
                items.sort()
            }
        }
}

@Suppress("NOTHING_TO_INLINE")
inline fun attendeePane(
    resourced: Resourced,
    attendee: Attendee,
    noinline init: ((@LayoutDsl AttendeePane).() -> Unit)? = null
): AttendeePane = AttendeePane(resourced, attendee).apply { init?.invoke(this) }