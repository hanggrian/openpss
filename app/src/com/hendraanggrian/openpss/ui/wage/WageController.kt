package com.hendraanggrian.openpss.ui.wage

import com.hendraanggrian.openpss.BuildConfig.DEBUG
import com.hendraanggrian.openpss.R
import com.hendraanggrian.openpss.db.schema.Recesses
import com.hendraanggrian.openpss.db.transaction
import com.hendraanggrian.openpss.io.WageFolder
import com.hendraanggrian.openpss.scene.control.FileField
import com.hendraanggrian.openpss.ui.Controller
import com.hendraanggrian.openpss.ui.controller
import com.hendraanggrian.openpss.ui.pane
import com.hendraanggrian.openpss.ui.wage.WageRecordController.Companion.EXTRA_ATTENDEES
import com.hendraanggrian.openpss.ui.wage.WageRecordController.Companion.EXTRA_STAGE
import com.hendraanggrian.openpss.ui.wage.readers.Reader
import com.hendraanggrian.openpss.util.getResource
import com.hendraanggrian.openpss.util.openFile
import javafx.fxml.FXML
import javafx.fxml.FXMLLoader
import javafx.scene.Scene
import javafx.scene.control.Button
import javafx.scene.control.ButtonType.OK
import javafx.scene.control.ChoiceBox
import javafx.scene.control.Label
import javafx.scene.control.ListView
import javafx.scene.control.ScrollPane
import javafx.scene.control.Separator
import javafx.scene.control.TitledPane
import javafx.scene.control.ToggleButton
import javafx.scene.image.ImageView
import javafx.scene.layout.FlowPane
import javafx.scene.layout.Pane
import javafx.stage.Modality.APPLICATION_MODAL
import kfx.application.later
import kfx.beans.binding.booleanBindingOf
import kfx.beans.binding.lessEq
import kfx.beans.binding.or
import kfx.beans.binding.stringBindingOf
import kfx.collections.emptyBinding
import kfx.collections.mutableObservableListOf
import kfx.collections.sizeBinding
import kfx.collections.toObservableList
import kfx.coroutines.FX
import kfx.coroutines.onAction
import kfx.layouts.borderPane
import kfx.layouts.choiceBox
import kfx.layouts.gridPane
import kfx.layouts.label
import kfx.scene.control.cancelButton
import kfx.scene.control.dialog
import kfx.scene.control.errorAlert
import kfx.scene.control.okButton
import kfx.scene.layout.gaps
import kfx.scene.layout.size
import kfx.stage.fileChooser
import kfx.stage.setSizeMax
import kfx.stage.stage
import kotlinx.coroutines.experimental.launch

class WageController : Controller() {

    @FXML lateinit var fileField: FileField
    @FXML lateinit var readerChoiceBox: ChoiceBox<Reader>
    @FXML lateinit var mergeToggleButton: ToggleButton
    @FXML lateinit var recessOffButton: Button
    @FXML lateinit var scrollPane: ScrollPane
    @FXML lateinit var flowPane: FlowPane
    @FXML lateinit var employeeCountLabel: Label
    @FXML lateinit var readButton: Button
    @FXML lateinit var processButton: Button

    override fun initialize() {
        readerChoiceBox.items = Reader.listAll()
        if (readerChoiceBox.items.isNotEmpty()) readerChoiceBox.selectionModel.select(0)

        recessOffButton.disableProperty().bind(flowPane.children.emptyBinding())
        employeeCountLabel.textProperty().bind(stringBindingOf(flowPane.children) {
            "${flowPane.children.size} ${getString(R.string.employee)}"
        })
        readButton.disableProperty().bind(fileField.validProperty)
        processButton.disableProperty().bind(flowPane.children.emptyBinding())

        if (DEBUG) {
            fileField.text = "/Users/hendraanggrian/Downloads/Absen 2-24-18.xlsx"
            readButton.fire()
        }
        later { flowPane.prefWrapLengthProperty().bind(fileField.scene.widthProperty()) }
    }

    @FXML fun recess() = stage(getString(R.string.recess)) {
        val loader = FXMLLoader(getResource(R.layout.controller_wage_recess), resources)
        initModality(APPLICATION_MODAL)
        scene = Scene(loader.pane)
        isResizable = false
        loader.controller._employee = _employee
    }.showAndWait()

