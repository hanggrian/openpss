package com.wijayaprinting.javafx.controller

import com.wijayaprinting.javafx.data.Employee
import com.wijayaprinting.javafx.data.Record
import com.wijayaprinting.javafx.io.PreferencesFile
import com.wijayaprinting.javafx.scene.control.DoubleField
import com.wijayaprinting.javafx.scene.layout.TimeBox
import com.wijayaprinting.javafx.utils.asJoda
import com.wijayaprinting.javafx.utils.round
import javafx.application.Platform.runLater
import javafx.beans.property.DoubleProperty
import javafx.beans.property.ReadOnlyObjectWrapper
import javafx.beans.property.SimpleDoubleProperty
import javafx.beans.value.ObservableValue
import javafx.fxml.FXML
import javafx.print.PrinterJob
import javafx.scene.control.*
import javafx.scene.control.SelectionMode.MULTIPLE
import javafx.scene.control.cell.TextFieldTreeTableCell
import javafx.scene.text.Text
import javafx.scene.text.TextFlow
import kotfx.bindings.booleanBindingOf
import kotfx.bindings.plus
import kotfx.bindings.stringBindingOf
import kotfx.dialogs.warningAlert
import kotfx.stringConverterOf
import java.time.LocalDate.now

class AttendanceRecordController {

    companion object {
        lateinit var EMPLOYEES: Set<Employee>

        const val AFFECT_DAILY = "dailyToggle"
        const val AFFECT_OVERTIME = "overtimeToggle"
        const val AFFECT_BOTH = "bothToggle"
    }

    @FXML lateinit var dailyToggle: ToggleButton
    @FXML lateinit var overtimeToggle: ToggleButton
    @FXML lateinit var bothToggle: ToggleButton
    @FXML lateinit var picker: DatePicker
    @FXML lateinit var field: DoubleField
    @FXML lateinit var substractButton: Button
    @FXML lateinit var addButton: Button
    @FXML lateinit var timeBox: TimeBox
    @FXML lateinit var lockStartButton: Button
    @FXML lateinit var lockEndButton: Button
    @FXML lateinit var grandTotalFlow: TextFlow

    @FXML lateinit var treeTableView: TreeTableView<Record>
    @FXML lateinit var employeeColumn: TreeTableColumn<Record, Employee>
    @FXML lateinit var startColumn: TreeTableColumn<Record, String>
    @FXML lateinit var endColumn: TreeTableColumn<Record, String>
    @FXML lateinit var dailyColumn: TreeTableColumn<Record, Double>
    @FXML lateinit var dailyIncomeColumn: TreeTableColumn<Record, Double>
    @FXML lateinit var overtimeColumn: TreeTableColumn<Record, Double>
    @FXML lateinit var overtimeIncomeColumn: TreeTableColumn<Record, Double>
    @FXML lateinit var totalColumn: TreeTableColumn<Record, Double>

    private val toggleGroup = ToggleGroup()

