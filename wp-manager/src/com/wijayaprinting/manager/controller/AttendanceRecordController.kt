package com.wijayaprinting.manager.controller

import com.wijayaprinting.PATTERN_DATE
import com.wijayaprinting.PATTERN_DATETIME
import com.wijayaprinting.PATTERN_TIME
import com.wijayaprinting.manager.R
import com.wijayaprinting.manager.data.AttendanceRecord
import com.wijayaprinting.manager.data.Attendee
import com.wijayaprinting.manager.data.Undoable
import com.wijayaprinting.manager.dialog.DateDialog
import com.wijayaprinting.manager.scene.layout.TimeBox
import com.wijayaprinting.manager.utils.toChildRecords
import com.wijayaprinting.manager.utils.toNodeRecord
import com.wijayaprinting.manager.utils.toRootRecord
import com.wijayaprinting.manager.utils.toTotalRecords
import javafx.fxml.FXML
import javafx.print.Printer.defaultPrinterProperty
import javafx.scene.control.*
import javafx.scene.control.SelectionMode.MULTIPLE
import javafx.scene.text.Text
import javafx.scene.text.TextFlow
import kotfx.*
import java.text.NumberFormat

class AttendanceRecordController : Controller() {

    @FXML lateinit var undoButton: SplitMenuButton
    @FXML lateinit var timeBox: TimeBox
    @FXML lateinit var lockStartButton: Button
    @FXML lateinit var lockEndButton: Button
    @FXML lateinit var emptyButton: Button
    @FXML lateinit var printButton: Button
    @FXML lateinit var grandTotalFlow: TextFlow
    @FXML lateinit var totalText: Text

    @FXML lateinit var treeTableView: TreeTableView<AttendanceRecord>
    @FXML lateinit var nameColumn: TreeTableColumn<AttendanceRecord, String>
    @FXML lateinit var startColumn: TreeTableColumn<AttendanceRecord, String>
    @FXML lateinit var endColumn: TreeTableColumn<AttendanceRecord, String>
    @FXML lateinit var dailyColumn: TreeTableColumn<AttendanceRecord, Double>
    @FXML lateinit var dailyIncomeColumn: TreeTableColumn<AttendanceRecord, Double>
    @FXML lateinit var overtimeColumn: TreeTableColumn<AttendanceRecord, Double>
    @FXML lateinit var overtimeIncomeColumn: TreeTableColumn<AttendanceRecord, Double>
    @FXML lateinit var totalColumn: TreeTableColumn<AttendanceRecord, Double>

    @FXML
    fun initialize() {
        undoButton.disableProperty() bind undoButton.items.isEmpty
        arrayOf(lockStartButton, lockEndButton).forEach {
            it.disableProperty() bind (treeTableView.selectionModel.selectedItemProperty().isNull or booleanBindingOf(treeTableView.selectionModel.selectedItemProperty()) {
                treeTableView.selectionModel.selectedItems?.map { it.value.type }?.any { it != AttendanceRecord.TYPE_CHILD } ?: true
            })
        }
        printButton.disableProperty() bind defaultPrinterProperty().isNull

        treeTableView.selectionModel.selectionMode = MULTIPLE
        treeTableView.root = TreeItem(Attendee.getDummy(this).toRootRecord())
        treeTableView.isShowRoot = false

        nameColumn.setCellValueFactory { it.value.value.displayedName }
        startColumn.setCellValueFactory { it.value.value.displayedStart }
        endColumn.setCellValueFactory { it.value.value.displayedEnd }
        dailyColumn.setCellValueFactory { it.value.value.dailyProperty.asObservable() }
        dailyIncomeColumn.setCellValueFactory { it.value.value.dailyIncomeProperty.asObservable() }
        overtimeColumn.setCellValueFactory { it.value.value.overtimeProperty.asObservable() }
        overtimeIncomeColumn.setCellValueFactory { it.value.value.overtimeIncomeProperty.asObservable() }
        totalColumn.setCellValueFactory { it.value.value.totalProperty.asObservable() }

        runFX {
            getExtra<Set<Attendee>>().forEach { attendee ->
                val node = attendee.toNodeRecord()
                val childs = attendee.toChildRecords()
                val total = attendee.toTotalRecords(childs)
                treeTableView.root.children.add(TreeItem(node).apply {
                    isExpanded = true
                    children.addAll(*childs.map { TreeItem(it) }.toTypedArray(), TreeItem(total))
                })
            }
            totalText.textProperty() bind stringBindingOf(*records.filter { it.type == AttendanceRecord.TYPE_CHILD }.map { it.totalProperty }.toTypedArray()) {
                NumberFormat.getCurrencyInstance().format(records
                        .filter { it.type == AttendanceRecord.TYPE_TOTAL }
                        .map { it.totalProperty.value }
                        .sum())
                        .let { s -> s.substring(s.indexOf(s.toCharArray().first { Character.isDigit(it) })) }
            }
        }
    }

