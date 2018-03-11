package com.hendraanggrian.openpss.ui.wage

import com.hendraanggrian.openpss.R
import com.hendraanggrian.openpss.db.schema.Recesses
import com.hendraanggrian.openpss.db.transaction
import com.hendraanggrian.openpss.scene.control.intField
import com.hendraanggrian.openpss.time.FlexibleInterval
import com.hendraanggrian.openpss.time.PATTERN_DATETIME_EXTENDED
import com.hendraanggrian.openpss.ui.DateTimeDialog
import com.hendraanggrian.openpss.ui.Resourced
import com.hendraanggrian.openpss.util.getColor
import com.hendraanggrian.openpss.util.isDelete
import com.hendraanggrian.openpss.util.round
import javafx.geometry.Pos
import javafx.scene.control.ContentDisplay
import javafx.scene.control.ListView
import javafx.scene.control.MenuItem
import javafx.scene.control.TitledPane
import javafx.scene.image.Image
import javafx.scene.input.MouseEvent
import javafx.scene.layout.Priority
import javafx.scene.layout.StackPane
import javafx.scene.text.Font
import kotlinfx.beans.binding.bindingOf
import kotlinfx.collections.emptyBinding
import kotlinfx.collections.sort
import kotlinfx.coroutines.FX
import kotlinfx.coroutines.listener
import kotlinfx.coroutines.onAction
import kotlinfx.coroutines.onKeyPressed
import kotlinfx.layouts.LayoutDsl
import kotlinfx.layouts.checkBox
import kotlinfx.layouts.contextMenu
import kotlinfx.layouts.gridPane
import kotlinfx.layouts.imageView
import kotlinfx.layouts.label
import kotlinfx.layouts.listView
import kotlinfx.layouts.menuItem
import kotlinfx.layouts.separatorMenuItem
import kotlinfx.layouts.vbox
import kotlinfx.listeners.cellFactory
import kotlinfx.scene.layout.gaps
import kotlinfx.scene.layout.paddings
import kotlinfx.scene.layout.prefSize
import kotlinx.coroutines.experimental.delay
import kotlinx.coroutines.experimental.launch
import org.joda.time.DateTime

