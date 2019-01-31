package com.hendraanggrian.openpss.ui.wage

import com.hendraanggrian.openpss.BuildConfig2
import com.hendraanggrian.openpss.FxSetting
import com.hendraanggrian.openpss.R
import com.hendraanggrian.openpss.R2
import com.hendraanggrian.openpss.WageDirectory
import com.hendraanggrian.openpss.control.StretchableButton
import com.hendraanggrian.openpss.ui.ActionController
import com.hendraanggrian.openpss.ui.Stylesheets
import com.hendraanggrian.openpss.ui.TextDialog
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
import kotlinx.coroutines.runBlocking
import ktfx.bindings.buildBooleanBinding
import ktfx.bindings.buildStringBinding
import ktfx.bindings.lessEq
import ktfx.bindings.or
import ktfx.collections.isEmptyBinding
import ktfx.collections.sizeBinding
import ktfx.coroutines.onAction
import ktfx.getValue
import ktfx.jfoenix.jfxSnackbar
import ktfx.layouts.NodeInvokable
import ktfx.layouts.borderPane
import ktfx.layouts.scene
import ktfx.runLater
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
            getDouble(R.value.stretch),
            getString(R2.string.browse),
            ImageView(R.image.action_browse)
        ).apply {
            onAction { browse() }
        }()
        saveWageButton = StretchableButton(
            getDouble(R.value.stretch),
            getString(R2.string.save_wage),
            ImageView(R.image.action_save)
        ).apply {
            disableProperty().bind(flowPane.children.isEmptyBinding)
            onAction {
                saveWage()
                rootLayout.jfxSnackbar(
                    getString(R2.string.wage_saved),
                    getLong(R.value.duration_short)
                )
            }
        }()
        historyButton = StretchableButton(
            getDouble(R.value.stretch),
            getString(R2.string.history),
            ImageView(R.image.action_history)
        ).apply {
            onAction { history() }
        }()
    }

    override fun initialize(location: URL, resources: ResourceBundle) {
        super.initialize(location, resources)
        titleProperty().bind(buildStringBinding(flowPane.children) {
            when {
                flowPane.children.isEmpty() -> null
                else -> "${flowPane.children.size} ${getString(R2.string.employee)}"
            }
        })
        titleLabel.textProperty().bind(buildStringBinding(flowPane.children) {
            when {
                flowPane.children.isEmpty() -> getString(R2.string._wage_record_empty)
                else -> filePath
            }
        })
        disableRecessButton.disableProperty().bind(flowPane.children.isEmptyBinding)
        bindProcessButton()
        runLater {
            flowPane.prefWrapLengthProperty().bind(flowPane.scene.widthProperty())
            if (BuildConfig2.DEBUG) {
                val file = File("/Users/hendraanggrian/Downloads/Absen 4-13-18.xlsx")
                if (file.exists()) {
                    read(file)
                }
            }
        }
    }

    @FXML
    fun disableRecess() = DisableRecessPopOver(this, attendeePanes).show(disableRecessButton)

    @FXML
    fun process() = stage(getString(R2.string.wage_record)) {
        val loader = FXMLLoader(getResource(R.layout.controller_wage_record), resourceBundle)
        scene = scene {
            loader.pane()
            stylesheets += Stylesheets.OPENPSS
        }
        setMinSize(1000.0, 650.0)
        loader.controller.addExtra(EXTRA_ATTENDEES, attendees)
    }.showAndWait()

    private fun saveWage() = runBlocking(Dispatchers.IO) { attendees.forEach { it.saveWage(api) } }

    private fun history() = desktop?.open(WageDirectory)

    private fun browse() {
        runBlocking {
            withPermission {
                anchorPane.scene.window.chooseFile(
                    getString(R2.string.input_file) to
                        WageReader.of(defaults[FxSetting.KEY_WAGEREADER]!!).extension
                )?.let { read(it) }
            }
        }
    }

    private fun read(file: File) {
        filePath = file.absolutePath
        val loadingPane = borderPane {
            prefWidthProperty().bind(anchorPane.widthProperty())
            prefHeightProperty().bind(anchorPane.heightProperty())
            center = ktfx.jfoenix.jfxSpinner {
                setMaxSize(96.0, 96.0)
            }
        }
        anchorPane.children += loadingPane
        flowPane.children.clear()
        val onFinish = {
            anchorPane.children -= loadingPane
            bindProcessButton()
        }
        runCatching {
            GlobalScope.launch(Dispatchers.IO) {
                WageReader.of(defaults[FxSetting.KEY_WAGEREADER]!!).read(file)
                    .forEach { attendee ->
                        GlobalScope.launch(Dispatchers.JavaFx) {
                            attendee.init(api)
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
                                            flowPane.children.lastIndex - flowPane.children.indexOf(
                                                this@apply
                                            )
                                        )
                                        bindProcessButton()
                                    }
                                }
                            }
                        }
                    }
                GlobalScope.launch(Dispatchers.JavaFx) {
                    onFinish()
                }
            }
        }.onFailure {
            if (BuildConfig2.DEBUG) it.printStackTrace()
            TextDialog(this@WageController, R2.string.reading_failed, it.message.toString()).show()
            onFinish()
        }
    }

    private inline val attendeePanes: List<AttendeePane> get() = flowPane.children.map { (it as AttendeePane) }

    private inline val attendees: List<Attendee> get() = attendeePanes.map { it.attendee }

    /** As attendees are populated, process button need to be rebinded according to new requirements. */
    private fun bindProcessButton() =
        processButton.disableProperty().bind(flowPane.children.isEmptyBinding or
            buildBooleanBinding(flowPane.children, *flowPane.children
                .map { (it as AttendeePane).attendanceList.items }
                .toTypedArray()) { attendees.any { it.attendances.size % 2 != 0 } })
}