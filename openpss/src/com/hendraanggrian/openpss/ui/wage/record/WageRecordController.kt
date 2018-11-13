package com.hendraanggrian.openpss.ui.wage.record

import com.hendraanggrian.openpss.R
import com.hendraanggrian.openpss.content.PATTERN_DATE
import com.hendraanggrian.openpss.content.PATTERN_DATETIME
import com.hendraanggrian.openpss.content.PATTERN_TIME
import com.hendraanggrian.openpss.content.STYLESHEET_PRINT_TREETABLEVIEW
import com.hendraanggrian.openpss.content.currencyConverter
import com.hendraanggrian.openpss.content.numberConverter
import com.hendraanggrian.openpss.content.trimMinutes
import com.hendraanggrian.openpss.control.UncollapsibleTreeItem
import com.hendraanggrian.openpss.control.popover.DatePopover
import com.hendraanggrian.openpss.control.popover.TimePopover
import com.hendraanggrian.openpss.io.WageDirectory
import com.hendraanggrian.openpss.io.WageFile
import com.hendraanggrian.openpss.ui.Controller
import com.hendraanggrian.openpss.ui.wage.Attendee
import com.hendraanggrian.openpss.ui.wage.record.Record.Companion.getDummy
import com.hendraanggrian.openpss.util.concatenate
import com.hendraanggrian.openpss.util.stringCell
import com.jfoenix.controls.JFXToolbar
import com.sun.javafx.scene.control.skin.TreeTableViewSkin
import com.sun.javafx.scene.control.skin.VirtualFlow
import javafx.beans.value.ObservableValue
import javafx.fxml.FXML
import javafx.scene.control.Button
import javafx.scene.control.Label
import javafx.scene.control.Menu
import javafx.scene.control.MenuBar
import javafx.scene.control.MenuItem
import javafx.scene.control.SelectionMode.MULTIPLE
import javafx.scene.control.TreeItem
import javafx.scene.control.TreeTableColumn
import javafx.scene.control.TreeTableView
import javafx.scene.layout.StackPane
import javafx.scene.layout.VBox
import ktfx.application.later
import ktfx.beans.binding.buildBooleanBinding
import ktfx.beans.binding.buildStringBinding
import ktfx.beans.value.lessEq
import ktfx.beans.value.or
import ktfx.collections.size
import ktfx.coroutines.onAction
import ktfx.embed.swing.toSwingImage
import ktfx.jfoenix.jfxIndefiniteSnackbar
import ktfx.layouts.label
import ktfx.layouts.menuItem
import ktfx.listeners.cellFactory
import ktfx.scene.snapshot
import ktfx.util.invoke
import org.apache.commons.lang3.SystemUtils
import org.joda.time.LocalTime
import java.awt.image.BufferedImage
import java.net.URL
import java.util.ResourceBundle
import javax.imageio.ImageIO

@Suppress("UNCHECKED_CAST")
class WageRecordController : Controller() {

    companion object {
        const val EXTRA_ATTENDEES = "EXTRA_ATTENDEES"
    }

    @FXML override lateinit var root: StackPane
    @FXML lateinit var vbox: VBox
    @FXML lateinit var menuBar: MenuBar
    @FXML lateinit var editMenu: Menu
    @FXML lateinit var undoMenu: MenuItem
    @FXML lateinit var toolbar: JFXToolbar
    @FXML lateinit var disableDailyIncomeButton: Button
    @FXML lateinit var lockStartButton: Button
    @FXML lateinit var lockEndButton: Button
    @FXML lateinit var totalLabel: Label
    @FXML lateinit var recordTable: TreeTableView<Record>
    @FXML lateinit var nameColumn: TreeTableColumn<Record, String>
    @FXML lateinit var startColumn: TreeTableColumn<Record, String>
    @FXML lateinit var endColumn: TreeTableColumn<Record, String>
    @FXML lateinit var dailyColumn: TreeTableColumn<Record, Double>
    @FXML lateinit var dailyIncomeColumn: TreeTableColumn<Record, Double>
    @FXML lateinit var overtimeColumn: TreeTableColumn<Record, Double>
    @FXML lateinit var overtimeIncomeColumn: TreeTableColumn<Record, Double>
    @FXML lateinit var totalColumn: TreeTableColumn<Record, Double>