class AttendeePane(
    resourced: Resourced,
    val attendee: Attendee
) : TitledPane(attendee.toString(), null), Resourced by resourced {

    private lateinit var listView: ListView<DateTime>
    lateinit var deleteMenu: MenuItem
    lateinit var deleteOthersMenu: MenuItem
    lateinit var deleteToTheRightMenu: MenuItem

    init {
        isCollapsible = false
        content = vbox {
            gridPane {
                gaps = 4
                paddings = 8
                attendee.role?.let { role ->
                    label(getString(R.string.role)) col 0 row 0 rightMargin 4
                    label(role) col 1 row 0 colSpan 2
                }
                label(getString(R.string.income)) col 0 row 1 rightMargin 4
                intField {
                    prefSize(width = 88)
                    promptText = getString(R.string.income)
                    valueProperty.bindBidirectional(attendee.dailyProperty)
                } col 1 row 1
                label("@${getString(R.string.day)}") { font = Font.font(9.0) } col 2 row 1
                label(getString(R.string.overtime)) col 0 row 2 rightMargin 4
                intField {
                    prefSize(width = 88)
                    promptText = getString(R.string.overtime)
                    valueProperty.bindBidirectional(attendee.hourlyOvertimeProperty)
                } col 1 row 2
                label("@${getString(R.string.hour)}") { font = Font.font(9.0) } col 2 row 2
                label(getString(R.string.recess)) col 0 row 3 rightMargin 4
                vbox {
                    transaction {
                        Recesses.find().forEach { recess ->
                            checkBox(recess.toString()) {
                                selectedProperty().listener { _, _, selected ->
                                    attendee.recesses.let { recesses ->
                                        if (selected) recesses.add(recess) else recesses.remove(recess)
                                    }
                                }
                                isSelected = true
                            } topMargin if (children.size > 1) 4 else 0
                        }
                    }
                } col 1 row 3 colSpan 2
            }
            listView = listView(attendee.attendances) {
                prefSize(width = 128)
                cellFactory {
                    onUpdateItem { dateTime, empty ->
                        text = null
                        graphic = null
                        if (dateTime != null && !empty) graphic = kotlinfx.layouts.hbox {
                            val index = listView.items.indexOf(dateTime)
                            alignment = if (index % 2 == 0) Pos.BOTTOM_CENTER else Pos.TOP_CENTER
                            val itemLabel = label(dateTime.toString(PATTERN_DATETIME_EXTENDED)) { maxWidth = Double.MAX_VALUE } hpriority Priority.ALWAYS
                            if (alignment == Pos.BOTTOM_CENTER) listView.items.getOrNull(index + 1).let { nextItem ->
                                when (nextItem) {
                                    null -> itemLabel.textFill = getColor(R.color.red)
                                    else -> label(FlexibleInterval(dateTime, nextItem).hours.round().toString()) { font = Font.font(9.0) }
                                }
                            }
                        }
                    }
                }
                onKeyPressed {
                    if (it.code.isDelete() && selectionModel.selectedItem != null) selectionModel.run {
                        listView.items.remove(selectedItem)
                        clearSelection()
                    }
                }
            }
        }
        contextMenu {
            menuItem(getString(R.string.add)) {
                onAction {
                    val prefill = listView.selectionModel.selectedItem ?: DateTime.now()
                    DateTimeDialog(this@AttendeePane, getString(R.string.add_record), prefill.minusMinutes(prefill.minuteOfHour))
                        .showAndWait()
                        .ifPresent {
                            listView.items.add(it)
                            listView.items.sort()
                        }
                }
            }
            menuItem(getString(R.string.edit)) {
                disableProperty().bind(listView.selectionModel.selectedItems.emptyBinding())
                onAction {
                    DateTimeDialog(this@AttendeePane, getString(R.string.edit_record), listView.selectionModel.selectedItem)
                        .showAndWait()
                        .ifPresent {
                            listView.items[listView.selectionModel.selectedIndex] = it
                            listView.items.sort()
                        }
                }
            }
            menuItem(getString(R.string.delete)) {
                disableProperty().bind(listView.selectionModel.selectedItems.emptyBinding())
                onAction {
                    listView.selectionModel.run {
                        listView.items.remove(selectedItem)
                        clearSelection()
                    }
                }
            }
            separatorMenuItem()
            menuItem(getString(R.string.revert)) { onAction { attendee.attendances.revert() } }
            separatorMenuItem()
            deleteMenu = menuItem("${getString(R.string.delete)} ${attendee.name}")
            deleteOthersMenu = menuItem(getString(R.string.delete_others))
            deleteToTheRightMenu = menuItem(getString(R.string.delete_employees_to_the_right))
        }
        contentDisplay = ContentDisplay.RIGHT
        graphic = imageView {
            imageProperty().bind(bindingOf(hoverProperty()) { Image(if (isHover) R.image.btn_clear_active else R.image.btn_clear_inactive) })
            addEventHandler(MouseEvent.MOUSE_CLICKED) {
                it.consume()
                deleteMenu.fire()
            }
        }
        launch(FX) {
            delay(100)
            applyCss()
            layout()
            val titleRegion = lookup(".title")
            val padding = (titleRegion as StackPane).padding
            val graphicWidth = graphic.layoutBounds.width
            val labelWidth = titleRegion.lookup(".text").layoutBounds.width
            graphicTextGap = width - graphicWidth - padding.left - padding.right - labelWidth
        }
    }
}

@Suppress("NOTHING_TO_INLINE")
inline fun attendeePane(
    resourced: Resourced,
    attendee: Attendee,
    noinline init: ((@LayoutDsl AttendeePane).() -> Unit)? = null
): AttendeePane = AttendeePane(resourced, attendee).apply { init?.invoke(this) }