    @FXML fun undoOnAction() = undoButton.items[0].fire()

    @FXML
    fun lockStartOnAction() {
        val undoable = Undoable()
        treeTableView.selectionModel.selectedItems
                .map { it.value }
                .forEach { record ->
                    val initial = record.startProperty.value
                    if (initial.toLocalTime() < timeBox.value) {
                        record.startProperty.set(record.cloneStart(timeBox.value))
                        undoable.name = if (undoable.name == null) "${record.attendee.name} ${initial.toString(PATTERN_DATETIME)} -> ${timeBox.value.toString(PATTERN_TIME)}" else getString(R.string.multiple_lock_start_time)
                        undoable.addAction { record.startProperty.set(initial) }
                    }
                }
        undoable.append()
    }

    @FXML
    fun lockEndOnAction() {
        val undoable = Undoable()
        treeTableView.selectionModel.selectedItems
                .map { it.value }
                .forEach { record ->
                    val initial = record.endProperty.value
                    if (initial.toLocalTime() > timeBox.value) {
                        record.endProperty.set(record.cloneEnd(timeBox.value))
                        undoable.name = if (undoable.name == null) "${record.attendee.name} ${initial.toString(PATTERN_DATETIME)} -> ${timeBox.value.toString(PATTERN_TIME)}" else getString(R.string.multiple_lock_end_time)
                        undoable.addAction { record.endProperty.set(initial) }
                    }
                }
        undoable.append()
    }

    @FXML
    fun emptyOnAction() = DateDialog(this, getString(R.string.empty_daily_income))
            .showAndWait()
            .ifPresent { date ->
                val undoable = Undoable()
                records.filter { it.startProperty.value.toLocalDate() == date }
                        .forEach { record ->
                            val initial = record.dailyEmptyProperty.value
                            record.dailyEmptyProperty.set(!initial)
                            if (undoable.name == null) undoable.name = "${getString(R.string.daily_emptied)} ${record.startProperty.value.toString(PATTERN_DATE)}"
                            undoable.addAction { record.dailyEmptyProperty.set(initial) }
                        }
                undoable.append()
            }

    @FXML
    fun printOnAction() {
        //TreeTablePrinter.print(treeTableView, null)
        //val printerJob = createPrinterJob()
        //if (printerJob.showPrintDialog(treeTableView.scene.window) && printerJob.printPage(treeTableView)) printerJob.endJob()
    }

    private val records: List<AttendanceRecord> get() = treeTableView.root.children.flatMap { it.children }.map { it.value }

    private fun Undoable.append() {
        if (isValid) undoButton.items.add(0, menuItem {
            text = name
            setOnAction {
                undo()
                undoButton.items.getOrNull(undoButton.items.indexOf(this) - 1)?.fire()
                undoButton.items.remove(this)
            }
        })
    }
}