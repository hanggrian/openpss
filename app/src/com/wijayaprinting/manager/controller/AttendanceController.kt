package com.wijayaprinting.manager.controller

import com.wijayaprinting.data.PATTERN_DATETIME
import com.wijayaprinting.manager.BuildConfig.DEBUG
import com.wijayaprinting.manager.R
import com.wijayaprinting.manager.data.Employee
import com.wijayaprinting.manager.dialog.DateTimeDialog
import com.wijayaprinting.manager.reader.Reader
import com.wijayaprinting.manager.scene.control.FileField
import com.wijayaprinting.manager.scene.control.doubleField
import com.wijayaprinting.manager.scene.control.intField
import com.wijayaprinting.manager.scene.utils.setGaps
import com.wijayaprinting.manager.scene.utils.setMaxSize
import com.wijayaprinting.manager.scene.utils.setSize
import com.wijayaprinting.manager.utils.multithread
import io.reactivex.Observable
import io.reactivex.rxkotlin.subscribeBy
import io.reactivex.schedulers.Schedulers.computation
import javafx.fxml.FXML
import javafx.geometry.Insets
import javafx.geometry.Pos.CENTER
import javafx.scene.Node
import javafx.scene.control.*
import javafx.scene.control.ButtonType.OK
import javafx.scene.image.Image
import javafx.scene.image.ImageView
import javafx.scene.layout.FlowPane
import javafx.scene.layout.Pane
import javafx.scene.layout.Priority.ALWAYS
import javafx.scene.text.Font.font
import kotfx.*
import org.joda.time.DateTime
import java.io.File

class AttendanceController : Controller() {

    @FXML lateinit var fileField: FileField
    @FXML lateinit var readerChoiceBox: ChoiceBox<Reader>
    @FXML lateinit var mergeToggleButton: ToggleButton
    @FXML lateinit var flowPane: FlowPane
    @FXML lateinit var employeeCountLabel: Label
    @FXML lateinit var readButton: Button
    @FXML lateinit var processButton: Button

    @FXML
    fun initialize() = runFX {
        readerChoiceBox.items = Reader.listAll()
        if (readerChoiceBox.items.isNotEmpty()) readerChoiceBox.selectionModel.select(0)

        flowPane.prefWrapLengthProperty() bind fileField.scene.widthProperty()
        employeeCountLabel.textProperty() bind stringBindingOf(flowPane.children) { "${flowPane.children.size} ${getString(R.string.employee)}" }
        readButton.disableProperty() bind fileField.validProperty
        processButton.disableProperty() bind flowPane.children.isEmpty

        if (DEBUG) {
            fileField.text = "/Users/hendraanggrian/Downloads/Absen 11-25-17.xlsx"
            readButton.fire()
        }
    }

    @FXML
    fun browseOnAction() = fileChooser(getString(R.string.input_file), *readerChoiceBox.value.extensions)
            .showOpenDialog(fileField.scene.window)
            ?.let { fileField.text = it.absolutePath }

