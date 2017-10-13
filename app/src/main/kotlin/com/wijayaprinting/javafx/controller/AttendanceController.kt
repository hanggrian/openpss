package com.wijayaprinting.javafx.controller

import com.wijayaprinting.javafx.*
import com.wijayaprinting.javafx.data.Employee
import com.wijayaprinting.javafx.reader.Reader
import com.wijayaprinting.javafx.scene.control.DoubleField
import com.wijayaprinting.javafx.scene.control.FileField
import com.wijayaprinting.javafx.scene.control.IntField
import com.wijayaprinting.javafx.scene.layout.TimeBox
import com.wijayaprinting.javafx.scene.utils.setGaps
import com.wijayaprinting.javafx.scene.utils.setMaxSize
import com.wijayaprinting.javafx.scene.utils.setSize
import com.wijayaprinting.mysql.utils.PATTERN_DATETIME
import io.reactivex.Observable
import io.reactivex.rxjavafx.schedulers.JavaFxScheduler
import io.reactivex.schedulers.Schedulers
import javafx.fxml.FXML
import javafx.fxml.FXMLLoader
import javafx.geometry.Insets
import javafx.geometry.Pos.CENTER
import javafx.scene.Node
import javafx.scene.Scene
import javafx.scene.control.*
import javafx.scene.image.ImageView
import javafx.scene.layout.*
import javafx.stage.FileChooser
import javafx.stage.Stage
import javafx.util.Callback
import kotfx.bindings.*
import kotfx.dialogs.*
import kotfx.runLater
import org.joda.time.DateTime
import java.io.File
import java.time.LocalDate
import java.util.*

/**
 * @author Hendra Anggrian (hendraanggrian@gmail.com)
 */
class AttendanceController {

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
            fileField.text = "/Users/hendraanggrian/Desktop/Absen 7-10-17.xlsx"
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
                .subscribeOn(Schedulers.computation())
                .observeOn(JavaFxScheduler.platform())
                .subscribe({ employee ->
                    flowPane.children.add(EmployeeTitledPane(employee))
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
                employee.daily.value <= 0 || employee.hourlyOvertime.value <= 0 -> {
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
                scene = Scene(FXMLLoader.load(App::class.java.getResource(R.fxml.layout_attendance_record), resources), minSize.first, minSize.second)
                title = "${getString(R.string.app_name)} - ${getString(R.string.record)}"
                minWidth = minSize.first
                minHeight = minSize.second
            }.showAndWait()
        }
    }

    class EmployeeTitledPane(val employee: Employee) : TitledPane() {
        val content = Content()
        val graphic = Graphic()

        init {
            isCollapsible = false
            text = employee.toString()
            setContent(content)
            setGraphic(graphic)
            contextMenu = ContextMenu(content.addMenuItem, SeparatorMenuItem(), content.deleteThisMenuItem, content.deleteOthersMenuItem)
        }

        inner class Graphic : Button() {
            init {
                setSize(18.0)
                graphicProperty().bind(bindingOf<Node?>(hoverProperty()) { if (isHover) ImageView(R.png.btn_clear) else null })
                setOnAction { (this@EmployeeTitledPane.parent as Pane).children.remove(this@EmployeeTitledPane) }
            }
        }

        inner class Content : VBox() {
            val dailyLabel = Label(getString(R.string.daily_income))
            val dailyField = IntField()
            val overtimeLabel = Label(getString(R.string.overtime_income))
            val overtimeField = IntField()
            val recessLabel = Label(getString(R.string.recess))
            val recessField = DoubleField()
            val listView = ListView<DateTime>(employee.attendances)

            val addMenuItem = MenuItem(getString(R.string.add))
            val deleteThisMenuItem = MenuItem("${getString(R.string.delete)} ${employee.name}")
            val deleteOthersMenuItem = MenuItem(getString(R.string.delete_others))

            init {
                dailyField.prefWidth = 96.0
                dailyField.promptText = getString(R.string.daily_income)
                dailyField.valueProperty.bindBidirectional(employee.daily)

                overtimeField.prefWidth = 96.0
                overtimeField.promptText = getString(R.string.overtime_income)
                overtimeField.valueProperty.bindBidirectional(employee.hourlyOvertime)

                recessField.prefWidth = 96.0
                recessField.promptText = getString(R.string.recess)
                recessField.valueProperty.bindBidirectional(employee.recess)

                listView.prefWidth = 128.0
                listView.setCellFactory {
                    object : ListCell<DateTime>() {
                        override fun updateItem(item: DateTime?, empty: Boolean) {
                            super.updateItem(item, empty)
                            text = null
                            graphic = null
                            if (item != null && !empty) {
                                val label = Label(item.toString(PATTERN_DATETIME)).apply { setMaxSize(Double.MAX_VALUE) }
                                val button = Button().apply { setSize(18.0) }
                                button.graphicProperty().bind(bindingOf<Node?>(button.hoverProperty()) { if (button.isHover) ImageView(R.png.btn_clear) else null })
                                button.setOnAction { listView.items.remove(item) }
                                graphic = HBox(label, button).apply { alignment = CENTER }
                                HBox.setHgrow(label, Priority.ALWAYS)
                            }
                        }
                    }
                }

                addMenuItem.setOnAction {
                    dialog<DateTime>(getString(R.string.record), ImageView(R.png.ic_calendar), getString(R.string.record)) {
                        val datePicker: DatePicker = DatePicker().apply {
                            listView.selectionModel.selectedItem?.let {
                                value = LocalDate.of(it.year, it.monthOfYear, it.dayOfMonth)
                            }
                            isEditable = false // force pick from popup
                            maxWidth = 128.0
                            alignment = CENTER
                        }
                        val timeBox = TimeBox()
                        content = HBox().apply {
                            spacing = 8.0
                            alignment = CENTER
                            children.addAll(Label(getString(R.string.date)), datePicker, timeBox)
                        }
                        buttonTypes.addAll(ButtonType.OK, ButtonType.CANCEL)
                        lookupButton(ButtonType.OK).disableProperty().bind(datePicker.valueProperty().isNull or not(timeBox.validProperty))
                        runLater { datePicker.requestFocus() }
                        Callback {
                            if (it != ButtonType.OK) null
                            else DateTime(datePicker.value.year, datePicker.value.monthValue, datePicker.value.dayOfMonth, timeBox.value.hourOfDay, timeBox.value.minuteOfHour)
                        }
                    }.showAndWait()
                            .ifPresent {
                                listView.items.add(it)
                                Collections.sort(listView.items)
                            }
                }
                deleteThisMenuItem.setOnAction {
                    (this@EmployeeTitledPane.parent as Pane).let { it.children.remove(this@EmployeeTitledPane) }
                }
                deleteOthersMenuItem.setOnAction {
                    (this@EmployeeTitledPane.parent as Pane).let {
                        it.children.removeAll(((it).children).toMutableList().apply {
                            remove(this@EmployeeTitledPane)
                        })
                    }
                }

                children.addAll(GridPane().apply {
                    setGaps(4.0)
                    padding = Insets(8.0)
                    add(dailyLabel, 0, 0)
                    add(dailyField, 1, 0)
                    add(overtimeLabel, 0, 1)
                    add(overtimeField, 1, 1)
                    add(recessLabel, 0, 2)
                    add(recessField, 1, 2)
                }, listView)
            }
        }
    }
}