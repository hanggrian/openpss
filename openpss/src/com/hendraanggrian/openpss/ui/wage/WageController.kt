package com.hendraanggrian.openpss.ui.wage

import com.hendraanggrian.openpss.App
import com.hendraanggrian.openpss.BuildConfig.DEBUG
import com.hendraanggrian.openpss.R
import com.hendraanggrian.openpss.control.SegmentedTabPane.Companion.STRETCH_POINT
import com.hendraanggrian.openpss.control.space
import com.hendraanggrian.openpss.control.stretchableButton
import com.hendraanggrian.openpss.io.WageDirectory
import com.hendraanggrian.openpss.io.properties.PreferencesFile.WAGE_READER
import com.hendraanggrian.openpss.ui.SegmentedController
import com.hendraanggrian.openpss.ui.controller
import com.hendraanggrian.openpss.ui.pane
import com.hendraanggrian.openpss.ui.wage.readers.Reader
import com.hendraanggrian.openpss.ui.wage.record.WageRecordController.Companion.EXTRA_ATTENDEES
import com.hendraanggrian.openpss.util.desktop
import com.hendraanggrian.openpss.util.getResource
import com.hendraanggrian.openpss.util.getStyle
import javafx.fxml.FXML
import javafx.fxml.FXMLLoader
import javafx.scene.control.Button
import javafx.scene.control.TitledPane
import javafx.scene.image.ImageView
import javafx.scene.layout.AnchorPane
import javafx.scene.layout.FlowPane
import javafx.stage.FileChooser.ExtensionFilter
import kotlinx.coroutines.experimental.Dispatchers
import kotlinx.coroutines.experimental.GlobalScope
import kotlinx.coroutines.experimental.javafx.JavaFx
import kotlinx.coroutines.experimental.launch
import ktfx.NodeManager
import ktfx.application.later
import ktfx.beans.binding.booleanBindingOf
import ktfx.beans.binding.stringBindingOf
import ktfx.beans.value.lessEq
import ktfx.beans.value.or
import ktfx.collections.isEmpty
import ktfx.collections.size
import ktfx.coroutines.onAction
import ktfx.layouts.borderPane
import ktfx.layouts.label
import ktfx.layouts.scene
import ktfx.scene.control.errorAlert
import ktfx.scene.layout.maxSize
import ktfx.stage.fileChooser
import ktfx.stage.setMinSize
import ktfx.stage.stage
import java.io.File
import java.net.URL
import java.util.ResourceBundle

class WageController : SegmentedController() {

    @FXML lateinit var anchorPane: AnchorPane
    @FXML lateinit var titledPane: TitledPane
    @FXML lateinit var flowPane: FlowPane

    private lateinit var browseButton: Button
    private lateinit var disableRecessButton: Button
    private lateinit var saveWageButton: Button
    private lateinit var processButton: Button
    private lateinit var historyButton: Button

    override fun NodeManager.onCreateLeftActions() {
        browseButton = stretchableButton(
            STRETCH_POINT,
            getString(R.string.browse),
            ImageView(R.image.btn_browse_light)
        ) {
            onAction { browse() }
        }
        space()
        disableRecessButton = stretchableButton(
            STRETCH_POINT,
            getString(R.string.disable_recess),
            ImageView(R.image.btn_disable_recess_light)
        ) {
            disableProperty().bind(flowPane.children.isEmpty)
            onAction { disableRecess() }
        }
        saveWageButton = stretchableButton(
            STRETCH_POINT,
            getString(R.string.save_wage),
            ImageView(R.image.btn_save_light)
        ) {
            disableProperty().bind(flowPane.children.isEmpty)
            onAction { saveWage() }
        }
    }

    override fun NodeManager.onCreateRightActions() {
        processButton = stretchableButton(
            STRETCH_POINT,
            getString(R.string.process),
            ImageView(R.image.btn_process_dark)
        ) {
            styleClass += App.STYLE_DEFAULT_BUTTON
            isDisable = true
            onAction { process() }
        }
        space()
        historyButton = stretchableButton(
            STRETCH_POINT,
            getString(R.string.history),
            ImageView(R.image.btn_history_light)
        ) {
            onAction { history() }
        }
    }

