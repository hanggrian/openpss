package com.wijayaprinting.manager.controller

import com.wijayaprinting.data.PATTERN_DATETIME
import com.wijayaprinting.data.PATTERN_TIME
import com.wijayaprinting.manager.R
import com.wijayaprinting.manager.data.*
import com.wijayaprinting.manager.scene.layout.TimeBox
import javafx.collections.ObservableSet
import javafx.fxml.FXML
import javafx.print.PrinterJob.createPrinterJob
import javafx.scene.control.*
import javafx.scene.control.SelectionMode.MULTIPLE
import javafx.scene.text.Text
import javafx.scene.text.TextFlow
import kotfx.*
import java.lang.Character.isDigit
import java.text.NumberFormat.getCurrencyInstance

class AttendanceRecordController : Controller() {

    lateinit var employees: Set<Employee>

    @FXML lateinit var undoButton: SplitMenuButton
    @FXML lateinit var timeBox: TimeBox
    @FXML lateinit var lockStartButton: Button
    @FXML lateinit var lockEndButton: Button
    @FXML lateinit var grandTotalFlow: TextFlow
    @FXML lateinit var totalText: Text

    @FXML lateinit var treeTableView: TreeTableView<Record>
    @FXML lateinit var employeeColumn: TreeTableColumn<Record, Employee>
    @FXML lateinit var startColumn: TreeTableColumn<Record, String>
    @FXML lateinit var endColumn: TreeTableColumn<Record, String>
    @FXML lateinit var dailyColumn: TreeTableColumn<Record, Double>
    @FXML lateinit var dailyIncomeColumn: TreeTableColumn<Record, Double>
    @FXML lateinit var overtimeColumn: TreeTableColumn<Record, Double>
    @FXML lateinit var overtimeIncomeColumn: TreeTableColumn<Record, Double>
    @FXML lateinit var totalColumn: TreeTableColumn<Record, Double>

    @FXML
    fun initialize() = runFX {
        undoButton.disableProperty() bind undoButton.items.isEmpty
        arrayOf(lockStartButton, lockEndButton).forEach {
            it.disableProperty() bind booleanBindingOf(treeTableView.selectionModel.selectedIndexProperty()) {
                if (treeTableView.selectionModel.selectedIndex == -1) true
                else treeTableView.selectionModel.selectedItems.map { it.value.type }.any { it != Record.TYPE_CHILD }
            }
        }

        treeTableView.selectionModel.selectionMode = MULTIPLE
        treeTableView.root = TreeItem(Record.ROOT) // dummy for invisible root
        treeTableView.isShowRoot = false
        employees.forEach { employee ->
            val node = employee.toNodeRecord()
            val childs = employee.toChildRecords()
            val total = employee.toTotalRecords(childs)
            treeTableView.root.children.add(TreeItem(node).apply {
                isExpanded = true
                children.addAll(*childs.map { TreeItem(it) }.toTypedArray(), TreeItem(total))
            })
        }

        employeeColumn.setCellValueFactory { it.value.value.employee.asProperty() }
        startColumn.setCellValueFactory { it.value.value.startString }
        endColumn.setCellValueFactory { it.value.value.endString }
        dailyColumn.setCellValueFactory { it.value.value.daily.asObservable() }
        dailyIncomeColumn.setCellValueFactory { it.value.value.dailyIncome.asObservable() }
        overtimeColumn.setCellValueFactory { it.value.value.overtime.asObservable() }
        overtimeIncomeColumn.setCellValueFactory { it.value.value.overtimeIncome.asObservable() }
        totalColumn.setCellValueFactory { it.value.value.total.asObservable() }

        totalText.textProperty() bind stringBindingOf(*treeTableView.root.children.flatMap { it.children }.filter { it.value.type == Record.TYPE_CHILD }.map { it.value.total }.toTypedArray()) {
            getCurrencyInstance().format(treeTableView.root.children
                    .flatMap { it.children }
                    .filter { it.value.type == Record.TYPE_TOTAL }
                    .map { it.value.total.value }
                    .sum())
                    .let { s -> s.substring(s.indexOf(s.toCharArray().first { isDigit(it) })) }
        }
    }

    @FXML
    fun undoOnAction() = undoButton.items[0].fire()

    @FXML
    fun lockStartOnAction() {
        var name: String? = null
        val undos: ObservableSet<() -> Unit> = mutableObservableSetOf()
        treeTableView.selectionModel.selectedItems
                .map { it.value }
                .forEach { record ->
                    val initial = record.start.value
                    if (initial.toLocalTime() < timeBox.value) {
                        record.start.set(record.cloneStart(timeBox.value))
                        name = if (name == null) "${record.actualEmployee.name} ${initial.toString(PATTERN_DATETIME)} -> ${timeBox.value.toString(PATTERN_TIME)}" else getString(R.string.multiple_actions)
                        undos.add { record.start.set(initial) }
                    }
                }
        if (name != null && undos.isNotEmpty()) undoButton.items.add(0, menuItem {
            text = name
            setOnAction {
                undos.forEach { it() }
                undoButton.items.getOrNull(undoButton.items.indexOf(this) - 1)?.fire()
                undoButton.items.remove(this)
            }
        })
    }

    @FXML
    fun lockEndOnAction() {
        var name: String? = null
        val undos: ObservableSet<() -> Unit> = mutableObservableSetOf()
        treeTableView.selectionModel.selectedItems
                .map { it.value }
                .forEach { record ->
                    val initial = record.end.value
                    if (initial.toLocalTime() > timeBox.value) {
                        record.end.set(record.cloneEnd(timeBox.value))
                        name = if (name == null) "${record.actualEmployee.name} ${initial.toString(PATTERN_DATETIME)} -> ${timeBox.value.toString(PATTERN_TIME)}" else getString(R.string.multiple_actions)
                        undos.add { record.end.set(initial) }
                    }
                }
        if (name != null && undos.isNotEmpty()) undoButton.items.add(0, menuItem {
            text = name
            setOnAction {
                undos.forEach { it() }
                undoButton.items.getOrNull(undoButton.items.indexOf(this) - 1)?.fire()
                undoButton.items.remove(this)
            }
        })
    }

    @FXML
    fun printOnAction() {
        val printerJob = createPrinterJob()
        if (printerJob.showPrintDialog(treeTableView.scene.window) && printerJob.printPage(treeTableView)) printerJob.endJob()
    }
}