package com.hendraanggrian.openpss.ui.wage

import com.hendraanggrian.openpss.App.Companion.STRETCH_POINT
import com.hendraanggrian.openpss.BuildConfig.DEBUG
import com.hendraanggrian.openpss.R
import com.hendraanggrian.openpss.control.space
import com.hendraanggrian.openpss.control.stretchableButton
import com.hendraanggrian.openpss.io.WageDirectory
import com.hendraanggrian.openpss.io.properties.PreferencesFile.WAGE_READER
import com.hendraanggrian.openpss.ui.ActionController
import com.hendraanggrian.openpss.ui.controller
import com.hendraanggrian.openpss.ui.pane
import com.hendraanggrian.openpss.ui.wage.readers.Reader
import com.hendraanggrian.openpss.ui.wage.record.WageRecordController.Companion.EXTRA_ATTENDEES
import com.hendraanggrian.openpss.util.desktop
import com.hendraanggrian.openpss.util.getResource
import com.hendraanggrian.openpss.util.getStyle
import javafx.fxml.FXML
import javafx.fxml.FXMLLoader
import javafx.geometry.Side
import javafx.scene.control.Button
import javafx.scene.control.Label
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
import ktfx.beans.binding.bindingOf
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
import org.controlsfx.control.HiddenSidesPane
import java.io.File
import java.net.URL
import java.util.ResourceBundle

class WageController : ActionController() {

    @FXML lateinit var anchorPane: AnchorPane
    @FXML lateinit var titleLabel: Label
    @FXML lateinit var hiddenSidesPane: HiddenSidesPane
    @FXML lateinit var flowPane: FlowPane
    @FXML lateinit var processButton: Button

    private lateinit var browseButton: Button
    private lateinit var disableRecessButton: Button
    private lateinit var saveWageButton: Button
    private lateinit var historyButton: Button

    override fun NodeManager.onCreateActions() {
        browseButton = stretchableButton(STRETCH_POINT, getString(R.string.browse), ImageView(R.image.act_browse)) {
            onAction { browse() }
        }
        disableRecessButton = stretchableButton(
            STRETCH_POINT,
            getString(R.string.disable_recess),
            ImageView(R.image.act_disable_recess)
        ) {
            disableProperty().bind(flowPane.children.isEmpty)
            onAction { disableRecess() }
        }
        saveWageButton = stretchableButton(STRETCH_POINT, getString(R.string.save_wage), ImageView(R.image.act_save)) {
            disableProperty().bind(flowPane.children.isEmpty)
            onAction { saveWage() }
        }
        space(R.dimen.padding_large.toDouble())
        historyButton = stretchableButton(STRETCH_POINT, getString(R.string.history), ImageView(R.image.act_history)) {
            onAction { history() }
        }
    }

    override fun initialize(location: URL, resources: ResourceBundle) {
        super.initialize(location, resources)
        titleLabel.textProperty().bind(stringBindingOf(flowPane.children) {
            "${flowPane.children.size} ${getString(R.string.employee)}"
        })
        processButton.onAction { process() }
        later { flowPane.prefWrapLengthProperty().bind(flowPane.scene.widthProperty()) }
        if (DEBUG) {
            val file = File("/Users/hendraanggrian/Downloads/Absen 4-13-18.xlsx")
            if (file.exists()) read(file)
        }
    }

    private fun disableRecess() = DisableRecessPopover(this, attendeePanes).show(disableRecessButton)

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
        titleLabel.graphic = label("${file.absolutePath} -")
        val loadingPane = borderPane {
            prefWidthProperty().bind(anchorPane.widthProperty())
            prefHeightProperty().bind(anchorPane.heightProperty())
            center = ktfx.jfoenix.jfxSpinner { maxSize = 96.0 }
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
                                bindProcessButton()
                            }
                            deleteOthersMenu.run {
                                disableProperty().bind(flowPane.children.size() lessEq 1)
                                onAction {
                                    flowPane.children -= flowPane.children.toMutableList().apply {
                                        remove(this@attendeePane)
                                    }
                                    bindProcessButton()
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
                                    bindProcessButton()
                                }
                            }
                        }
                    }
                }
                GlobalScope.launch(Dispatchers.JavaFx) {
                    anchorPane.children -= loadingPane
                    bindProcessButton()
                }
            } catch (e: Exception) {
                if (DEBUG) e.printStackTrace()
                GlobalScope.launch(Dispatchers.JavaFx) {
                    anchorPane.children -= loadingPane
                    bindProcessButton()
                    errorAlert(e.message.toString()) {
                        dialogPane.stylesheets += getStyle(R.style.openpss)
                    }.show()
                }
            }
        }
    }

    private inline val attendeePanes: List<AttendeePane> get() = flowPane.children.map { (it as AttendeePane) }

    private inline val attendees: List<Attendee> get() = attendeePanes.map { it.attendee }

    /** As attendees are populated, process button need to be rebinded according to new requirements. */
    private fun bindProcessButton() {
        processButton.disableProperty().bind(flowPane.children.isEmpty or
            booleanBindingOf(flowPane.children, *flowPane.children
                .map { (it as AttendeePane).attendanceList.items }
                .toTypedArray()) { attendees.any { it.attendances.size % 2 != 0 } })
        hiddenSidesPane.pinnedSideProperty().bind(bindingOf(flowPane.children, *flowPane.children
            .map { (it as AttendeePane).attendanceList.items }
            .toTypedArray()) {
            when {
                flowPane.children.isEmpty() || attendees.any { it.attendances.size % 2 != 0 } -> null
                else -> Side.BOTTOM
            }
        })
    }
}