package com.wijayaprinting.manager.controller

import com.wijayaprinting.data.PATTERN_DATETIME
import com.wijayaprinting.manager.*
import com.wijayaprinting.manager.data.Employee
import com.wijayaprinting.manager.dialog.DateTimeDialog
import com.wijayaprinting.manager.reader.Reader
import com.wijayaprinting.manager.scene.control.DoubleField
import com.wijayaprinting.manager.scene.control.FileField
import com.wijayaprinting.manager.scene.control.IntField
import com.wijayaprinting.manager.scene.utils.setGaps
import com.wijayaprinting.manager.scene.utils.setMaxSize
import com.wijayaprinting.manager.scene.utils.setSize
import com.wijayaprinting.manager.utils.multithread
import io.reactivex.Observable
import io.reactivex.rxkotlin.subscribeBy
import io.reactivex.schedulers.Schedulers.computation
import javafx.application.Platform.runLater
import javafx.fxml.FXML
import javafx.fxml.FXMLLoader
import javafx.geometry.Insets
import javafx.geometry.Pos.CENTER
import javafx.scene.Node
import javafx.scene.Scene
import javafx.scene.control.*
import javafx.scene.control.ButtonType.OK
import javafx.scene.image.Image
import javafx.scene.image.ImageView
import javafx.scene.layout.*
import javafx.stage.FileChooser
import javafx.stage.Stage
import kotfx.bind
import kotfx.bindBidirectional
import kotfx.bindings.bindingOf
import kotfx.bindings.isEmpty
import kotfx.dialogs.errorAlert
import kotfx.dialogs.fileChooser
import kotfx.dialogs.infoAlert
import kotfx.dialogs.warningAlert
import org.joda.time.DateTime
import java.io.File
import java.util.*

class AttendanceController {

    @FXML lateinit var fileField: FileField
    @FXML lateinit var readerChoiceBox: ChoiceBox<Reader>
    @FXML lateinit var mergeToggleButton: ToggleButton
    @FXML lateinit var readButton: Button
    @FXML lateinit var processButton: Button
    @FXML lateinit var employeeCountLabel: Label
    @FXML lateinit var flowPane: FlowPane

    @FXML
    fun initialize() = runLater {
        readerChoiceBox.items = Reader.listAll()
        if (readerChoiceBox.items.isNotEmpty()) readerChoiceBox.selectionModel.select(0)

        readButton.disableProperty() bind fileField.validProperty
        processButton.disableProperty() bind flowPane.children.isEmpty
        employeeCountLabel.textProperty() bind bindingOf(flowPane.children) { flowPane.children.size.toString() + " " + getString(R.string.employee) }
        flowPane.prefWrapLengthProperty() bind fileField.scene.widthProperty()

        if (BuildConfig.DEBUG) {
            fileField.text = "/Users/hendraanggrian/Downloads/Absen 11-25-17.xlsx"
            readButton.fire()
        }
    }

    @FXML
    fun browseButtonOnAction() = fileChooser(FileChooser.ExtensionFilter("XLSX file", "*.xlsx"))
            .showOpenDialog(fileField.scene.window)
            ?.let { fileField.text = it.absolutePath }

