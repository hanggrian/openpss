package com.wijayaprinting.manager.controller

import com.wijayaprinting.manager.data.*
import com.wijayaprinting.manager.scene.layout.TimeBox
import javafx.application.Platform.runLater
import javafx.beans.value.ObservableValue
import javafx.collections.ObservableSet
import javafx.fxml.FXML
import javafx.print.PrinterJob.createPrinterJob
import javafx.scene.control.Button
import javafx.scene.control.SelectionMode.MULTIPLE
import javafx.scene.control.TreeItem
import javafx.scene.control.TreeTableColumn
import javafx.scene.control.TreeTableView
import javafx.scene.text.Text
import javafx.scene.text.TextFlow
import kotfx.bind
import kotfx.bindings.booleanBindingOf
import kotfx.bindings.isEmpty
import kotfx.bindings.stringBindingOf
import kotfx.collections.mutableObservableSetOf
import kotfx.toProperty
import java.lang.Character.isDigit
import java.text.NumberFormat.getCurrencyInstance

class AttendanceRecordController {

    companion object {
        lateinit var EMPLOYEES: Set<Employee>
    }

    @FXML lateinit var undoButton: Button
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

    private var undos: ObservableSet<() -> Unit> = mutableObservableSetOf()

    @FXML
    @Suppress("UNCHECKED_CAST")
    fun initialize() = runLater {
        undoButton.disableProperty() bind undos.isEmpty
        arrayOf(lockStartButton, lockEndButton).forEach {
            it.disableProperty() bind booleanBindingOf(treeTableView.selectionModel.selectedIndexProperty()) {
                if (treeTableView.selectionModel.selectedIndex == -1) true
                else treeTableView.selectionModel.selectedItems.map { it.value.type }.any { it != Record.TYPE_CHILD }
            }
        }

        treeTableView.selectionModel.selectionMode = MULTIPLE
        treeTableView.root = TreeItem(Record.ROOT) // dummy for invisible root
        treeTableView.isShowRoot = false
        EMPLOYEES.forEach { employee ->
            val node = employee.toNodeRecord()
            val childs = employee.toChildRecords()
            val total = employee.toTotalRecords(childs)
            treeTableView.root.children.add(TreeItem(node).apply {
                isExpanded = true
                children.addAll(*childs.map { TreeItem(it) }.toTypedArray(), TreeItem(total))
            })
        }

        employeeColumn.setCellValueFactory { it.value.value.employee.toProperty() }
        startColumn.setCellValueFactory { it.value.value.startString }
        endColumn.setCellValueFactory { it.value.value.endString }
        dailyColumn.setCellValueFactory { it.value.value.daily as ObservableValue<Double> }
        dailyIncomeColumn.setCellValueFactory { it.value.value.dailyIncome as ObservableValue<Double> }
        overtimeColumn.setCellValueFactory { it.value.value.overtime as ObservableValue<Double> }
        overtimeIncomeColumn.setCellValueFactory { it.value.value.overtimeIncome as ObservableValue<Double> }
        totalColumn.setCellValueFactory { it.value.value.total as ObservableValue<Double> }

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
    fun undoOnAction() {
        undos.forEach { it() }
        undos.clear()
    }

    @FXML
    fun lockStartOnAction() {
        undos.clear()
        treeTableView.selectionModel.selectedItems
                .map { it.value }
                .forEach { record ->
                    val initial = record.start.value
                    if (initial.toLocalTime() < timeBox.value) {
                        record.start.set(record.cloneStart(timeBox.value))
                        undos.add { record.start.set(initial) }
                    }
                }
    }

    @FXML
    fun lockEndOnAction() {
        undos.clear()
        treeTableView.selectionModel.selectedItems
                .map { it.value }
                .forEach { record ->
                    val initial = record.end.value
                    if (initial.toLocalTime() > timeBox.value) {
                        record.end.set(record.cloneEnd(timeBox.value))
                        undos.add { record.end.set(initial) }
                    }
                }
    }

    @FXML
    fun printOnAction() {
        val printerJob = createPrinterJob()
        if (printerJob.showPrintDialog(treeTableView.scene.window) && printerJob.printPage(treeTableView)) printerJob.endJob()
    }
}