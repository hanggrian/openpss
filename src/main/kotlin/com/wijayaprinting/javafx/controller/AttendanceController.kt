package com.wijayaprinting.javafx.controller

import com.wijayaprinting.javafx.BuildConfig
import com.wijayaprinting.javafx.R
import com.wijayaprinting.javafx.WPApp
import com.wijayaprinting.javafx.control.EmployeeListView
import com.wijayaprinting.javafx.data.Employee
import com.wijayaprinting.javafx.dialog.ShiftDialog
import com.wijayaprinting.javafx.layout.EmployeeTitledPane
import com.wijayaprinting.javafx.reader.Reader
import com.wijayaprinting.javafx.utils.getString
import com.wijayaprinting.javafx.utils.resources
import com.wijayaprinting.javafx.utils.safeTransaction
import com.wijayaprinting.mysql.dao.Shift
import com.wijayaprinting.mysql.dao.Shifts
import io.reactivex.Observable
import io.reactivex.rxjavafx.schedulers.JavaFxScheduler
import io.reactivex.schedulers.Schedulers
import javafx.beans.binding.Bindings
import javafx.fxml.FXML
import javafx.fxml.FXMLLoader
import javafx.scene.Scene
import javafx.scene.control.*
import javafx.scene.image.Image
import javafx.scene.image.ImageView
import javafx.scene.layout.FlowPane
import javafx.scene.layout.VBox
import javafx.stage.FileChooser
import javafx.stage.Stage
import kotfx.bindings.booleanBindingOf
import kotfx.bindings.or
import kotfx.collections.mutableObservableListOf
import kotfx.dialogs.*
import kotfx.runLater
import org.jetbrains.exposed.sql.deleteAll
import java.awt.Desktop
import java.io.File
import java.io.File.separator
import java.net.URI

/**
 * @author Hendra Anggrian (hendraanggrian@gmail.com)
 */
class AttendanceController {

    private val shifts = mutableObservableListOf<Shift>()

    private val toggleGroupReader = ToggleGroup()
    @FXML lateinit var menuShift: Menu
    @FXML lateinit var readerBox: ChoiceBox<Reader>
    @FXML lateinit var labelFile: Label
    @FXML lateinit var labelMerge: Label
    @FXML lateinit var textFieldFile: TextField
    @FXML lateinit var checkBoxMerge: CheckBox
    @FXML lateinit var buttonRead: Button
    @FXML lateinit var buttonProcess: Button
    @FXML lateinit var scrollPane: ScrollPane
    @FXML lateinit var flowPane: FlowPane

    private fun refreshShift() {
        // shifts
        val iterator = menuShift.items.iterator()
        while (iterator.hasNext()) {
            val item = iterator.next()
            if (item is SeparatorMenuItem) break
            iterator.remove()
        }
        safeTransaction {
            shifts.clear()
            shifts.addAll(Shift.all())
            menuShift.items.addAll(0, shifts.map { shift ->
                Menu(shift.toString(), null, MenuItem(getString(R.string.delete)).apply {
                    setOnAction({
                        confirmAlert(shift.toString(), getString(R.string.delete))
                                .showAndWait()
                                .filter { it == ButtonType.OK }
                                .ifPresent {
                                    safeTransaction { shift.delete() }
                                    refreshShift()
                                }
                    })
                })
            })
        }
    }

    @FXML
    fun initialize() = runLater {
        readerBox.items = Reader.listAll()
        if (readerBox.items.isNotEmpty()) readerBox.selectionModel.select(0)

        refreshShift()
        buttonRead.disableProperty().bind(textFieldFile.textProperty().isEmpty
                or booleanBindingOf(textFieldFile.textProperty()) { File(textFieldFile.text).let { !it.exists() || !it.isFile || it.extension != "xlsx" } })
        buttonProcess.disableProperty().bind(Bindings.isEmpty(flowPane.children))
        flowPane.prefWrapLengthProperty().bind(textFieldFile.scene.widthProperty())

        if (BuildConfig.DEBUG) {
            textFieldFile.text = "${System.getProperty("user.home")}${separator}Documents${separator}GitHub${separator}${BuildConfig.ARTIFACT}${separator}sample${separator}2017-9-15.xlsx"
        }
    }

    @FXML
    fun menuItemClearOnAction() {
        textFieldFile.clear()
        flowPane.children.clear()
    }

    @FXML
    fun menuItemResetOnAction() = confirmAlert(getString(R.string.are_you_sure), getString(R.string.this_will_reset_employees_saved_salary_and_shifts))
            .showAndWait()
            .filter { it == ButtonType.OK }
            .ifPresent {
                safeTransaction { Shifts.deleteAll() }
                refreshShift()
            }

    @FXML
    fun menuItemShiftAddOnAction() = ShiftDialog()
            .showAndWait()
            .ifPresent { (mStart, mEnd, mRecess) ->
                safeTransaction {
                    Shift.new {
                        startTime = mStart
                        endTime = mEnd
                        recess = mRecess
                    }
                }
                refreshShift()
            }

    @FXML
    fun menuItemAboutOnAction() {
        infoAlert(ImageView(Image(R.png.ic_launcher)), "${getString(R.string.app_name)} ${BuildConfig.VERSION}", getString(R.string.about_content)) {
            expandableContent = VBox(
                    Label(getString(R.string.about_expandable_content)),
                    Hyperlink("https://github.com/WijayaPrinting/wp-attendance").apply { setOnAction { Desktop.getDesktop().browse(URI(text)) } })
            isExpanded = true
        }.showAndWait()
    }

    @FXML
    fun buttonBrowseOnAction() = fileChooser(FileChooser.ExtensionFilter("XLSX file", "*.xlsx"))
            .showOpenDialog(textFieldFile.scene.window)
            ?.let { textFieldFile.text = it.absolutePath }

    @FXML
    fun buttonReadOnAction() {
        val dialog = infoAlert(getString(R.string.please_wait), getString(R.string.please_wait_content)) { buttonTypes.clear() }
        dialog.show()
        flowPane.children.clear()
        Observable
                .create<Employee> { emitter ->
                    try {
                        val employees = Reader.parse((toggleGroupReader.selectedToggle as MenuItem).text).read(File(textFieldFile.text))
                        when (BuildConfig.DEBUG) {
                            true -> employees.filter { it.name == "Yanti" || it.name == "Yoyo" }.toMutableList()
                            else -> employees
                        }.forEach {
                            if (checkBoxMerge.isSelected) it.mergeDuplicates()
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
                    val pane = EmployeeTitledPane(employee.toString(), shifts, list)
                    flowPane.children.add(pane)
                }, { e ->
                    e.message ?: getString(R.string.error_unknown).let { errorAlert(it).showAndWait() }
                }, {
                    dialog.dialogPane.buttonTypes.add(ButtonType.OK) // apparently alert won't close without a button
                    dialog.close()
                })
    }

    @FXML
    fun buttonProcessOnAction() {
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
                scene = Scene(FXMLLoader.load(WPApp::class.java.getResource(R.fxml.layout_record), resources), minSize.first, minSize.second)
                icons.add(Image(R.png.ic_launcher))
                title = "${getString(R.string.app_name)} ${BuildConfig.VERSION} - ${getString(R.string.record)}"
                minWidth = minSize.first
                minHeight = minSize.second
            }.showAndWait()
        }
    }
}