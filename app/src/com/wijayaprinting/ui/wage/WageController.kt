package com.wijayaprinting.ui.wage

import com.wijayaprinting.BuildConfig.DEBUG
import com.wijayaprinting.PATTERN_DATETIME
import com.wijayaprinting.R
import com.wijayaprinting.db.schema.Recesses
import com.wijayaprinting.db.transaction
import com.wijayaprinting.io.WageFolder
import com.wijayaprinting.ui.Controller
import com.wijayaprinting.ui.DateTimeDialog
import com.wijayaprinting.ui.controller
import com.wijayaprinting.ui.pane
import com.wijayaprinting.ui.scene.control.FileField
import com.wijayaprinting.ui.scene.control.GraphicListCell
import com.wijayaprinting.ui.scene.control.intField
import com.wijayaprinting.ui.wage.WageRecordController.Companion.EXTRA_ATTENDEES
import com.wijayaprinting.ui.wage.WageRecordController.Companion.EXTRA_STAGE
import com.wijayaprinting.ui.wage.readers.Reader
import com.wijayaprinting.util.getResource
import javafx.fxml.FXML
import javafx.fxml.FXMLLoader
import javafx.geometry.Insets
import javafx.geometry.Pos.CENTER
import javafx.scene.Node
import javafx.scene.Scene
import javafx.scene.control.Button
import javafx.scene.control.ChoiceBox
import javafx.scene.control.Label
import javafx.scene.control.ListView
import javafx.scene.control.ScrollPane
import javafx.scene.control.TitledPane
import javafx.scene.control.ToggleButton
import javafx.scene.image.Image
import javafx.scene.image.ImageView
import javafx.scene.layout.FlowPane
import javafx.scene.layout.Pane
import javafx.scene.layout.Priority.ALWAYS
import javafx.scene.text.Font.font
import javafx.stage.Modality.APPLICATION_MODAL
import kotfx.bindings.`else`
import kotfx.bindings.`if`
import kotfx.bindings.bindingOf
import kotfx.bindings.booleanBindingOf
import kotfx.bindings.isEmpty
import kotfx.bindings.lessEq
import kotfx.bindings.or
import kotfx.bindings.sizeBinding
import kotfx.bindings.stringBindingOf
import kotfx.bindings.then
import kotfx.dialogs.errorAlert
import kotfx.dialogs.fileChooser
import kotfx.gap
import kotfx.maxSize
import kotfx.minSize
import kotfx.runLater
import kotfx.scene.borderPane
import kotfx.scene.button
import kotfx.scene.checkBox
import kotfx.scene.contextMenu
import kotfx.scene.gridPane
import kotfx.scene.imageView
import kotfx.scene.label
import kotfx.scene.listView
import kotfx.scene.menuItem
import kotfx.scene.separatorMenuItem
import kotfx.scene.titledPane
import kotfx.scene.vbox
import kotfx.stage
import kotlinx.coroutines.experimental.javafx.JavaFx
import kotlinx.coroutines.experimental.launch
import org.joda.time.DateTime
import java.awt.Desktop.getDesktop

class WageController : Controller() {

    @FXML lateinit var fileField: FileField
    @FXML lateinit var readerChoiceBox: ChoiceBox<Reader>
    @FXML lateinit var mergeToggleButton: ToggleButton
    @FXML lateinit var scrollPane: ScrollPane
    @FXML lateinit var flowPane: FlowPane
    @FXML lateinit var employeeCountLabel: Label
    @FXML lateinit var readButton: Button
    @FXML lateinit var processButton: Button

