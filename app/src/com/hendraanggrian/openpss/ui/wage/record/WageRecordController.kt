package com.hendraanggrian.openpss.ui.wage.record

import com.hendraanggrian.openpss.BuildConfig.DEBUG
import com.hendraanggrian.openpss.R
import com.hendraanggrian.openpss.controls.DateDialog
import com.hendraanggrian.openpss.controls.UncollapsibleTreeItem
import com.hendraanggrian.openpss.io.WageContentFolder
import com.hendraanggrian.openpss.io.WageFile
import com.hendraanggrian.openpss.layouts.TimeBox
import com.hendraanggrian.openpss.time.PATTERN_DATE
import com.hendraanggrian.openpss.time.PATTERN_DATETIME
import com.hendraanggrian.openpss.time.PATTERN_TIME
import com.hendraanggrian.openpss.ui.Controller
import com.hendraanggrian.openpss.ui.wage.Attendee
import com.hendraanggrian.openpss.ui.wage.record.Record.Companion.getDummy
import com.hendraanggrian.openpss.utils.currencyConverter
import com.hendraanggrian.openpss.utils.getFont
import com.hendraanggrian.openpss.utils.getResource
import com.hendraanggrian.openpss.utils.numberConverter
import com.hendraanggrian.openpss.utils.openFile
import com.hendraanggrian.openpss.utils.stringCell
import com.hendraanggrian.openpss.utils.style
import com.sun.javafx.scene.control.skin.TreeTableViewSkin
import com.sun.javafx.scene.control.skin.VirtualFlow
import javafx.fxml.FXML
import javafx.scene.control.Button
import javafx.scene.control.ButtonBar.ButtonData.CANCEL_CLOSE
import javafx.scene.control.Label
import javafx.scene.control.SelectionMode.MULTIPLE
import javafx.scene.control.SplitMenuButton
import javafx.scene.control.ToolBar
import javafx.scene.control.TreeItem
import javafx.scene.control.TreeTableColumn
import javafx.scene.control.TreeTableView
import javafx.scene.layout.VBox
import ktfx.application.later
import ktfx.beans.binding.booleanBindingOf
import ktfx.beans.binding.stringBindingOf
import ktfx.beans.property.asObservable
import ktfx.beans.value.or
import ktfx.collections.isEmpty
import ktfx.coroutines.onAction
import ktfx.layouts.label
import ktfx.layouts.menuItem
import ktfx.listeners.cellFactory
import ktfx.scene.control.button
import ktfx.scene.control.infoAlert
import ktfx.scene.snapshot
import java.net.URL
import java.util.ResourceBundle

class WageRecordController : Controller() {

    companion object {
        const val EXTRA_ATTENDEES = "EXTRA_ATTENDEES"
    }

    @FXML lateinit var root: VBox
    @FXML lateinit var toolbar1: ToolBar
    @FXML lateinit var toolbar2: ToolBar
    @FXML lateinit var undoButton: SplitMenuButton
    @FXML lateinit var timeBox: TimeBox
    @FXML lateinit var totalLabel1: Label
    @FXML lateinit var totalLabel2: Label
    @FXML lateinit var lockStartButton: Button
    @FXML lateinit var lockEndButton: Button
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
        undoButton.disableProperty().bind(undoButton.items.isEmpty)
        arrayOf(lockStartButton, lockEndButton).forEach {
            it.disableProperty().bind(recordTable.selectionModel.selectedItemProperty().isNull or
                booleanBindingOf(recordTable.selectionModel.selectedItemProperty()) {
                    recordTable.selectionModel.selectedItems?.any { !it.value.isChild() } ?: true
                })
        }
        totalLabel1.font = getFont(R.font.opensans_bold)

        recordTable.run {
            selectionModel.selectionMode = MULTIPLE
            root = TreeItem(getDummy(this@WageRecordController))
            columns.forEach {
                it.cellFactory {
                    onUpdate { any, empty ->
                        if (any != null && !empty) graphic = label(when (it) {
                            dailyColumn, overtimeColumn -> numberConverter.toString(any as Number)
                            dailyIncomeColumn, overtimeIncomeColumn, totalColumn -> currencyConverter.toString(any as Number)
                            else -> any.toString()
                        }) {
                            font = getFont(when {
                                treeTableRow.treeItem?.value?.isTotal() ?: true -> R.font.opensans_bold
                                else -> R.font.opensans_regular
                            })
                        }
                    }
                }
            }
        }

