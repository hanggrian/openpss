package com.wijayaprinting.javafx.controller

import com.wijayaprinting.javafx.BuildConfig
import com.wijayaprinting.javafx.R
import com.wijayaprinting.javafx.App
import com.wijayaprinting.javafx.control.EmployeeListView
import com.wijayaprinting.javafx.control.field.FileField
import com.wijayaprinting.javafx.data.Employee
import com.wijayaprinting.javafx.dialog.ShiftDialog
import com.wijayaprinting.javafx.layout.EmployeeTitledPane
import com.wijayaprinting.javafx.reader.Reader
import com.wijayaprinting.javafx.utils.getString
import com.wijayaprinting.javafx.utils.resources
import com.wijayaprinting.javafx.utils.safeTransaction
import com.wijayaprinting.mysql.dao.Shift
import io.reactivex.Observable
import io.reactivex.rxjavafx.schedulers.JavaFxScheduler
import io.reactivex.schedulers.Schedulers
import javafx.fxml.FXML
import javafx.fxml.FXMLLoader
import javafx.scene.Scene
import javafx.scene.control.*
import javafx.scene.image.Image
import javafx.scene.layout.FlowPane
import javafx.stage.FileChooser
import javafx.stage.Stage
import kotfx.bindings.and
import kotfx.bindings.isEmpty
import kotfx.collections.toObservableList
import kotfx.dialogs.*
import kotfx.runLater
import java.io.File
import java.io.File.separator

/**
 * @author Hendra Anggrian (hendraanggrian@gmail.com)
 */
class AttendanceController {

    @FXML lateinit var shiftListView: ListView<Shift>
    @FXML lateinit var fileLabel: Label
    @FXML lateinit var fileField: FileField
    @FXML lateinit var readerLabel: Label
    @FXML lateinit var readerChoiceBox: ChoiceBox<Reader>
    @FXML lateinit var mergeCheckBox: CheckBox
    @FXML lateinit var clearButton: Button
    @FXML lateinit var readButton: Button
    @FXML lateinit var processButton: Button
    @FXML lateinit var scrollPane: ScrollPane
    @FXML lateinit var flowPane: FlowPane

    @FXML
    fun initialize() = runLater {
        readerChoiceBox.items = Reader.listAll()
        if (readerChoiceBox.items.isNotEmpty()) readerChoiceBox.selectionModel.select(0)

        safeTransaction { shiftListView.items = Shift.all().toList().toObservableList() }

        clearButton.disableProperty().bind(fileField.textProperty().isEmpty and flowPane.children.isEmpty)
        readButton.disableProperty().bind(fileField.validProperty)
        processButton.disableProperty().bind(flowPane.children.isEmpty)
        flowPane.prefWrapLengthProperty().bind(fileField.scene.widthProperty())

        if (BuildConfig.DEBUG) {
            fileField.text = "${System.getProperty("user.home")}${separator}Documents${separator}GitHub${separator}${BuildConfig.ARTIFACT}${separator}sample${separator}2017-9-15.xlsx"
        }
    }

    @FXML
    fun addShiftMenuItemOnAction() = ShiftDialog()
            .showAndWait()
            .ifPresent { (mStart, mEnd, mRecess) ->
                safeTransaction {
                    Shift.new {
                        startTime = mStart
                        endTime = mEnd
                        recess = mRecess
                    }
                }
                // refreshShift()
            }

    @FXML
    fun deleteShiftMenuItemOnAction() {
        val shift = shiftListView.selectionModel.selectedItem
        if (shift == null) warningAlert(getString(R.string.error_no_selection)).show()
        else confirmAlert(shiftListView.selectionModel.selectedItem.toString(), getString(R.string.delete))
                .showAndWait()
                .filter { it == ButtonType.OK }
                .ifPresent {
                    safeTransaction { shift.delete() }
                    // refreshShift()
                }
    }

    @FXML
    fun browseButtonOnAction() = fileChooser(FileChooser.ExtensionFilter("XLSX file", "*.xlsx"))
            .showOpenDialog(fileField.scene.window)
            ?.let { fileField.text = it.absolutePath }

    @FXML
    fun clearButtonOnAction() {
        fileField.clear()
        flowPane.children.clear()
    }

    @FXML
    fun readButtonOnAction() {
        val dialog = infoAlert(getString(R.string.please_wait), getString(R.string.please_wait_content)) { buttonTypes.clear() }
        dialog.show()
        flowPane.children.clear()
        Observable
                .create<Employee> { emitter ->
                    try {
                        val employees = readerChoiceBox.selectionModel.selectedItem.read(File(fileField.text))
                        when (BuildConfig.DEBUG) {
                            true -> employees.filter { it.name == "Yanti" || it.name == "Yoyo" }.toMutableList()
                            else -> employees
                        }.forEach {
                            if (mergeCheckBox.isSelected) it.mergeDuplicates()
                            emitter.onNext(it)
                        }
                    } catch (e: Exception) {
                        emitter.onError(e)
                    }
                    emitter.onComplete()
                }
                .subscribeOn(Schedulers.io())
                .observeOn(JavaFxScheduler.platform())
                .subscribe({ employee ->
                    val list = EmployeeListView(employee)
                    val pane = EmployeeTitledPane(employee.toString(), shiftListView.items, list)
                    flowPane.children.add(pane)
                }, { e ->
                    e.message ?: getString(R.string.error_unknown).let { errorAlert(it).showAndWait() }
                }, {
                    dialog.dialogPane.buttonTypes.add(ButtonType.OK) // apparently alert won't close without a button
                    dialog.close()
                })
    }

    @FXML
    fun processButtonOnAction() {
        val set = mutableSetOf<Employee>()
        for (pane in flowPane.children) {
            val employee = (pane as EmployeeTitledPane).employee
            when {
                employee.shift.value == null -> {
                    warningAlert(pane.employee.name, getString(R.string.error_employee_noshift)).show()
                    return
                }
                employee.daily.value <= 0 || employee.overtimeHourly.value <= 0 -> {
                    warningAlert(pane.employee.name, getString(R.string.error_employee_incomplete)).show()
                    return
                }
                employee.attendances.size % 2 != 0 -> {
                    warningAlert(pane.employee.name, getString(R.string.error_employee_odd)).show()
                    return
                }
                else -> {
                    employee.saveWage()
                    set.add(employee)
                }
            }
        }
        if (set.isNotEmpty()) {
            val minSize = Pair(960.0, 640.0)
            Stage().apply {
                scene = Scene(FXMLLoader.load(App::class.java.getResource(R.fxml.layout_record), resources), minSize.first, minSize.second)
                icons.add(Image(R.png.ic_launcher))
                title = "${getString(R.string.app_name)} ${BuildConfig.VERSION} - ${getString(R.string.record)}"
                minWidth = minSize.first
                minHeight = minSize.second
            }.showAndWait()
        }
    }
}