    override fun initialize() {
        readerChoiceBox.items = Reader.listAll()
        if (readerChoiceBox.items.isNotEmpty()) readerChoiceBox.selectionModel.select(0)

        employeeCountLabel.textProperty().bind(stringBindingOf(flowPane.children) { "${flowPane.children.size} ${getString(R.string.employee)}" })
        readButton.disableProperty().bind(fileField.validProperty)
        processButton.disableProperty().bind(flowPane.children.isEmpty)

        if (DEBUG) {
            fileField.text = "/Users/hendraanggrian/Downloads/Absen 12-29-17.xlsx"
            //readButton.fire()
        }
        runLater { flowPane.prefWrapLengthProperty().bind(fileField.scene.widthProperty()) }
    }

    @FXML
    fun recess() = stage(getString(R.string.recess)) {
        val loader = FXMLLoader(getResource(R.layout.controller_wage_recess), resources)
        initModality(APPLICATION_MODAL)
        scene = Scene(loader.pane)
        isResizable = false
        loader.controller._employee = _employee
    }.showAndWait()

    @FXML fun history() = getDesktop().open(WageFolder)

    @FXML fun browse() = fileChooser(getString(R.string.input_file), *readerChoiceBox.value.extensions).showOpenDialog(fileField.scene.window)?.let { fileField.text = it.absolutePath }