    override fun initialize(location: URL, resources: ResourceBundle) {
        super.initialize(location, resources)
        menuBar.isUseSystemMenuBar = SystemUtils.IS_OS_MAC
        undoMenu.disableProperty().bind(editMenu.items.size() lessEq 2)
        arrayOf(lockStartButton, lockEndButton).forEach { button ->
            button.disableProperty().bind(recordTable.selectionModel.selectedItemProperty().isNull or
                buildBooleanBinding(recordTable.selectionModel.selectedItemProperty()) {
                    recordTable.selectionModel.selectedItems?.any { !it.value.isChild() } ?: true
                })
        }

        recordTable.run {
            selectionModel.selectionMode = MULTIPLE
            root = TreeItem(getDummy(this@WageRecordController))
            columns.forEach {
                it.cellFactory {
                    onUpdate { any, empty ->
                        if (any != null && !empty) graphic = label(
                            when (it) {
                                dailyColumn, overtimeColumn -> numberConverter(any as Number)
                                dailyIncomeColumn, overtimeIncomeColumn, totalColumn -> currencyConverter(any as Number)
                                else -> any.toString()
                            }
                        ) {
                            if (treeTableRow.treeItem?.value?.isTotal() == true) {
                                styleClass += "bold"
                            }
                        }
                    }
                }
            }
        }

        nameColumn.stringCell { displayedName }
        startColumn.setCellValueFactory { it.value.value.displayedStart }
        endColumn.setCellValueFactory { it.value.value.displayedEnd }
        dailyColumn.setCellValueFactory { it.value.value.dailyProperty as ObservableValue<Double> }
        dailyIncomeColumn.setCellValueFactory { it.value.value.dailyIncomeProperty as ObservableValue<Double> }
        overtimeColumn.setCellValueFactory { it.value.value.overtimeProperty as ObservableValue<Double> }
        overtimeIncomeColumn.setCellValueFactory { it.value.value.overtimeIncomeProperty as ObservableValue<Double> }
        totalColumn.setCellValueFactory { it.value.value.totalProperty as ObservableValue<Double> }

        later {
            getExtra<List<Attendee>>(EXTRA_ATTENDEES).forEach { attendee ->
                val node = attendee.toNodeRecord(this)
                val childs = attendee.toChildRecords(this)
                val total = attendee.toTotalRecords(this, childs)
                recordTable.root.children += UncollapsibleTreeItem(node).apply {
                    children += childs.map { TreeItem(it) }.toTypedArray()
                    children += TreeItem(total)
                }
            }
            totalLabel.textProperty()
                .bind(buildStringBinding(records.filter { it.isChild() }.map { it.totalProperty }) {
                    currencyConverter(records
                        .asSequence()
                        .filter { it.isTotal() }
                        .sumByDouble { it.totalProperty.value })
                })
        }
    }

    @FXML fun undo() = editMenu.items[2].fire()

    @FXML fun disableDailyIncome() =
        DatePopover(this, R.string.disable_daily_income).show(disableDailyIncomeButton) { date ->
            val undoable = Undoable()
            records.filter { it.startProperty.value.toLocalDate() == date }
                .forEach { record ->
                    val initial = record.dailyDisabledProperty.value
                    record.dailyDisabledProperty.set(!initial)
                    if (undoable.name == null) undoable.name = "${getString(R.string.daily_disabled)} " +
                        record.startProperty.value.toString(PATTERN_DATE)
                    undoable.addAction { record.dailyDisabledProperty.set(initial) }
                }
            undoable.append()
        }

    @FXML fun lockStart() =
        TimePopover(this, R.string.lock_start_time, LocalTime.now().trimMinutes()).show(lockStartButton) { time ->
            val undoable = Undoable()
            recordTable.selectionModel.selectedItems.map { it.value }
                .forEach { record ->
                    val initial = record.startProperty.value
                    if (initial.toLocalTime() < time!!) {
                        record.startProperty.set(record.cloneStart(time))
                        undoable.name = when {
                            undoable.name == null -> "${record.attendee.name} ${initial.toString(PATTERN_DATETIME)} -> " +
                                time.toString(PATTERN_TIME)
                            else -> getString(R.string.multiple_lock_start_time)
                        }
                        undoable.addAction { record.startProperty.set(initial) }
                    }
                }
            undoable.append()
        }

    @FXML fun lockEnd() =
        TimePopover(this, R.string.lock_end_time, LocalTime.now().trimMinutes()).show(lockEndButton) { time ->
            val undoable = Undoable()
            recordTable.selectionModel.selectedItems.map { it.value }
                .forEach { record ->
                    val initial = record.endProperty.value
                    if (initial.toLocalTime() > time!!) {
                        record.endProperty.set(record.cloneEnd(time))
                        undoable.name = when {
                            undoable.name == null -> "${record.attendee.name} ${initial.toString(PATTERN_DATETIME)} -> " +
                                time.toString(PATTERN_TIME)
                            else -> getString(R.string.multiple_lock_end_time)
                        }
                        undoable.addAction { record.endProperty.set(initial) }
                    }
                }
            undoable.append()
        }

    @FXML fun screenshot() {
        val images = mutableListOf<BufferedImage>()
        recordTable.selectionModel.clearSelection()
        togglePrintMode(true, STYLESHEET_PRINT_TREETABLEVIEW)
        recordTable.scrollTo(0)
        val flow = (recordTable.skin as TreeTableViewSkin<*>).children[1] as VirtualFlow<*>
        var i = 0
        do {
            images += recordTable.snapshot().toSwingImage()
            recordTable.scrollTo(flow.lastVisibleCell.index)
            i++
        } while (flow.lastVisibleCell.index + 1 <
            recordTable.root.children.size + recordTable.root.children.sumBy { it.children.size }
        )
        togglePrintMode(false, STYLESHEET_PRINT_TREETABLEVIEW)
        ImageIO.write(images.concatenate(), "png", WageFile())
        root.jfxIndefiniteSnackbar(getString(R.string.screenshot_finished), getString(R.string.open_folder)) {
            desktop?.open(WageDirectory)
        }
    }

    private inline val records: List<Record> get() = recordTable.root.children.flatMap { it.children }.map { it.value }

    private fun Undoable.append() {
        if (isValid) {
            editMenu.items.add(2, menuItem(name) {
                onAction {
                    undo()
                    editMenu.items.getOrNull(editMenu.items.indexOf(this@menuItem) - 1)?.fire()
                    editMenu.items -= this@menuItem
                }
            })
        }
    }

    private fun togglePrintMode(on: Boolean, printStylesheet: String) = when {
        on -> {
            vbox.children -= toolbar
            recordTable.stylesheets += printStylesheet
        }
        else -> {
            vbox.children.add(0, toolbar)
            recordTable.stylesheets -= printStylesheet
        }
    }
}