    @FXML fun history() = openFile(WageFolder)

    @FXML fun browse() = fileChooser(getString(R.string.input_file), *readerChoiceBox.value.extensions)
        .showOpenDialog(fileField.scene.window)?.run { fileField.text = absolutePath }

    @FXML fun recessOff() = dialog<Pair<Any, Any>>(getString(R.string.disable_recess), ImageView(R.image.ic_clock)) {
        lateinit var recessChoice: ChoiceBox<*>
        lateinit var roleChoice: ChoiceBox<*>
        dialogPane.content = gridPane {
            gaps = 8
            label(getString(R.string.recess)) col 0 row 0
            transaction {
                recessChoice = choiceBox(mutableObservableListOf(getString(R.string.all),
                    Separator(),
                    *Recesses.find().toObservableList().toTypedArray())) col 1 row 0
            }
            label(getString(R.string.employee)) col 0 row 1
            roleChoice = choiceBox(mutableObservableListOf(
                *attendees.filter { it.role != null }.map { it.role!! }.distinct().toTypedArray(),
                Separator(),
                *attendees.toTypedArray())) col 1 row 1
        }
        cancelButton()
        okButton { disableProperty().bind(recessChoice.valueProperty().isNull or roleChoice.valueProperty().isNull) }
        setResultConverter { if (it == OK) Pair(recessChoice.value, roleChoice.value) else null }
    }.showAndWait().ifPresent { (recess, role) ->
        attendeePanes.filter {
            when (role) {
                is String -> it.attendee.role == role
                else -> it.attendee == role as Attendee
            }
        }.forEach {
            when (recess) {
                is String -> it.recessChecks
                else -> it.recessChecks.filter { it.text == recess.toString() }
            }.forEach { it.isSelected = false }
        }
    }

    @FXML fun read() {
        scrollPane.content = borderPane {
            prefWidthProperty().bind(scrollPane.widthProperty())
            prefHeightProperty().bind(scrollPane.heightProperty())
            center = kfx.layouts.progressIndicator { size = 128 }
        }
        flowPane.children.clear()
        launch {
            try {
                readerChoiceBox.value.read(fileField.file).forEach { attendee ->
                    if (mergeToggleButton.isSelected) attendee.mergeDuplicates()
                    launch(FX) {
                        flowPane.children += attendeePane(this@WageController, attendee) {
                            deleteMenu.onAction {
                                flowPane.children.remove(this@attendeePane)
                                rebindProcessButton()
                            }
                            deleteOthersMenu.disableProperty().bind(flowPane.children.sizeBinding() lessEq 1)
                            deleteOthersMenu.onAction {
                                flowPane.children.removeAll(flowPane.children.toMutableList().apply {
                                    remove(this@attendeePane)
                                })
                                rebindProcessButton()
                            }
                            deleteToTheRightMenu.disableProperty().bind(booleanBindingOf(flowPane.children) {
                                flowPane.children.indexOf(this@attendeePane) == flowPane.children.lastIndex
                            })
                            deleteToTheRightMenu.onAction {
                                flowPane.children.removeAll(flowPane.children.toList().takeLast(
                                    flowPane.children.lastIndex - flowPane.children.indexOf(this@attendeePane)))
                                rebindProcessButton()
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

    @FXML fun process() {
        attendees.forEach { it.saveWage() }
        stage {
            val loader = FXMLLoader(getResource(R.layout.controller_wage_record), resources)
            scene = Scene(loader.pane)
            setSizeMax(1000, 650)
            loader.controller.addExtra(EXTRA_ATTENDEES, attendees).addExtra(EXTRA_STAGE, this)
        }.showAndWait()
    }

    private inline val attendeePanes: List<AttendeePane> get() = flowPane.children.map { (it as AttendeePane) }

    private inline val attendees: List<Attendee> get() = attendeePanes.map { it.attendee }

    /** As attendees are populated, process button need to be rebinded according to new requirements. */
    private fun rebindProcessButton() = processButton.disableProperty().bind(flowPane.children.emptyBinding() or
        booleanBindingOf(flowPane.children, *flowPane.children
            .map { (it as TitledPane).content }
            .map { (it as Pane).children[1] as ListView<*> }
            .map { it.items }.toTypedArray()) {
            attendees.any { it.attendances.size % 2 != 0 }
        })
}