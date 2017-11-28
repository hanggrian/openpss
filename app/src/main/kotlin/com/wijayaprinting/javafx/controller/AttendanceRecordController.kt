package com.wijayaprinting.javafx.controller

import com.wijayaprinting.javafx.data.*
import com.wijayaprinting.javafx.scene.layout.TimeBox
import javafx.application.Platform.runLater
import javafx.beans.property.ReadOnlyObjectWrapper
import javafx.beans.value.ObservableValue
import javafx.fxml.FXML
import javafx.print.PrinterJob
import javafx.scene.control.Button
import javafx.scene.control.SelectionMode.MULTIPLE
import javafx.scene.control.TreeItem
import javafx.scene.control.TreeTableColumn
import javafx.scene.control.TreeTableView
import javafx.scene.text.Text
import javafx.scene.text.TextFlow
import kotfx.bindings.booleanBindingOf
import kotfx.bindings.stringBindingOf
import java.lang.Character.isDigit
import java.text.NumberFormat.getCurrencyInstance

class AttendanceRecordController {

    companion object {
        lateinit var EMPLOYEES: Set<Employee>
    }

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
    @Suppress("UNCHECKED_CAST")
    fun initialize() = runLater {
        arrayOf(lockStartButton, lockEndButton).forEach {
            it.disableProperty().bind(booleanBindingOf(treeTableView.selectionModel.selectedIndexProperty()) {
                if (treeTableView.selectionModel.selectedIndex == -1) true
                else treeTableView.selectionModel.selectedItems.map { it.value.type }.any { it != Record.TYPE_CHILD }
            })
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

        employeeColumn.setCellValueFactory { ReadOnlyObjectWrapper(it.value.value.employee) }
        startColumn.setCellValueFactory { it.value.value.startString }
        endColumn.setCellValueFactory { it.value.value.endString }
        dailyColumn.setCellValueFactory { it.value.value.daily as ObservableValue<Double> }
        dailyIncomeColumn.setCellValueFactory { it.value.value.dailyIncome as ObservableValue<Double> }
        overtimeColumn.setCellValueFactory { it.value.value.overtime as ObservableValue<Double> }
        overtimeIncomeColumn.setCellValueFactory { it.value.value.overtimeIncome as ObservableValue<Double> }
        totalColumn.setCellValueFactory { it.value.value.total as ObservableValue<Double> }

        totalText.textProperty().bind(stringBindingOf(*treeTableView.root.children.flatMap { it.children }.map { it.value.total }.toTypedArray()) {
            getCurrencyInstance().format(treeTableView.root.children.flatMap { it.children }
                    .map { it.value.total.value }
                    .sum())
                    .let { it.substring(it.indexOf(it.toCharArray().first { isDigit(it) })) }
        })
    }

    @FXML
    fun lockStartOnAction() = treeTableView.selectionModel.selectedItems
            .map { it.value }
            .forEach { if (it.start.value.toLocalTime().isBefore(timeBox.value)) it.start.set(it.cloneStart(timeBox.value)) }

    @FXML
    fun lockEndOnAction() = treeTableView.selectionModel.selectedItems
            .map { it.value }
            .forEach { if (it.end.value.toLocalTime().isAfter(timeBox.value)) it.end.set(it.cloneEnd(timeBox.value)) }

    @FXML
    fun printOnAction() {
        val printerJob = PrinterJob.createPrinterJob()
        if (printerJob.showPrintDialog(treeTableView.scene.window) && printerJob.printPage(treeTableView)) printerJob.endJob()
    }
}