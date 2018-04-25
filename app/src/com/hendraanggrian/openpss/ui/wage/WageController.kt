package com.hendraanggrian.openpss.ui.wage

import com.hendraanggrian.openpss.BuildConfig.DEBUG
import com.hendraanggrian.openpss.R
import com.hendraanggrian.openpss.controls.FileField
import com.hendraanggrian.openpss.io.WageFolder
import com.hendraanggrian.openpss.io.properties.SettingsFile.WAGE_READER
import com.hendraanggrian.openpss.ui.Controller
import com.hendraanggrian.openpss.ui.wage.readers.Reader
import com.hendraanggrian.openpss.ui.wage.record.WageRecordController.Companion.EXTRA_ATTENDEES
import com.hendraanggrian.openpss.util.controller
import com.hendraanggrian.openpss.util.getFont
import com.hendraanggrian.openpss.util.getResource
import com.hendraanggrian.openpss.util.openFile
import com.hendraanggrian.openpss.util.pane
import com.hendraanggrian.openpss.util.style
import javafx.fxml.FXML
import javafx.fxml.FXMLLoader
import javafx.scene.Scene
import javafx.scene.control.Button
import javafx.scene.control.Label
import javafx.scene.control.ScrollPane
import javafx.scene.layout.FlowPane
import javafx.stage.FileChooser.ExtensionFilter
import javafx.stage.Modality.APPLICATION_MODAL
import kotlinx.coroutines.experimental.launch
import ktfx.application.later
import ktfx.beans.binding.booleanBindingOf
import ktfx.beans.value.lessEq
import ktfx.beans.value.or
import ktfx.collections.isEmpty
import ktfx.collections.size
import ktfx.coroutines.FX
import ktfx.coroutines.onAction
import ktfx.layouts.borderPane
import ktfx.scene.control.errorAlert
import ktfx.scene.layout.maxSize
import ktfx.stage.fileChooser
import ktfx.stage.setMinSize
import ktfx.stage.stage
import java.net.URL
import java.util.ResourceBundle

class WageController : Controller() {

    @FXML lateinit var readButton: Button
    @FXML lateinit var processButton: Button
    @FXML lateinit var disableRecessButton: Button
    @FXML lateinit var fileField: FileField
    @FXML lateinit var employeeCountLabel1: Label
    @FXML lateinit var employeeCountLabel2: Label
    @FXML lateinit var scrollPane: ScrollPane
    @FXML lateinit var flowPane: FlowPane

    override fun initialize(location: URL, resources: ResourceBundle) {
        super.initialize(location, resources)
        disableRecessButton.disableProperty().bind(flowPane.children.isEmpty)
        employeeCountLabel1.font = getFont(R.font.sf_pro_text_bold)
        employeeCountLabel2.textProperty().bind(flowPane.children.size().asString())
        readButton.disableProperty().bind(fileField.validProperty)
        processButton.disableProperty().bind(flowPane.children.isEmpty)

        if (DEBUG) {
            fileField.text = "/Users/hendraanggrian/Downloads/Absen 4-13-18.xlsx"
            // readButton.fire()
        }
        later { flowPane.prefWrapLengthProperty().bind(fileField.scene.widthProperty()) }
    }

    @FXML fun read() {
        scrollPane.content = borderPane {
            prefWidthProperty().bind(scrollPane.widthProperty())
            prefHeightProperty().bind(scrollPane.heightProperty())
            center = ktfx.layouts.progressIndicator { maxSize = 128.0 }
        }
        flowPane.children.clear()
        launch {
            try {
                Reader.of(WAGE_READER).read(fileField.value).forEach { attendee ->
                    attendee.mergeDuplicates()
                    launch(FX) {
                        flowPane.children += attendeePane(this@WageController, attendee) {
                            deleteMenu.onAction {
                                flowPane.children -= this@attendeePane
                                bindProcessButton()
                            }
                            deleteOthersMenu.disableProperty().bind(flowPane.children.size() lessEq 1)
                            deleteOthersMenu.onAction {
                                flowPane.children -= flowPane.children.toMutableList().apply {
                                    remove(this@attendeePane)
                                }
                                bindProcessButton()
                            }
                            deleteToTheRightMenu.disableProperty().bind(booleanBindingOf(flowPane.children) {
                                flowPane.children.indexOf(this@attendeePane) == flowPane.children.lastIndex
                            })
                            deleteToTheRightMenu.onAction {
                                flowPane.children -= flowPane.children.toList().takeLast(
                                    flowPane.children.lastIndex - flowPane.children.indexOf(this@attendeePane))
                                bindProcessButton()
                            }
                        }
                    }
                }
                launch(FX) {
                    scrollPane.content = flowPane
                    bindProcessButton()
                }
            } catch (e: Exception) {
                if (DEBUG) e.printStackTrace()
                launch(FX) {
                    scrollPane.content = flowPane
                    bindProcessButton()
                    errorAlert(e.message.toString()) { style() }.show()
                }
            }
        }
    }

    @FXML fun process() {
        attendees.forEach { it.saveWage() }
        stage(getString(R.string.record)) {
            val loader = FXMLLoader(getResource(R.layout.controller_wage_record), resources)
            scene = Scene(loader.pane).apply { style() }
            setMinSize(1000.0, 650.0)
            loader.controller.addExtra(EXTRA_ATTENDEES, attendees)
        }.showAndWait()
    }

    @FXML fun disableRecess() = DisableRecessDialog(this, attendeePanes).show()

    @FXML fun recess() = stage(getString(R.string.recess)) {
        val loader = FXMLLoader(getResource(R.layout.controller_wage_recess), resources)
        initModality(APPLICATION_MODAL)
        scene = Scene(loader.pane).apply { style() }
        isResizable = false
        loader.controller._employee = _employee
    }.showAndWait()

    @FXML fun history() = openFile(WageFolder)

    @FXML fun browse() = fileChooser(
        ExtensionFilter(getString(R.string.input_file), *Reader.of(WAGE_READER).extensions))
        .showOpenDialog(fileField.scene.window)
        ?.run {
            fileField.text = absolutePath
            fileField.requestFocus()
        }

    private inline val attendeePanes: List<AttendeePane> get() = flowPane.children.map { (it as AttendeePane) }

    private inline val attendees: List<Attendee> get() = attendeePanes.map { it.attendee }

    /** As attendees are populated, process button need to be rebinded according to new requirements. */
    private fun bindProcessButton() = processButton.disableProperty().bind(flowPane.children.isEmpty or
        booleanBindingOf(flowPane.children, *flowPane.children
            .map { (it as AttendeePane).attendanceList.items }
            .toTypedArray()) { attendees.any { it.attendances.size % 2 != 0 } })
}