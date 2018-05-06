package com.hendraanggrian.openpss.ui.wage

import com.hendraanggrian.openpss.App.Companion.STYLE_DEFAULT_BUTTON
import com.hendraanggrian.openpss.BuildConfig.DEBUG
import com.hendraanggrian.openpss.R
import com.hendraanggrian.openpss.controls.FileField
import com.hendraanggrian.openpss.io.WageFolder
import com.hendraanggrian.openpss.io.properties.SettingsFile.WAGE_READER
import com.hendraanggrian.openpss.ui.SegmentedController
import com.hendraanggrian.openpss.ui.wage.readers.Reader
import com.hendraanggrian.openpss.ui.wage.record.WageRecordController.Companion.EXTRA_ATTENDEES
import com.hendraanggrian.openpss.util.controller
import com.hendraanggrian.openpss.util.getFont
import com.hendraanggrian.openpss.util.getResource
import com.hendraanggrian.openpss.util.getStyle
import com.hendraanggrian.openpss.util.openFile
import com.hendraanggrian.openpss.util.pane
import javafx.fxml.FXML
import javafx.fxml.FXMLLoader
import javafx.geometry.Orientation.VERTICAL
import javafx.scene.Node
import javafx.scene.control.Button
import javafx.scene.control.Label
import javafx.scene.control.ScrollPane
import javafx.scene.image.ImageView
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
import ktfx.layouts.button
import ktfx.layouts.separator
import ktfx.layouts.styledButton
import ktfx.layouts.styledScene
import ktfx.scene.control.styledErrorAlert
import ktfx.scene.layout.maxSize
import ktfx.stage.fileChooser
import ktfx.stage.setMinSize
import ktfx.stage.stage
import java.net.URL
import java.util.ResourceBundle

class WageController : SegmentedController() {

    @FXML lateinit var fileField: FileField
    @FXML lateinit var employeeCountLabel1: Label
    @FXML lateinit var employeeCountLabel2: Label
    @FXML lateinit var scrollPane: ScrollPane
    @FXML lateinit var flowPane: FlowPane

    private lateinit var readButton: Button
    private lateinit var processButton: Button
    private lateinit var disableRecessButton: Button
    override val leftButtons: List<Node>
        get() = listOf(readButton, processButton, separator(VERTICAL), disableRecessButton)

    private lateinit var recessButton: Button
    private lateinit var historyButton: Button
    override val rightButtons: List<Node> get() = listOf(recessButton, historyButton)

    override fun initialize(location: URL, resources: ResourceBundle) {
        super.initialize(location, resources)
        readButton = button(getString(R.string.read), ImageView(R.image.btn_attendee)) {
            onAction { read() }
            disableProperty().bind(fileField.validProperty)
        }
        processButton = styledButton(STYLE_DEFAULT_BUTTON, getString(R.string.process),
            ImageView(R.image.btn_process_dark)) {
            onAction { process() }
            disableProperty().bind(flowPane.children.isEmpty)
        }
        disableRecessButton = button(getString(R.string.disable_recess), ImageView(R.image.btn_disable_recess)) {
            onAction { disableRecess() }
            disableProperty().bind(flowPane.children.isEmpty)
        }
        recessButton = button(getString(R.string.recess), ImageView(R.image.btn_recess)) {
            onAction { recess() }
        }
        historyButton = button(getString(R.string.history), ImageView(R.image.btn_history)) {
            onAction { history() }
        }
        employeeCountLabel1.font = getFont(R.font.sf_pro_text_bold)
        employeeCountLabel2.textProperty().bind(flowPane.children.size().asString())

        if (DEBUG) {
            fileField.text = "/Users/hendraanggrian/Downloads/Absen 4-13-18.xlsx"
            // readButton.fire()
        }
        later {
            flowPane
                .prefWrapLengthProperty()
                .bind(fileField
                    .scene
                    .widthProperty()) // TODO: fix occasional NPE
        }
    }

    private fun read() {
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
                    styledErrorAlert(getStyle(R.style.openpss), e.message.toString()).show()
                }
            }
        }
    }

    private fun process() {
        attendees.forEach { it.saveWage() }
        stage(getString(R.string.record)) {
            val loader = FXMLLoader(getResource(R.layout.controller_wage_record), resources)
            scene = styledScene(getStyle(R.style.openpss), loader.pane)
            setMinSize(1000.0, 650.0)
            loader.controller.addExtra(EXTRA_ATTENDEES, attendees)
        }.showAndWait()
    }

    private fun disableRecess() = DisableRecessPopup(this, attendeePanes).show(disableRecessButton)

    private fun recess() = stage(getString(R.string.recess)) {
        val loader = FXMLLoader(getResource(R.layout.controller_wage_recess), resources)
        initModality(APPLICATION_MODAL)
        scene = styledScene(getStyle(R.style.openpss), loader.pane)
        isResizable = false
        loader.controller.login = login
    }.showAndWait()

    private fun history() = openFile(WageFolder)

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