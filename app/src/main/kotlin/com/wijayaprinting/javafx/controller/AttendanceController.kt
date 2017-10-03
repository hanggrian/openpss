package com.wijayaprinting.javafx.controller

import com.wijayaprinting.javafx.*
import com.wijayaprinting.javafx.data.Employee
import com.wijayaprinting.javafx.reader.Reader
import com.wijayaprinting.javafx.scene.control.FileField
import com.wijayaprinting.javafx.scene.control.IntField
import com.wijayaprinting.javafx.scene.control.TimeField
import com.wijayaprinting.javafx.scene.layout.ShiftTitledPane
import com.wijayaprinting.javafx.scene.utils.gaps
import com.wijayaprinting.mysql.dao.Shift
import com.wijayaprinting.mysql.utils.PATTERN_DATETIME
import io.reactivex.Observable
import io.reactivex.rxjavafx.schedulers.JavaFxScheduler
import io.reactivex.schedulers.Schedulers
import javafx.beans.property.Property
import javafx.collections.ObservableList
import javafx.fxml.FXML
import javafx.fxml.FXMLLoader
import javafx.geometry.Insets
import javafx.geometry.Pos
import javafx.scene.Scene
import javafx.scene.control.*
import javafx.scene.image.Image
import javafx.scene.image.ImageView
import javafx.scene.layout.FlowPane
import javafx.scene.layout.GridPane
import javafx.scene.layout.Pane
import javafx.scene.layout.VBox
import javafx.stage.FileChooser
import javafx.stage.Stage
import javafx.util.Callback
import kotfx.bindings.and
import kotfx.bindings.isEmpty
import kotfx.bindings.not
import kotfx.bindings.or
import kotfx.dialogs.*
import kotfx.runLater
import kotfx.stringConverter
import org.joda.time.DateTime
import org.joda.time.format.DateTimeFormat
import java.io.File
import java.io.File.separator
import java.util.*

/**
 * @author Hendra Anggrian (hendraanggrian@gmail.com)
 */
class AttendanceController {

    @FXML lateinit var shiftTitledPane: ShiftTitledPane
    @FXML lateinit var fileField: FileField
    @FXML lateinit var readerChoiceBox: ChoiceBox<Reader>
    @FXML lateinit var mergeCheckBox: CheckBox
    @FXML lateinit var clearButton: Button
    @FXML lateinit var readButton: Button
    @FXML lateinit var processButton: Button
    @FXML lateinit var flowPane: FlowPane

    @FXML
    fun initialize() = runLater {
        readerChoiceBox.items = Reader.listAll()
        if (readerChoiceBox.items.isNotEmpty()) readerChoiceBox.selectionModel.select(0)

        clearButton.disableProperty().bind(fileField.textProperty().isEmpty and flowPane.children.isEmpty)
        readButton.disableProperty().bind(fileField.validProperty)
        processButton.disableProperty().bind(flowPane.children.isEmpty)
        flowPane.prefWrapLengthProperty().bind(fileField.scene.widthProperty())

        if (BuildConfig.DEBUG) {
            fileField.text = "${System.getProperty("user.home")}${separator}Documents${separator}GitHub${separator}${BuildConfig.ARTIFACT}${separator}sample${separator}2017-9-15.xlsx"
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
                    val pane = EmployeeTitledPane(shiftTitledPane.listView.items, employee)
                    pane.text = employee.toString()
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
            setExtra(set)
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

    class EmployeeTitledPane(shifts: ObservableList<Shift>, val employee: Employee) : TitledPane() {
        val listView = ListView<DateTime>(employee.attendances).apply {
            prefWidth = 128.0
            setCellFactory { _ ->
                object : ListCell<DateTime>() {
                    override fun updateItem(item: DateTime?, empty: Boolean) {
                        super.updateItem(item, empty)
                        text = when {
                            item == null || empty -> null
                            else -> DateTimeFormat.forPattern(PATTERN_DATETIME).print(item)
                        }
                    }
                }
            }
        }
        val choiceBox: ChoiceBox<Shift> = ChoiceBox<Shift>(shifts).apply {
            maxWidth = Double.MAX_VALUE
            valueProperty().bindBidirectional(employee.shift as Property<Shift>)
        }
        val dailyField: TextField = IntField().apply {
            promptText = getString(R.string.daily_income)
            textProperty().bindBidirectional(employee.daily, stringConverter<Number> { if (it.isBlank()) 0 else Integer.valueOf(it) })
        }
        val overtimeField: TextField = IntField().apply {
            promptText = getString(R.string.overtime_income)
            textProperty().bindBidirectional(employee.overtimeHourly, stringConverter<Number> { if (it.isBlank()) 0 else Integer.valueOf(it) })
        }

        val addMenuItem = MenuItem(getString(R.string.add)).apply {
            setOnAction {
                dialog<DateTime>(getString(R.string.record), ImageView(R.png.ic_calendar), getString(R.string.record)) {
                    val datePicker: DatePicker = DatePicker().apply {
                        isEditable = false // force pick from popup
                        maxWidth = 128.0
                        alignment = Pos.CENTER
                    }
                    val timeField: TimeField = TimeField().apply {
                        maxWidth = 64.0
                        alignment = Pos.CENTER
                    }
                    content = GridPane().apply {
                        gaps = 8.0
                        add(Label(getString(R.string.date)), 0, 0)
                        add(datePicker, 1, 0)
                        add(Label(getString(R.string.time)), 0, 1)
                        add(timeField, 1, 1)
                    }
                    buttonTypes.addAll(ButtonType.OK, ButtonType.CANCEL)
                    lookupButton(ButtonType.OK).disableProperty().bind(datePicker.valueProperty().isNull or not(timeField.validProperty))
                    runLater { datePicker.requestFocus() }
                    Callback {
                        when (it) {
                            ButtonType.OK -> DateTime(datePicker.value.year, datePicker.value.monthValue, datePicker.value.dayOfMonth, timeField.value!!.hourOfDay, timeField.value!!.minuteOfHour)
                            else -> null
                        }
                    }
                }.showAndWait()
                        .ifPresent {
                            listView.items.add(it)
                            Collections.sort(listView.items)
                        }
            }
        }
        val deleteMenuItem = MenuItem(getString(R.string.delete)).apply {
            visibleProperty().bind(listView.selectionModel.selectedItemProperty().isNotNull)
            setOnAction {
                listView.items.removeAt(listView.selectionModel.selectedIndex)
                Collections.sort(listView.items)
            }
        }
        val deleteThisMenuItem = MenuItem("${getString(R.string.delete)} ${employee.name}").apply {
            setOnAction { (parent as Pane).children.remove(this@EmployeeTitledPane) }
        }
        val deleteOthersMenuItem = MenuItem(getString(R.string.delete_others)).apply {
            setOnAction {
                (parent as Pane).children.removeAll(((parent as Pane).children).toMutableList().apply {
                    remove(this@EmployeeTitledPane)
                })
            }
        }

        init {
            isCollapsible = false
            content = VBox().apply {
                padding = Insets(-0.1) // force no padding
                children.addAll(choiceBox, overtimeField, dailyField, listView)
            }
            contextMenu = ContextMenu(addMenuItem, deleteMenuItem, SeparatorMenuItem(), deleteThisMenuItem, deleteOthersMenuItem)
        }
    }
}