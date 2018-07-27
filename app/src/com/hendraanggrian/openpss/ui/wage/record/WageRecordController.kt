package com.hendraanggrian.openpss.ui.wage.record

import com.hendraanggrian.openpss.R
import com.hendraanggrian.openpss.control.UncollapsibleTreeItem
import com.hendraanggrian.openpss.control.popover.DatePopover
import com.hendraanggrian.openpss.io.WageContentDirectory
import com.hendraanggrian.openpss.io.WageContentFile
import com.hendraanggrian.openpss.layout.TimeBox
import com.hendraanggrian.openpss.ui.Controller
import com.hendraanggrian.openpss.ui.wage.Attendee
import com.hendraanggrian.openpss.ui.wage.record.Record.Companion.getDummy
import com.hendraanggrian.openpss.util.PATTERN_DATE
import com.hendraanggrian.openpss.util.PATTERN_DATETIME
import com.hendraanggrian.openpss.util.PATTERN_TIME
import com.hendraanggrian.openpss.util.bold
import com.hendraanggrian.openpss.util.concatenate
import com.hendraanggrian.openpss.util.currencyConverter
import com.hendraanggrian.openpss.util.getResource
import com.hendraanggrian.openpss.util.getStyle
import com.hendraanggrian.openpss.util.numberConverter
import com.hendraanggrian.openpss.util.openFile
import com.hendraanggrian.openpss.util.stringCell
import com.sun.javafx.scene.control.skin.TreeTableViewSkin
import com.sun.javafx.scene.control.skin.VirtualFlow
import javafx.beans.value.ObservableValue
import javafx.fxml.FXML
import javafx.scene.control.Button
import javafx.scene.control.ButtonBar.ButtonData.CANCEL_CLOSE
import javafx.scene.control.SelectionMode.MULTIPLE
import javafx.scene.control.SplitMenuButton
import javafx.scene.control.TreeItem
import javafx.scene.control.TreeTableColumn
import javafx.scene.control.TreeTableView
import javafx.scene.layout.HBox
import javafx.scene.layout.VBox
import javafx.stage.Stage
import javafxx.application.later
import javafxx.beans.binding.booleanBindingOf
import javafxx.beans.binding.stringBindingOf
import javafxx.beans.value.or
import javafxx.collections.isEmpty
import javafxx.coroutines.onAction
import javafxx.embed.swing.toBufferedImage
import javafxx.layouts.label
import javafxx.layouts.menuItem
import javafxx.listeners.cellFactory
import javafxx.scene.control.customButton
import javafxx.scene.control.styledInfoAlert
import javafxx.scene.snapshot
import java.awt.image.BufferedImage
import java.net.URL
import java.util.ResourceBundle
import javax.imageio.ImageIO

@Suppress("UNCHECKED_CAST")
class WageRecordController : Controller() {

    companion object {
        const val EXTRA_ATTENDEES = "EXTRA_ATTENDEES"
    }

    @FXML lateinit var root: VBox
    @FXML lateinit var navigationBox: HBox
    @FXML lateinit var undoButton: SplitMenuButton
    @FXML lateinit var timeBox: TimeBox
    @FXML lateinit var lockStartButton: Button
    @FXML lateinit var lockEndButton: Button
    @FXML lateinit var disableDailyIncomeButton: Button
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
                            if (treeTableRow.treeItem?.value?.isTotal() == true) {
                                font = bold()
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
            (root.scene.window as Stage).titleProperty().bind(
                stringBindingOf(*records.filter { it.isChild() }.map { it.totalProperty }.toTypedArray()) {
                    "${getString(R.string.record)} - ${currencyConverter.toString(records
                        .filter { it.isTotal() }
                        .sumByDouble { it.totalProperty.value })}"
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

    @FXML fun disableDailyIncome() = DatePopover(this, R.string.disable_daily_income)
        .showAt(disableDailyIncomeButton) { date ->
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

    @FXML fun screenshot() {
        val images = mutableListOf<BufferedImage>()
        val stylesheet = getResource(R.style.treetableview_print).toExternalForm()
        togglePrintMode(true, stylesheet)
        recordTable.scrollTo(0)
        val flow = (recordTable.skin as TreeTableViewSkin<*>).children[1] as VirtualFlow<*>
        var i = 0
        do {
            images += recordTable.snapshot().toBufferedImage()
            recordTable.scrollTo(flow.lastVisibleCell.index)
            i++
        } while (flow.lastVisibleCell.index + 1 <
            recordTable.root.children.size + recordTable.root.children.sumBy { it.children.size })
        togglePrintMode(false, stylesheet)
        ImageIO.write(images.concatenate(), "png", WageContentFile)
        styledInfoAlert(getStyle(R.style.openpss), getString(R.string.screenshot_finished)) {
            customButton(getString(R.string.open_folder), CANCEL_CLOSE)
        }.showAndWait()
            .filter { it.buttonData == CANCEL_CLOSE }
            .ifPresent { openFile(WageContentDirectory) }
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
            root.children -= navigationBox
            recordTable.stylesheets += printStylesheet
        }
        else -> {
            root.children.add(0, navigationBox)
            recordTable.stylesheets -= printStylesheet
        }
    }
}