    override fun initialize(location: URL, resources: ResourceBundle) {
        super.initialize(location, resources)
        titledPane.textProperty().bind(stringBindingOf(flowPane.children) {
            "${flowPane.children.size} ${getString(R.string.employee)}"
        })

        later { flowPane.prefWrapLengthProperty().bind(flowPane.scene.widthProperty()) }
        if (DEBUG) {
            val file = File("/Users/hendraanggrian/Downloads/Absen 4-13-18.xlsx")
            if (file.exists()) read(file)
        }
    }

    private fun disableRecess() = DisableRecessPopover(this, attendeePanes).showAt(disableRecessButton)

    private fun saveWage() = attendees.forEach { it.saveWage() }

    private fun process() = stage(getString(R.string.record)) {
        val loader = FXMLLoader(getResource(R.layout.controller_wage_record), resources)
        scene = scene {
            loader.pane()
            stylesheets += getStyle(R.style.openpss)
        }
        setMinSize(1000.0, 650.0)
        loader.controller.addExtra(EXTRA_ATTENDEES, attendees)
    }.showAndWait()

    private fun history() = desktop?.open(WageDirectory)

    private fun browse() =
        fileChooser(ExtensionFilter(getString(R.string.input_file), *Reader.of(WAGE_READER).extensions))
            .showOpenDialog(anchorPane.scene.window)
            ?.let { read(it) }

    private fun read(file: File) {
        titledPane.graphic = label("${file.absolutePath} -")
        val loadingPane = borderPane {
            prefWidthProperty().bind(titledPane.widthProperty())
            prefHeightProperty().bind(titledPane.heightProperty())
            center = ktfx.layouts.progressIndicator { maxSize = 128.0 }
        }
        anchorPane.children += loadingPane
        flowPane.children.clear()
        GlobalScope.launch(Dispatchers.Default) {
            try {
                Reader.of(WAGE_READER).read(file).forEach { attendee ->
                    attendee.mergeDuplicates()
                    GlobalScope.launch(Dispatchers.JavaFx) {
                        flowPane.children += attendeePane(this@WageController, attendee) {
                            deleteMenu.onAction {
                                flowPane.children -= this@attendeePane
                                bindSaveAndProcessButton()
                            }
                            deleteOthersMenu.run {
                                disableProperty().bind(flowPane.children.size() lessEq 1)
                                onAction {
                                    flowPane.children -= flowPane.children.toMutableList().apply {
                                        remove(this@attendeePane)
                                    }
                                    bindSaveAndProcessButton()
                                }
                            }
                            deleteToTheRightMenu.run {
                                disableProperty().bind(booleanBindingOf(flowPane.children) {
                                    flowPane.children.indexOf(this@attendeePane) == flowPane.children.lastIndex
                                })
                                onAction {
                                    flowPane.children -= flowPane.children.toList().takeLast(
                                        flowPane.children.lastIndex - flowPane.children.indexOf(this@attendeePane)
                                    )
                                    bindSaveAndProcessButton()
                                }
                            }
                        }
                    }
                }
                GlobalScope.launch(Dispatchers.JavaFx) {
                    anchorPane.children -= loadingPane
                    bindSaveAndProcessButton()
                }
            } catch (e: Exception) {
                if (DEBUG) e.printStackTrace()
                GlobalScope.launch(Dispatchers.JavaFx) {
                    anchorPane.children -= loadingPane
                    bindSaveAndProcessButton()
                    errorAlert(e.message.toString()) {
                        dialogPane.stylesheets += getStyle(R.style.openpss)
                    }.show()
                }
            }
        }
    }

    private inline val attendeePanes: List<AttendeePane> get() = flowPane.children.map { (it as AttendeePane) }

    private inline val attendees: List<Attendee> get() = attendeePanes.map { it.attendee }

    /** As attendees are populated, saveAndProcess button need to be rebinded according to new requirements. */
    private fun bindSaveAndProcessButton() = processButton.disableProperty().bind(flowPane.children.isEmpty or
        booleanBindingOf(flowPane.children, *flowPane.children
            .map { (it as AttendeePane).attendanceList.items }
            .toTypedArray()) { attendees.any { it.attendances.size % 2 != 0 } })
}