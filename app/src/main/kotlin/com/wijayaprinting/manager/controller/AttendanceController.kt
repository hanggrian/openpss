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
import javafx.scene.layout.FlowPane
import javafx.scene.layout.HBox
import javafx.scene.layout.Priority
import javafx.stage.Stage
import kotfx.bindings.bindingOf
import kotfx.bindings.isEmpty
import kotfx.bindings.stringBindingOf
import kotfx.controls.imageViewOf
import kotfx.controls.label
import kotfx.controls.listView
import kotfx.controls.menus.contextMenuOf
import kotfx.controls.menus.menuItem
import kotfx.controls.menus.separatorMenuItem
import kotfx.controls.titledPaneOf
import kotfx.dialogs.errorAlertWait
import kotfx.dialogs.fileChooser
import kotfx.dialogs.infoAlert
import kotfx.dialogs.warningAlert
import kotfx.layouts.gridPane
import kotfx.layouts.vboxOf
import kotfx.properties.bind
import kotfx.properties.bindBidirectional
import kotfx.runFX
import org.joda.time.DateTime
import java.io.File

class AttendanceController {

    @FXML lateinit var fileField: FileField
    @FXML lateinit var readerChoiceBox: ChoiceBox<Reader>
    @FXML lateinit var mergeToggleButton: ToggleButton
    @FXML lateinit var readButton: Button
    @FXML lateinit var processButton: Button
    @FXML lateinit var employeeCountLabel: Label
    @FXML lateinit var flowPane: FlowPane

    @FXML
    fun initialize() = runFX {
        readerChoiceBox.items = Reader.listAll()
        if (readerChoiceBox.items.isNotEmpty()) readerChoiceBox.selectionModel.select(0)

        readButton.disableProperty() bind fileField.validProperty
        processButton.disableProperty() bind flowPane.children.isEmpty
        employeeCountLabel.textProperty() bind stringBindingOf(flowPane.children) { "${flowPane.children.size} ${getString(R.string.employee)}" }
        flowPane.prefWrapLengthProperty() bind fileField.scene.widthProperty()

        if (BuildConfig.DEBUG) {
            fileField.text = "/Users/hendraanggrian/Downloads/Absen 11-25-17.xlsx"
            readButton.fire()
        }
    }

    @FXML
    fun browseButtonOnAction() = fileField.scene.window.fileChooser(getString(R.string.input_file), *readerChoiceBox.value.extensions)?.let { fileField.text = it.absolutePath }

    @FXML
    fun readButtonOnAction() {
        val progressDialog = infoAlert(getString(R.string.please_wait_content)) {
            header(getString(R.string.please_wait))
            buttonTypes.clear()
        }
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
                .subscribeBy({ e -> errorAlertWait(e.message ?: getString(R.string.error_unknown)) }, {
                    progressDialog.dialogPane.buttonTypes.add(OK) // apparently alert won't close without a button
                    progressDialog.close()
                }) { employee ->
                    flowPane.children.add(titledPaneOf(employee.toString()) {
                        userData = employee
                        isCollapsible = false
                        lateinit var listView: ListView<DateTime>
                        content = vboxOf {
                            gridPane {
                                setGaps(4)
                                padding = Insets(4.0, 8.0, 4.0, 8.0)
                                label(getString(R.string.daily_income)) col 0 row 0
                                IntField().apply {
                                    prefWidth = 96.0
                                    promptText = getString(R.string.daily_income)
                                    valueProperty bindBidirectional employee.daily
                                }.add() col 1 row 0 colSpan 2
                                label(getString(R.string.overtime_income)) col 0 row 1
                                IntField().apply {
                                    prefWidth = 96.0
                                    promptText = getString(R.string.overtime_income)
                                    valueProperty bindBidirectional employee.hourlyOvertime
                                }.add() col 1 row 1 colSpan 2
                                label(getString(R.string.recess)) col 0 row 2
                                DoubleField().apply {
                                    prefWidth = 48.0
                                    promptText = getString(R.string.recess)
                                    valueProperty bindBidirectional employee.recess
                                }.add() col 1 row 2
                                DoubleField().apply {
                                    prefWidth = 48.0
                                    promptText = getString(R.string.recess)
                                    valueProperty bindBidirectional employee.recessOvertime
                                }.add() col 2 row 2
                            }
                            listView = listView(employee.attendances) {
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
                        }
                        contextMenu = contextMenuOf {
                            menuItem(getString(R.string.add)) {
                                setOnAction {
                                    DateTimeDialog(getString(R.string.record), ImageView(R.png.ic_calendar), getString(R.string.add), listView.selectionModel.selectedItem)
                                            .showAndWait()
                                            .ifPresent {
                                                listView.items.add(it)
                                                listView.items.sort()
                                            }
                                }
                            }
                            menuItem(getString(R.string.edit)) {
                                setOnAction {
                                    DateTimeDialog(getString(R.string.record), ImageView(R.png.ic_calendar), getString(R.string.edit), listView.selectionModel.selectedItem)
                                            .showAndWait()
                                            .ifPresent {
                                                listView.items[listView.selectionModel.selectedIndex] = it
                                                listView.items.sort()
                                            }
                                }
                                disableProperty() bind listView.selectionModel.selectedItems.isEmpty
                            }
                            separatorMenuItem()
                            menuItem(getString(R.string.revert)) { setOnAction { employee.revert() } }
                            separatorMenuItem()
                            menuItem("${getString(R.string.delete)} ${employee.name}") { setOnAction { flowPane.children.remove(this@titledPaneOf) } }
                            menuItem(getString(R.string.delete_others)) { setOnAction { flowPane.children.removeAll(((flowPane).children).toMutableList().apply { remove(this@titledPaneOf) }) } }
                            menuItem(getString(R.string.delete_all)) { setOnAction { flowPane.children.clear() } }
                        }
                        graphic = imageViewOf { imageProperty() bind bindingOf(listView.items) { Image(if (listView.items.size % 2 == 0) R.png.btn_checkbox else R.png.btn_checkbox_outline) } }
                    })
                }
    }

    @FXML
    fun processButtonOnAction() {
        val set = mutableSetOf<Employee>()
        flowPane.children.map { it.userData as Employee }.forEach { employee ->
            when {
                employee.daily.value <= 0 || employee.hourlyOvertime.value <= 0 -> {
                    warningAlert(getString(R.string.error_employee_incomplete)) { header(employee.name) }
                    return
                }
                employee.attendances.size % 2 != 0 -> {
                    warningAlert(getString(R.string.error_employee_odd)) { header(employee.name) }
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
}