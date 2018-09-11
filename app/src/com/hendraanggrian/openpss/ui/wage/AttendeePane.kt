package com.hendraanggrian.openpss.ui.wage

import com.hendraanggrian.openpss.PATTERN_DATETIME_EXTENDED
import com.hendraanggrian.openpss.R
import com.hendraanggrian.openpss.control.forceRefresh
import com.hendraanggrian.openpss.control.intField
import com.hendraanggrian.openpss.control.popover.DateTimePopover
import com.hendraanggrian.openpss.db.schemas.Recesses
import com.hendraanggrian.openpss.db.transaction
import com.hendraanggrian.openpss.i18n.Resourced
import com.hendraanggrian.openpss.ui.Selectable
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
import javafxx.beans.binding.bindingOf
import javafxx.collections.sort
import javafxx.coroutines.FX
import javafxx.coroutines.eventFilter
import javafxx.coroutines.listener
import javafxx.coroutines.onAction
import javafxx.coroutines.onKeyPressed
import javafxx.coroutines.onMouseClicked
import javafxx.layouts.LayoutDsl
import javafxx.layouts.checkBox
import javafxx.layouts.contextMenu
import javafxx.layouts.gridPane
import javafxx.layouts.imageView
import javafxx.layouts.label
import javafxx.layouts.listView
import javafxx.layouts.separatorMenuItem
import javafxx.layouts.vbox
import javafxx.listeners.cellFactory
import javafxx.scene.input.isDelete
import javafxx.scene.input.isDoubleClick
import javafxx.scene.layout.gap
import javafxx.scene.layout.paddingAll
import kotlinx.coroutines.experimental.delay
import kotlinx.coroutines.experimental.launch
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
                gap = R.dimen.padding_verysmall.toDouble()
                paddingAll = R.dimen.padding_small.toDouble()
                attendee.role?.let { role ->
                    label(getString(R.string.role)) col 0 row 0 marginRight 4.0
                    label(role) col 1 row 0 colSpans 2
                }
                label(getString(R.string.income)) col 0 row 1 marginRight 4.0
                intField {
                    prefWidth = 80.0
                    promptText = getString(R.string.income)
                    valueProperty().bindBidirectional(attendee.dailyProperty)
                } col 1 row 1
                label("@${getString(R.string.day)}") { font = font(10.0) } col 2 row 1
                label(getString(R.string.overtime)) col 0 row 2 marginRight 4.0
                intField {
                    prefWidth = 80.0
                    promptText = getString(R.string.overtime)
                    valueProperty().bindBidirectional(attendee.hourlyOvertimeProperty)
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
                        if (dateTime != null && !empty) graphic = javafxx.layouts.hbox {
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
                                        } marginLeft R.dimen.padding_small.toDouble()
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
                onAction { copyAttendance() }
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
                Image(
                    when {
                        isHover -> R.image.btn_clear_active
                        else -> R.image.btn_clear_inactive
                    }
                )
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

    private fun addAttendance() = DateTimePopover(this, R.string.add_record, R.string.add,
        now().run { minusMinutes(minuteOfHour) }).showAt(attendanceList) {
        attendanceList.run {
            items.add(it)
            items.sort()
        }
    }

    private fun copyAttendance() = DateTimePopover(this, R.string.add_record, R.string.add,
        selected!!.run { minusMinutes(minuteOfHour) }).showAt(attendanceList) {
        attendanceList.run {
            items.add(it)
            items.sort()
        }
    }

    private fun editAttendance() = DateTimePopover(
        this, R.string.edit_record, R.string.edit,
        selected!!
    ).showAt(attendanceList) {
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