    @FXML
    @Suppress("UNCHECKED_CAST")
    fun initialize() = runLater {
        dailyToggle.toggleGroup = toggleGroup
        dailyToggle.userData = AFFECT_DAILY
        overtimeToggle.toggleGroup = toggleGroup
        overtimeToggle.userData = AFFECT_OVERTIME
        bothToggle.toggleGroup = toggleGroup
        bothToggle.userData = AFFECT_BOTH
        toggleGroup.selectToggle(when (PreferencesFile[PreferencesFile.RECORD_AFFECTION].value) {
            AFFECT_DAILY -> dailyToggle
            AFFECT_OVERTIME -> overtimeToggle
            else -> bothToggle
        })
        toggleGroup.selectedToggleProperty().addListener { _, oldValue, newValue ->
            if (newValue == null) toggleGroup.selectToggle(oldValue)
            else PreferencesFile.apply { get(PreferencesFile.RECORD_AFFECTION).set(newValue.userData as String) }.save()
        }

        picker.value = now()

        arrayOf(substractButton, addButton, lockStartButton, lockEndButton).forEach {
            it.disableProperty().bind(booleanBindingOf(treeTableView.selectionModel.selectedIndexProperty()) {
                if (treeTableView.selectionModel.selectedIndex == -1) true
                else treeTableView.selectionModel.selectedItems.map { it.value.type }.any { it != Record.TYPE_CHILD }
            })
        }

        val totals = mutableListOf<DoubleProperty>()
        treeTableView.selectionModel.selectionMode = MULTIPLE
        treeTableView.root = TreeItem(Record.ROOT) // dummy for invisible root
        treeTableView.isShowRoot = false
        EMPLOYEES.forEach { employee ->
            val node = employee.toNodeRecord()
            val childs = employee.toChildRecords()
            val total = employee.toTotalRecords(childs)
            totals.add(total.total)
            treeTableView.root.children.add(TreeItem(node).apply {
                isExpanded = true
                children.addAll(*childs.map { TreeItem(it) }.toTypedArray(), TreeItem(total))
            })
        }

        employeeColumn.setCellValueFactory { ReadOnlyObjectWrapper(it.value.value.employee) }
        startColumn.setCellValueFactory { it.value.value.startString }
        endColumn.setCellValueFactory { it.value.value.endString }
        dailyColumn.setCellValueFactory { it.value.value.daily as ObservableValue<Double> }
        dailyColumn.cellFactory = TextFieldTreeTableCell.forTreeTableColumn<Record, Double>(stringConverterOf { it.toDouble() })
        dailyColumn.setOnEditStart {
            if (it.rowValue.value.type != Record.TYPE_CHILD) {
                it.consume()
                warningAlert("Can't edit!").show()
            }
        }
        dailyColumn.setOnEditCommit { it.rowValue.value.daily.set(it.newValue) }
        dailyIncomeColumn.setCellValueFactory { it.value.value.dailyIncome as ObservableValue<Double> }
        overtimeColumn.setCellValueFactory { it.value.value.overtime as ObservableValue<Double> }
        overtimeColumn.cellFactory = TextFieldTreeTableCell.forTreeTableColumn<Record, Double>(stringConverterOf { it.toDouble() })
        overtimeColumn.setOnEditStart {
            if (it.rowValue.value.type != Record.TYPE_CHILD) {
                it.consume()
                warningAlert("Can't edit!").show()
            }
        }
        overtimeColumn.setOnEditCommit { it.rowValue.value.overtime.set(it.newValue) }
        overtimeIncomeColumn.setCellValueFactory { it.value.value.overtimeIncome as ObservableValue<Double> }
        totalColumn.setCellValueFactory { it.value.value.total as ObservableValue<Double> }

        val first = totals.firstOrNull() ?: return@runLater
        var binding = first + SimpleDoubleProperty(0.0)
        (1 until totals.size).forEach { binding += totals[it] }
        grandTotalFlow.children.add(Text().apply { textProperty().bind(stringBindingOf(binding) { binding.get().toString() }) })
    }

    @FXML
    fun substractOnAction() = treeTableView.selectionModel.selectedItems
            .map { it.value }
            .forEach { it.affect { value -> (value - field.value).let { (if (it < 0) 0.0 else it).round } } }

    @FXML
    fun addOnAction() = treeTableView.selectionModel.selectedItems
            .map { it.value }
            .forEach { it.affect { value -> (value + field.value).round } }

    @FXML
    fun zeroOnAction() = treeTableView.root.children
            .flatMap { it.children }
            .map { it.value }
            .filter { it.type == Record.TYPE_CHILD && it.start.value.toLocalDate() == picker.value.asJoda() }
            .forEach { it.affect({ 0.0 }) }

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
        if (printerJob.showPrintDialog(treeTableView.scene.window) && printerJob.printPage(treeTableView)) {
            printerJob.endJob()
        }
    }

    private fun Record.affect(affection: (Double) -> Double) = when (toggleGroup.selectedToggle) {
        dailyToggle -> daily.set(affection(daily.value))
        overtimeToggle -> overtime.set(affection(overtime.value))
        else -> {
            daily.set(affection(daily.value))
            overtime.set(affection(overtime.value))
        }
    }
}