        nameColumn.stringCell { displayedName }
        startColumn.setCellValueFactory { it.value.value.displayedStart }
        endColumn.setCellValueFactory { it.value.value.displayedEnd }
        dailyColumn.setCellValueFactory { it.value.value.dailyProperty.asObservable() }
        dailyIncomeColumn.setCellValueFactory { it.value.value.dailyIncomeProperty.asObservable() }
        overtimeColumn.setCellValueFactory { it.value.value.overtimeProperty.asObservable() }
        overtimeIncomeColumn.setCellValueFactory { it.value.value.overtimeIncomeProperty.asObservable() }
        totalColumn.setCellValueFactory { it.value.value.totalProperty.asObservable() }

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
            totalLabel2.textProperty().bind(
                stringBindingOf(*records.filter { it.isChild() }.map { it.totalProperty }.toTypedArray()) {
                    currencyConverter.toString(records
                        .filter { it.isTotal() }
                        .sumByDouble { it.totalProperty.value })
                })
        }
    }

    @FXML fun undo() = undoButton.items[0].fire()

    @FXML fun lockStart() {
        val undoable = Undoable()
        recordTable.selectionModel.selectedItems
            .map { it.value }
            .forEach { record ->
                val initial = record.startProperty.value
                if (initial.toLocalTime() < timeBox.value) {
                    record.startProperty.set(record.cloneStart(timeBox.value))
                    undoable.name = when {
                        undoable.name == null -> "${record.attendee.name} ${initial.toString(PATTERN_DATETIME)} -> " +
                            timeBox.value.toString(PATTERN_TIME)
                        else -> getString(R.string.multiple_lock_start_time)
                    }
                    undoable.addAction { record.startProperty.set(initial) }
                }
            }
        undoable.append()
    }

    @FXML fun lockEnd() {
        val undoable = Undoable()
        recordTable.selectionModel.selectedItems
            .map { it.value }
            .forEach { record ->
                val initial = record.endProperty.value
                if (initial.toLocalTime() > timeBox.value) {
                    record.endProperty.set(record.cloneEnd(timeBox.value))
                    undoable.name = when {
                        undoable.name == null -> "${record.attendee.name} ${initial.toString(PATTERN_DATETIME)} -> " +
                            timeBox.value.toString(PATTERN_TIME)
                        else -> getString(R.string.multiple_lock_end_time)
                    }
                    undoable.addAction { record.endProperty.set(initial) }
                }
            }
        undoable.append()
    }

    @FXML fun empty() = DateDialog(this, R.string.disable_daily_income)
        .showAndWait()
        .ifPresent { date ->
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

    @FXML fun screenshot() = getResource(R.style.treetableview_print).toExternalForm().let { printStylesheet ->
        togglePrintMode(true, printStylesheet)
        recordTable.scrollTo(0)
        val flow = (recordTable.skin as TreeTableViewSkin<*>).children[1] as VirtualFlow<*>
        var i = 0
        do {
            if (DEBUG) println("Snapshoting page $i")
            WageFile(i).write(recordTable.snapshot())
            recordTable.scrollTo(flow.lastVisibleCell.index)
            i++
        } while (flow.lastVisibleCell.index + 1 <
            recordTable.root.children.size + recordTable.root.children.sumBy { it.children.size })
        togglePrintMode(false, printStylesheet)
        infoAlert(getString(R.string.screenshot_finished)) {
            style()
            button(getString(R.string.open_folder), CANCEL_CLOSE)
        }.showAndWait()
            .filter { it.buttonData == CANCEL_CLOSE }
            .ifPresent { openFile(WageContentFolder) }
    }

    private inline val records: List<Record> get() = recordTable.root.children.flatMap { it.children }.map { it.value }

    private fun Undoable.append() {
        if (isValid) undoButton.items.add(0, menuItem(name) {
            onAction {
                undo()
                undoButton.items.getOrNull(undoButton.items.indexOf(this@menuItem) - 1)?.fire()
                undoButton.items -= this@menuItem
            }
        })
    }

    private fun togglePrintMode(on: Boolean, printStylesheet: String) = when {
        on -> {
            root.children -= toolbar1
            root.children -= toolbar2
            recordTable.stylesheets += printStylesheet
        }
        else -> {
            root.children.add(0, toolbar2)
            root.children.add(0, toolbar1)
            recordTable.stylesheets -= printStylesheet
        }
    }
}