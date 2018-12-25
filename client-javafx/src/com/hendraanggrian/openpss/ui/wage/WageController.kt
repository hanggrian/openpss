package com.hendraanggrian.openpss.ui.wage

import com.hendraanggrian.openpss.App.Companion.STRETCH_POINT
import com.hendraanggrian.openpss.BuildConfig.DEBUG
import com.hendraanggrian.openpss.R
import com.hendraanggrian.openpss.content.STYLESHEET_OPENPSS
import com.hendraanggrian.openpss.control.StretchableButton
import com.hendraanggrian.openpss.io.ReaderFile
import com.hendraanggrian.openpss.io.WageDirectory
import com.hendraanggrian.openpss.popup.dialog.TextDialog
import com.hendraanggrian.openpss.ui.ActionController
import com.hendraanggrian.openpss.ui.wage.record.WageRecordController.Companion.EXTRA_ATTENDEES
import com.hendraanggrian.openpss.util.controller
import com.hendraanggrian.openpss.util.getResource
import com.hendraanggrian.openpss.util.pane
import javafx.beans.property.SimpleStringProperty
import javafx.beans.property.StringProperty
import javafx.fxml.FXML
import javafx.fxml.FXMLLoader
import javafx.scene.control.Button
import javafx.scene.control.Label
import javafx.scene.image.ImageView
import javafx.scene.layout.AnchorPane
import javafx.scene.layout.FlowPane
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.javafx.JavaFx
import kotlinx.coroutines.launch
import ktfx.bindings.buildBooleanBinding
import ktfx.bindings.buildStringBinding
import ktfx.bindings.lessEq
import ktfx.bindings.or
import ktfx.collections.isEmptyBinding
import ktfx.collections.sizeBinding
import ktfx.controls.maxSize
import ktfx.coroutines.onAction
import ktfx.getValue
import ktfx.later
import ktfx.layouts.NodeInvokable
import ktfx.layouts.borderPane
import ktfx.layouts.scene
import ktfx.setValue
import ktfx.windows.chooseFile
import ktfx.windows.setMinSize
import ktfx.windows.stage
import java.io.File
import java.net.URL
import java.util.ResourceBundle

class WageController : ActionController() {

    @FXML lateinit var titleLabel: Label
    @FXML lateinit var disableRecessButton: Button
    @FXML lateinit var processButton: Button
    @FXML lateinit var anchorPane: AnchorPane
    @FXML lateinit var flowPane: FlowPane

    private lateinit var browseButton: Button
    private lateinit var saveWageButton: Button
    private lateinit var historyButton: Button

    private val filePathProperty: StringProperty = SimpleStringProperty()
    private var filePath: String by filePathProperty

    override fun NodeInvokable.onCreateActions() {
        browseButton = StretchableButton(
            STRETCH_POINT,
            getString(R.string.browse),
            ImageView(R.image.act_browse)
        ).apply {
            onAction { browse() }
        }()
        saveWageButton = StretchableButton(
            STRETCH_POINT,
            getString(R.string.save_wage),
            ImageView(R.image.act_save)
        ).apply {
            disableProperty().bind(flowPane.children.isEmptyBinding)
            onAction { saveWage() }
        }()
        historyButton = StretchableButton(
            STRETCH_POINT,
            getString(R.string.history),
            ImageView(R.image.act_history)
        ).apply {
            onAction { history() }
        }()
    }

    override fun initialize(location: URL, resources: ResourceBundle) {
        super.initialize(location, resources)
        titleProperty().bind(buildStringBinding(flowPane.children) {
            when {
                flowPane.children.isEmpty() -> null
                else -> "${flowPane.children.size} ${getString(R.string.employee)}"
            }
        })
        titleLabel.textProperty().bind(buildStringBinding(flowPane.children) {
            when {
                flowPane.children.isEmpty() -> getString(R.string._wage_record_empty)
                else -> filePath
            }
        })
        disableRecessButton.disableProperty().bind(flowPane.children.isEmptyBinding)
        bindProcessButton()
        later {
            flowPane.prefWrapLengthProperty().bind(flowPane.scene.widthProperty())
            if (DEBUG) {
                val file = File("/Users/hendraanggrian/Downloads/Absen 4-13-18.xlsx")
                if (file.exists()) read(file)
            }
        }
    }

    @FXML fun disableRecess() = DisableRecessPopover(this, attendeePanes).show(disableRecessButton)

    @FXML fun process() = stage(getString(R.string.wage_record)) {
        val loader = FXMLLoader(getResource(R.layout.controller_wage_record), resourceBundle)
        scene = scene {
            loader.pane()
            stylesheets += STYLESHEET_OPENPSS
        }
        setMinSize(1000.0, 650.0)
        loader.controller.addExtra(EXTRA_ATTENDEES, attendees)
    }.showAndWait()

    private fun saveWage() = GlobalScope.launch(Dispatchers.JavaFx) { attendees.forEach { it.saveWage(api) } }

    private fun history() = desktop?.open(WageDirectory)

    private fun browse() = anchorPane.scene.window.chooseFile(
        getString(R.string.input_file) to Reader.of(ReaderFile.WAGE_READER).extension
    )?.let { file ->
        GlobalScope.launch(Dispatchers.JavaFx) {
            withPermission { read(file) }
        }
    }

    private fun read(file: File) {
        filePath = file.absolutePath
        val loadingPane = borderPane {
            prefWidthProperty().bind(anchorPane.widthProperty())
            prefHeightProperty().bind(anchorPane.heightProperty())
            center = ktfx.jfoenix.jfxSpinner { maxSize = 96.0 }
        }
        anchorPane.children += loadingPane
        flowPane.children.clear()
        GlobalScope.launch(Dispatchers.Default) {
            try {
                Reader.of(ReaderFile.WAGE_READER).read(file).forEach { attendee ->
                    attendee.init(api)
                    GlobalScope.launch(Dispatchers.JavaFx) {
                        flowPane.children += AttendeePane(this@WageController, attendee).apply {
                            deleteMenu.onAction {
                                flowPane.children -= this@apply
                                bindProcessButton()
                            }
                            deleteOthersMenu.run {
                                disableProperty().bind(flowPane.children.sizeBinding lessEq 1)
                                onAction {
                                    flowPane.children -= flowPane.children.toMutableList()
                                        .also { it -= this@apply }
                                    bindProcessButton()
                                }
                            }
                            deleteToTheRightMenu.run {
                                disableProperty().bind(buildBooleanBinding(flowPane.children) {
                                    flowPane.children.indexOf(this@apply) == flowPane.children.lastIndex
                                })
                                onAction {
                                    flowPane.children -= flowPane.children.toList().takeLast(
                                        flowPane.children.lastIndex - flowPane.children.indexOf(this@apply)
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
                    TextDialog(this@WageController, R.string.reading_failed, e.message.toString()).show()
                }
            }
        }
    }

    private inline val attendeePanes: List<AttendeePane> get() = flowPane.children.map { (it as AttendeePane) }

    private inline val attendees: List<Attendee> get() = attendeePanes.map { it.attendee }

    /** As attendees are populated, process button need to be rebinded according to new requirements. */
    private fun bindProcessButton() = processButton.disableProperty().bind(flowPane.children.isEmptyBinding or
        buildBooleanBinding(flowPane.children, *flowPane.children
            .map { (it as AttendeePane).attendanceList.items }
            .toTypedArray()) { attendees.any { it.attendances.size % 2 != 0 } })
}