    @FXML
    fun read() {
        launch(JavaFx) {
            scrollPane.content = borderPane {
                prefWidthProperty().bind(scrollPane.widthProperty())
                prefHeightProperty().bind(scrollPane.heightProperty())
                center = kotfx.scene.progressIndicator { maxSize = 72.0 }
            }
            flowPane.children.clear()
        }
        launch {
            try {
                readerChoiceBox.selectionModel.selectedItem.read(fileField.file).await().forEach { attendee ->
                    if (mergeToggleButton.isSelected) attendee.mergeDuplicates()
                    launch(JavaFx) {
                        flowPane.children.add(titledPane(attendee.toString()) {
                            lateinit var listView: ListView<DateTime>
                            userData = attendee
                            isCollapsible = false
                            content = vbox {
                                gridPane {
                                    gap = 4.0
                                    padding = Insets(8.0)
                                    attendee.role?.let { role ->
                                        label(getString(R.string.role)) col 0 row 0 marginRight 4
                                        label(role) col 1 row 0 colSpan 2
                                    }
                                    label(getString(R.string.income)) col 0 row 1 marginRight 4
                                    intField {
                                        prefWidth = 100.0
                                        promptText = getString(R.string.income)
                                        valueProperty.bindBidirectional(attendee.dailyProperty)
                                    } col 1 row 1
                                    label("@${getString(R.string.day)}") { font = font(9.0) } col 2 row 1
                                    label(getString(R.string.overtime)) col 0 row 2 marginRight 4
                                    intField {
                                        prefWidth = 96.0
                                        promptText = getString(R.string.overtime)
                                        valueProperty.bindBidirectional(attendee.hourlyOvertimeProperty)
                                    } col 1 row 2
                                    label("@${getString(R.string.hour)}") { font = font(9.0) } col 2 row 2
                                    label(getString(R.string.recess)) col 0 row 3 marginRight 4
                                    vbox {
                                        transaction {
                                            Recesses.find().forEach { recess ->
                                                checkBox(recess.toString()) {
                                                    selectedProperty().addListener { _, _, selected ->
                                                        (this@titledPane.userData as Attendee).recesses.let { recesses ->
                                                            if (selected) recesses.add(recess) else recesses.remove(recess)
                                                        }
                                                    }
                                                    isSelected = true
                                                } marginTop if (children.size > 1) 4 else 0
                                            }
                                        }
                                    } col 1 row 3 colSpan 2
                                }
                                listView = listView(attendee.attendances) {
                                    prefWidth = 128.0
                                    setCellFactory {
                                        object : GraphicListCell<DateTime>() {
                                            override fun getGraphic(item: DateTime): Node = kotfx.scene.hbox {
                                                alignment = CENTER
                                                label(item.toString(PATTERN_DATETIME)) { maxWidth = Double.MAX_VALUE } hpriority ALWAYS
                                                button {
                                                    minSize = 17.0
                                                    maxSize = 17.0
                                                    graphicProperty().bind(bindingOf<Node>(hoverProperty()) { if (isHover) ImageView(R.image.btn_clear) else null })
                                                    setOnAction { listView.items.remove(item) }
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                            contextMenu = contextMenu {
                                menuItem(getString(R.string.add)) {
                                    setOnAction {
                                        DateTimeDialog(this@WageController, getString(R.string.add_record))
                                                .showAndWait()
                                                .ifPresent {
                                                    listView.items.add(it)
                                                    listView.items.sort()
                                                }
                                    }
                                }
                                menuItem(getString(R.string.edit)) {
                                    disableProperty().bind(listView.selectionModel.selectedItems.isEmpty)
                                    setOnAction {
                                        DateTimeDialog(this@WageController, getString(R.string.edit_record), listView.selectionModel.selectedItem)
                                                .showAndWait()
                                                .ifPresent {
                                                    listView.items[listView.selectionModel.selectedIndex] = it
                                                    listView.items.sort()
                                                }
                                    }
                                }
                                separatorMenuItem()
                                menuItem(getString(R.string.revert)) { setOnAction { attendee.attendances.revert() } }
                                separatorMenuItem()
                                menuItem("${getString(R.string.delete)} ${attendee.name}") {
                                    setOnAction {
                                        flowPane.children.remove(this@titledPane)
                                        rebindProcessButton()
                                    }
                                }
                                menuItem(getString(R.string.delete_others)) {
                                    disableProperty().bind(flowPane.children.sizeBinding lessEq 1)
                                    setOnAction {
                                        flowPane.children.removeAll(flowPane.children.toMutableList().apply { remove(this@titledPane) })
                                        rebindProcessButton()
                                    }
                                }
                                menuItem(getString(R.string.delete_employees_to_the_right)) {
                                    disableProperty().bind(booleanBindingOf(flowPane.children) { flowPane.children.indexOf(this@titledPane) == flowPane.children.lastIndex })
                                    setOnAction {
                                        flowPane.children.removeAll(flowPane.children.toList().takeLast(flowPane.children.lastIndex - flowPane.children.indexOf(this@titledPane)))
                                        rebindProcessButton()
                                    }
                                }
                            }
                            graphic = imageView { imageProperty().bind(`if`(booleanBindingOf(listView.items) { listView.items.size % 2 == 0 }) then Image(R.image.btn_checkbox) `else` Image(R.image.btn_checkbox_outline)) }
                        })
                    }
                }
                launch(JavaFx) {
                    scrollPane.content = flowPane
                    rebindProcessButton()
                }
            } catch (e: Exception) {
                if (DEBUG) e.printStackTrace()
                launch(JavaFx) {
                    scrollPane.content = flowPane
                    rebindProcessButton()
                    errorAlert(e.message.toString()).showAndWait()
                }
            }
        }
    }

    @FXML
    fun process() {
        attendees.forEach { it.saveWage() }
        stage {
            val loader = FXMLLoader(getResource(R.layout.controller_wage_record), resources)
            scene = Scene(loader.pane)
            minWidth = 1000.0
            minHeight = 650.0
            loader.controller.addExtra(EXTRA_ATTENDEES, attendees).addExtra(EXTRA_STAGE, this)
        }.showAndWait()
    }

    /** Employees are stored in flowpane childrens' user model. */
    private val attendees: List<Attendee> get() = flowPane.children.map { it.userData as Attendee }

    /** As attendees are populated, process button need to be rebinded according to new requirements. */
    private fun rebindProcessButton() = processButton.disableProperty().bind(flowPane.children.isEmpty or booleanBindingOf(flowPane.children, *flowPane.children.map { (it as TitledPane).content }.map { (it as Pane).children[1] as ListView<*> }.map { it.items }.toTypedArray()) {
        attendees.any { it.attendances.size % 2 != 0 }
    })
}