    @FXML
    fun readButtonOnAction() {
        val progressDialog = infoAlert(getString(R.string.please_wait), getString(R.string.please_wait_content)) { buttonTypes.clear() }
        progressDialog.show()
        flowPane.children.clear()
        Observable
                .create<Employee> { emitter ->
                    try {
                        val employees = readerChoiceBox.selectionModel.selectedItem.read(File(fileField.text))
                        when (BuildConfig.DEBUG) {
                            true -> employees.filter { it.name == "Yanti" || it.name == "Yoyo" || it.name == "Mus" }.toMutableList()
                            else -> employees
                        }.forEach {
                            if (mergeToggleButton.isSelected) it.mergeDuplicates()
                            emitter.onNext(it)
                        }
                    } catch (e: Exception) {
                        emitter.onError(e)
                    }
                    emitter.onComplete()
                }
                .multithread(computation())
                .subscribeBy({ e -> e.message ?: getString(R.string.error_unknown).let { errorAlert(it).showAndWait() } }, {
                    progressDialog.dialogPane.buttonTypes.add(OK) // apparently alert won't close without a button
                    progressDialog.close()
                }) { employee ->
                    flowPane.children.add(EmployeeTitledPane(employee))
                }
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
            AttendanceRecordController.EMPLOYEES = set
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
        private val indicatorImage = ImageView()
        private val employeeBox = EmployeeBox()
        private val addMenu = MenuItem(getString(R.string.add))
        private val revertMenu = MenuItem(getString(R.string.revert))
        private val deleteMenu = MenuItem("${getString(R.string.delete)} ${employee.name}")
        private val deleteOthersMenu = MenuItem(getString(R.string.delete_others))
        private val deleteAllMenu = MenuItem(getString(R.string.delete_all))

        init {
            isCollapsible = false
            text = employee.toString()
            content = employeeBox
            graphic = indicatorImage

            indicatorImage.imageProperty() bind bindingOf(employeeBox.listView.items) {
                Image(if (employeeBox.listView.items.size % 2 == 0) R.png.btn_checkbox else R.png.btn_checkbox_outline)
            }

            contextMenu = ContextMenu(addMenu, SeparatorMenuItem(), revertMenu, SeparatorMenuItem(), deleteMenu, deleteOthersMenu, deleteAllMenu)
            addMenu.setOnAction {
                DateTimeDialog(getString(R.string.record), ImageView(R.png.ic_calendar), getString(R.string.record), employeeBox.listView.selectionModel.selectedItem)
                        .showAndWait()
                        .ifPresent {
                            employeeBox.listView.items.add(it)
                            Collections.sort(employeeBox.listView.items)
                        }
            }
            revertMenu.setOnAction { employee.revert() }
            deleteMenu.setOnAction { flowPane.children.remove(this@EmployeeTitledPane) }
            deleteOthersMenu.setOnAction { flowPane.children.removeAll(((flowPane).children).toMutableList().apply { remove(this@EmployeeTitledPane) }) }
            deleteAllMenu.setOnAction { flowPane.children.clear() }
        }

        private val flowPane: Pane get() = (this@EmployeeTitledPane.parent as Pane)

        inner class EmployeeBox : VBox() {
            private val dailyLabel = Label(getString(R.string.daily_income))
            private val dailyField = IntField().apply {
                prefWidth = 96.0
                promptText = getString(R.string.daily_income)
                valueProperty bindBidirectional employee.daily
            }

            private val overtimeLabel = Label(getString(R.string.overtime_income))
            private val overtimeField = IntField().apply {
                prefWidth = 96.0
                promptText = getString(R.string.overtime_income)
                valueProperty bindBidirectional employee.hourlyOvertime
            }

            private val recessLabel = Label(getString(R.string.recess))
            private val recessField = DoubleField().apply {
                prefWidth = 48.0
                promptText = getString(R.string.recess)
                valueProperty bindBidirectional employee.recess
            }
            private val recessOvertimeField = DoubleField().apply {
                prefWidth = 48.0
                promptText = getString(R.string.recess)
                valueProperty bindBidirectional employee.recessOvertime
            }

            val listView = ListView<DateTime>(employee.attendances).apply {
                prefWidth = 128.0
                setCellFactory {
                    object : ListCell<DateTime>() {
                        override fun updateItem(item: DateTime?, empty: Boolean) {
                            super.updateItem(item, empty)
                            text = null
                            graphic = null
                            if (item != null && !empty) {
                                val label = Label(item.toString(PATTERN_DATETIME)).apply { setMaxSize(Double.MAX_VALUE) }
                                val deleteButton = Button().apply { setSize(18.0) }
                                deleteButton.graphicProperty() bind bindingOf<Node?>(deleteButton.hoverProperty()) { if (deleteButton.isHover) ImageView(R.png.btn_clear) else null }
                                deleteButton.setOnAction { listView.items.remove(item) }
                                HBox.setHgrow(label, Priority.ALWAYS)
                                graphic = HBox(label, deleteButton).apply { alignment = CENTER }
                            }
                        }
                    }
                }
            }

            init {
                children.addAll(GridPane().apply {
                    setGaps(4.0)
                    padding = Insets(4.0, 8.0, 4.0, 8.0)
                    add(dailyLabel, 0, 0)
                    add(dailyField, 1, 0, 2, 1)
                    add(overtimeLabel, 0, 1)
                    add(overtimeField, 1, 1, 2, 1)
                    add(recessLabel, 0, 2)
                    add(recessField, 1, 2)
                    add(recessOvertimeField, 2, 2)
                }, listView)
            }
        }
    }
}