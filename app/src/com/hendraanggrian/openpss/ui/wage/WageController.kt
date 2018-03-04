package com.hendraanggrian.openpss.ui.wage

import com.hendraanggrian.openpss.BuildConfig.DEBUG
import com.hendraanggrian.openpss.R
import com.hendraanggrian.openpss.db.schema.Recesses
import com.hendraanggrian.openpss.db.transaction
import com.hendraanggrian.openpss.io.WageFolder
import com.hendraanggrian.openpss.scene.control.FileField
import com.hendraanggrian.openpss.scene.control.intField
import com.hendraanggrian.openpss.time.FlexibleInterval
import com.hendraanggrian.openpss.time.PATTERN_DATETIME_EXTENDED
import com.hendraanggrian.openpss.ui.Controller
import com.hendraanggrian.openpss.ui.DateTimeDialog
import com.hendraanggrian.openpss.ui.controller
import com.hendraanggrian.openpss.ui.pane
import com.hendraanggrian.openpss.ui.wage.WageRecordController.Companion.EXTRA_ATTENDEES
import com.hendraanggrian.openpss.ui.wage.WageRecordController.Companion.EXTRA_STAGE
import com.hendraanggrian.openpss.ui.wage.readers.Reader
import com.hendraanggrian.openpss.util.getColor
import com.hendraanggrian.openpss.util.getResource
import com.hendraanggrian.openpss.util.isDelete
import com.hendraanggrian.openpss.util.round
import javafx.fxml.FXML
import javafx.fxml.FXMLLoader
import javafx.geometry.Pos.BOTTOM_CENTER
import javafx.geometry.Pos.TOP_CENTER
import javafx.scene.Scene
import javafx.scene.control.Button
import javafx.scene.control.ChoiceBox
import javafx.scene.control.ContentDisplay.RIGHT
import javafx.scene.control.Label
import javafx.scene.control.ListView
import javafx.scene.control.MenuItem
import javafx.scene.control.ScrollPane
import javafx.scene.control.TitledPane
import javafx.scene.control.ToggleButton
import javafx.scene.image.Image
import javafx.scene.input.MouseEvent.MOUSE_CLICKED
import javafx.scene.layout.FlowPane
import javafx.scene.layout.Pane
import javafx.scene.layout.Priority.ALWAYS
import javafx.scene.layout.StackPane
import javafx.scene.text.Font.font
import javafx.stage.Modality.APPLICATION_MODAL
import kotfx.application.later
import kotfx.beans.binding.bindingOf
import kotfx.beans.binding.booleanBindingOf
import kotfx.beans.binding.lessEq
import kotfx.beans.binding.or
import kotfx.beans.binding.stringBindingOf
import kotfx.collections.emptyBinding
import kotfx.collections.sizeBinding
import kotfx.collections.sort
import kotfx.coroutines.FX
import kotfx.coroutines.listener
import kotfx.coroutines.onAction
import kotfx.coroutines.onKeyPressed
import kotfx.layouts.borderPane
import kotfx.layouts.checkBox
import kotfx.layouts.contextMenu
import kotfx.layouts.gridPane
import kotfx.layouts.imageView
import kotfx.layouts.label
import kotfx.layouts.listView
import kotfx.layouts.menuItem
import kotfx.layouts.separatorMenuItem
import kotfx.layouts.titledPane
import kotfx.layouts.vbox
import kotfx.listeners.cellFactory
import kotfx.scene.control.errorAlert
import kotfx.scene.layout.gaps
import kotfx.scene.layout.maxSize
import kotfx.scene.layout.paddings
import kotfx.scene.layout.prefSize
import kotfx.stage.fileChooser
import kotfx.stage.minSize
import kotfx.stage.stage
import kotlinx.coroutines.experimental.delay
import kotlinx.coroutines.experimental.launch
import org.joda.time.DateTime
import org.joda.time.DateTime.now
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
        processButton.disableProperty().bind(flowPane.children.emptyBinding())

        if (DEBUG) {
            fileField.text = "/Users/hendraanggrian/Downloads/Absen 2-24-18.xlsx"
            readButton.fire()
        }
        later { flowPane.prefWrapLengthProperty().bind(fileField.scene.widthProperty()) }
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

    @FXML fun browse() = fileChooser(getString(R.string.input_file), *readerChoiceBox.value.extensions).showOpenDialog(fileField.scene.window)?.run { fileField.text = absolutePath }

    @FXML
    fun read() {
        scrollPane.content = borderPane {
            prefWidthProperty().bind(scrollPane.widthProperty())
            prefHeightProperty().bind(scrollPane.heightProperty())
            center = kotfx.layouts.progressIndicator { maxSize = 128 }
        }
        flowPane.children.clear()
        launch {
            try {
                readerChoiceBox.value.read(fileField.file).forEach { attendee ->
                    if (mergeToggleButton.isSelected) attendee.mergeDuplicates()
                    launch(FX) {
                        flowPane.children += titledPane(attendee.toString()) {
                            lateinit var listView: ListView<DateTime>
                            lateinit var deleteMenu: MenuItem
                            userData = attendee
                            isCollapsible = false
                            content = vbox {
                                gridPane {
                                    gaps = 4
                                    paddings = 8
                                    attendee.role?.let { role ->
                                        label(getString(R.string.role)) col 0 row 0 rightMargin 4
                                        label(role) col 1 row 0 colSpan 2
                                    }
                                    label(getString(R.string.income)) col 0 row 1 rightMargin 4
                                    intField {
                                        prefSize(width = 88)
                                        promptText = getString(R.string.income)
                                        valueProperty.bindBidirectional(attendee.dailyProperty)
                                    } col 1 row 1
                                    label("@${getString(R.string.day)}") { font = font(9.0) } col 2 row 1
                                    label(getString(R.string.overtime)) col 0 row 2 rightMargin 4
                                    intField {
                                        prefSize(width = 88)
                                        promptText = getString(R.string.overtime)
                                        valueProperty.bindBidirectional(attendee.hourlyOvertimeProperty)
                                    } col 1 row 2
                                    label("@${getString(R.string.hour)}") { font = font(9.0) } col 2 row 2
                                    label(getString(R.string.recess)) col 0 row 3 rightMargin 4
                                    vbox {
                                        transaction {
                                            Recesses.find().forEach { recess ->
                                                checkBox(recess.toString()) {
                                                    selectedProperty().listener { _, _, selected ->
                                                        (this@titledPane.userData as Attendee).recesses.let { recesses ->
                                                            if (selected) recesses.add(recess) else recesses.remove(recess)
                                                        }
                                                    }
                                                    isSelected = true
                                                } topMargin if (children.size > 1) 4 else 0
                                            }
                                        }
                                    } col 1 row 3 colSpan 2
                                }
                                listView = listView(attendee.attendances) {
                                    prefSize(width = 128)
                                    cellFactory {
                                        onUpdateItem { dateTime, empty ->
                                            if (dateTime != null && !empty) graphic = kotfx.layouts.hbox {
                                                val index = listView.items.indexOf(dateTime)
                                                alignment = if (index % 2 == 0) BOTTOM_CENTER else TOP_CENTER
                                                val itemLabel = label(dateTime.toString(PATTERN_DATETIME_EXTENDED)) { maxWidth = Double.MAX_VALUE } hpriority ALWAYS
                                                if (alignment == BOTTOM_CENTER) listView.items.getOrNull(index + 1).let { nextItem ->
                                                    when (nextItem) {
                                                        null -> itemLabel.textFill = getColor(R.color.red)
                                                        else -> label(FlexibleInterval(dateTime, nextItem).hours.round().toString()) { font = font(9.0) }
                                                    }
                                                }
                                            }
                                        }
                                    }
                                    onKeyPressed {
                                        if (it.code.isDelete() && selectionModel.selectedItem != null) selectionModel.run {
                                            listView.items.remove(selectedItem)
                                            clearSelection()
                                        }
                                    }
                                }
                            }
                            contextMenu {
                                menuItem(getString(R.string.add)) {
                                    onAction {
                                        val prefill = listView.selectionModel.selectedItem ?: now()
                                        DateTimeDialog(this@WageController, getString(R.string.add_record), prefill.minusMinutes(prefill.minuteOfHour))
                                            .showAndWait()
                                            .ifPresent {
                                                listView.items.add(it)
                                                listView.items.sort()
                                            }
                                    }
                                }
                                menuItem(getString(R.string.edit)) {
                                    disableProperty().bind(listView.selectionModel.selectedItems.emptyBinding())
                                    onAction {
                                        DateTimeDialog(this@WageController, getString(R.string.edit_record), listView.selectionModel.selectedItem)
                                            .showAndWait()
                                            .ifPresent {
                                                listView.items[listView.selectionModel.selectedIndex] = it
                                                listView.items.sort()
                                            }
                                    }
                                }
                                menuItem(getString(R.string.delete)) {
                                    disableProperty().bind(listView.selectionModel.selectedItems.emptyBinding())
                                    onAction {
                                        listView.selectionModel.run {
                                            listView.items.remove(selectedItem)
                                            clearSelection()
                                        }
                                    }
                                }
                                separatorMenuItem()
                                menuItem(getString(R.string.revert)) { onAction { attendee.attendances.revert() } }
                                separatorMenuItem()
                                deleteMenu = menuItem("${getString(R.string.delete)} ${attendee.name}") {
                                    onAction {
                                        flowPane.children.remove(this@titledPane)
                                        rebindProcessButton()
                                    }
                                }
                                menuItem(getString(R.string.delete_others)) {
                                    disableProperty().bind(flowPane.children.sizeBinding() lessEq 1)
                                    onAction {
                                        flowPane.children.removeAll(flowPane.children.toMutableList().apply { remove(this@titledPane) })
                                        rebindProcessButton()
                                    }
                                }
                                menuItem(getString(R.string.delete_employees_to_the_right)) {
                                    disableProperty().bind(booleanBindingOf(flowPane.children) { flowPane.children.indexOf(this@titledPane) == flowPane.children.lastIndex })
                                    onAction {
                                        flowPane.children.removeAll(flowPane.children.toList().takeLast(flowPane.children.lastIndex - flowPane.children.indexOf(this@titledPane)))
                                        rebindProcessButton()
                                    }
                                }
                            }
                            contentDisplay = RIGHT
                            graphic = imageView {
                                imageProperty().bind(bindingOf(hoverProperty()) { Image(if (isHover) R.image.btn_clear_active else R.image.btn_clear_inactive) })
                                addEventHandler(MOUSE_CLICKED) {
                                    it.consume()
                                    deleteMenu.fire()
                                }
                            }
                            launch(FX) {
                                delay(100)
                                applyCss()
                                layout()
                                val titleRegion = lookup(".title")
                                val padding = (titleRegion as StackPane).padding
                                val graphicWidth = graphic.layoutBounds.width
                                val labelWidth = titleRegion.lookup(".text").layoutBounds.width
                                graphicTextGap = width - graphicWidth - padding.left - padding.right - labelWidth
                            }
                        }
                    }
                }
                launch(FX) {
                    scrollPane.content = flowPane
                    rebindProcessButton()
                }
            } catch (e: Exception) {
                if (DEBUG) e.printStackTrace()
                launch(FX) {
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
            minSize(1000, 650)
            loader.controller.addExtra(EXTRA_ATTENDEES, attendees).addExtra(EXTRA_STAGE, this)
        }.showAndWait()
    }

    /** Employees are stored in flowpane childrens' user model. */
    private val attendees: List<Attendee> get() = flowPane.children.map { it.userData as Attendee }

    /** As attendees are populated, process button need to be rebinded according to new requirements. */
    private fun rebindProcessButton() = processButton.disableProperty().bind(flowPane.children.emptyBinding() or booleanBindingOf(flowPane.children, *flowPane.children.map { (it as TitledPane).content }.map { (it as Pane).children[1] as ListView<*> }.map { it.items }.toTypedArray()) {
        attendees.any { it.attendances.size % 2 != 0 }
    })
}