    @FXML
    fun readOnAction() {
        val progressDialog = infoAlert(getString(R.string.please_wait_content)) {
            headerText = getString(R.string.please_wait)
            buttonTypes.clear()
        }.apply { show() }
        flowPane.children.clear()
        Observable
                .create<Employee> { emitter ->
                    try {
                        val employees = readerChoiceBox.selectionModel.selectedItem.read(File(fileField.text))
                        when (DEBUG) {
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
                .subscribeBy({ e -> errorAlert(e.message.toString()).showAndWait() }, {
                    progressDialog.addButtons(OK) // apparently alert won't close without a button
                    progressDialog.close()
                    rebindProcessButton()
                }) { employee ->
                    flowPane.children.add(titledPane(employee.toString()) {
                        userData = employee
                        isCollapsible = false
                        lateinit var listView: ListView<DateTime>
                        content = vbox {
                            gridPane {
                                setGaps(4)
                                padding = Insets(8.0)
                                employee.role?.let { role ->
                                    label(getString(R.string.role)) col 0 row 0 marginRight 4
                                    textField(role) {
                                        prefWidth = 100.0
                                        isEditable = false
                                    } col 1 row 0 colSpan 3
                                }
                                label(getString(R.string.income)) col 0 row 1 marginRight 4
                                intField {
                                    prefWidth = 100.0
                                    promptText = getString(R.string.income)
                                    valueProperty bindBidirectional employee.daily
                                } col 1 row 1 colSpan 2
                                label("@${getString(R.string.day)}") { font = font(9.0) } col 3 row 1
                                label(getString(R.string.overtime)) col 0 row 2 marginRight 4
                                intField {
                                    prefWidth = 96.0
                                    promptText = getString(R.string.overtime)
                                    valueProperty bindBidirectional employee.hourlyOvertime
                                } col 1 row 2 colSpan 2
                                label("@${getString(R.string.hour)}") { font = font(9.0) } col 3 row 2
                                label(getString(R.string.recess)) col 0 row 3 marginRight 4
                                doubleField {
                                    prefWidth = 48.0
                                    promptText = getString(R.string.recess)
                                    valueProperty bindBidirectional employee.recess
                                } col 1 row 3
                                doubleField {
                                    prefWidth = 48.0
                                    promptText = getString(R.string.recess)
                                    valueProperty bindBidirectional employee.recessOvertime
                                } col 2 row 3
                                label(getString(R.string.hour)) { font = font(9.0) } col 3 row 3
                            }
                            listView = listView(employee.attendances) {
                                prefWidth = 128.0
                                setCellFactory {
                                    object : ListCell<DateTime>() {
                                        override fun updateItem(item: DateTime?, empty: Boolean) {
                                            super.updateItem(item, empty)
                                            text = null
                                            graphic = null
                                            if (item != null && !empty) graphic = kotfx.hbox {
                                                alignment = CENTER
                                                label(item.toString(PATTERN_DATETIME)) { setMaxSize(Double.MAX_VALUE) } hpriority ALWAYS
                                                button {
                                                    setSize(17.0)
                                                    graphicProperty() bind bindingOf<Node>(hoverProperty()) { if (isHover) ImageView(R.png.btn_clear) else null }
                                                    setOnAction { listView.items.remove(item) }
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                        contextMenu = contextMenu {
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
                            menuItem(getString(R.string.revert)) { setOnAction { employee.attendances.revert() } }
                            separatorMenuItem()
                            menuItem("${getString(R.string.delete)} ${employee.name}") {
                                setOnAction {
                                    flowPane.children.remove(this@titledPane)
                                    rebindProcessButton()
                                }
                            }
                            menuItem(getString(R.string.delete_others)) {
                                disableProperty() bind (flowPane.children.sizeBinding lessEq 1)
                                setOnAction {
                                    flowPane.children.removeAll(flowPane.children.toMutableList().apply { remove(this@titledPane) })
                                    rebindProcessButton()
                                }
                            }
                            menuItem(getString(R.string.delete_employees_to_the_right)) {
                                disableProperty() bind booleanBindingOf(flowPane.children) { flowPane.children.indexOf(this@titledPane) == flowPane.children.lastIndex }
                                setOnAction {
                                    flowPane.children.removeAll(flowPane.children.toList().takeLast(flowPane.children.lastIndex - flowPane.children.indexOf(this@titledPane)))
                                    rebindProcessButton()
                                }
                            }
                        }
                        graphic = imageView { imageProperty() bind bindingOf(listView.items) { Image(if (listView.items.size % 2 == 0) R.png.btn_checkbox else R.png.btn_checkbox_outline) } }
                    })
                }
    }

    @FXML
    fun processOnAction() {
        val set = mutableSetOf<Employee>()
        employees.forEach { employee ->
            employee.saveWage()
            set.add(employee)
        }
        if (set.isNotEmpty()) {
            val minSize = Pair(960.0, 640.0)
            stage("${getString(R.string.app_name)} - ${getString(R.string.record)}") {
                val loader = getResource(R.fxml.layout_attendance_record).loadFXML(resources)
                scene = loader.load<Pane>().toScene(minSize.first, minSize.second)
                minWidth = minSize.first
                minHeight = minSize.second
                loader.getController<Controller>().setUserData(set)
            }.showAndWait()
        }
    }

    /** Employees are stored in flowpane childrens' user data. */
    private val employees: Iterable<Employee> get() = flowPane.children.map { it.userData as Employee }

    /** As employees are populated, process button need to be rebinded according to new requirements. */
    private fun rebindProcessButton() {
        processButton.disableProperty().unbind()
        processButton.disableProperty() bind (flowPane.children.isEmpty or booleanBindingOf(flowPane.children, *flowPane.children.map { (it as TitledPane).content }.map { (it as Pane).children[1] as ListView<*> }.map { it.items }.toTypedArray()) {
            employees.any { it.attendances.size % 2 != 0 }
        })
    }
}