@file:Suppress("ktlint:rulebook:exception-subclass-catching")

package com.hanggrian.openpss.ui.wage

import com.hanggrian.openpss.BuildConfig
import com.hanggrian.openpss.R
import com.hanggrian.openpss.STYLESHEET_OPENPSS
import com.hanggrian.openpss.io.WageDirectory
import com.hanggrian.openpss.io.properties.PreferencesFile.WAGE_READER
import com.hanggrian.openpss.popup.dialog.TextDialog
import com.hanggrian.openpss.ui.ActionController
import com.hanggrian.openpss.ui.wage.readers.Reader
import com.hanggrian.openpss.ui.wage.record.WageRecordController.Companion.EXTRA_ATTENDEES
import com.hanggrian.openpss.util.controller
import com.hanggrian.openpss.util.getResource
import com.hanggrian.openpss.util.pane
import javafx.beans.property.SimpleStringProperty
import javafx.fxml.FXML
import javafx.fxml.FXMLLoader
import javafx.scene.control.Button
import javafx.scene.control.Label
import javafx.scene.image.ImageView
import javafx.scene.layout.AnchorPane
import javafx.scene.layout.FlowPane
import javafx.stage.FileChooser.ExtensionFilter
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.javafx.JavaFx
import kotlinx.coroutines.launch
import ktfx.bindings.booleanBindingBy
import ktfx.bindings.booleanBindingOf
import ktfx.bindings.emptyBinding
import ktfx.bindings.lessEq
import ktfx.bindings.or
import ktfx.bindings.sizeBinding
import ktfx.bindings.stringBindingBy
import ktfx.coroutines.onAction
import ktfx.getValue
import ktfx.jfoenix.layouts.styledJfxButton
import ktfx.layouts.NodeContainer
import ktfx.layouts.borderPane
import ktfx.layouts.scene
import ktfx.layouts.tooltip
import ktfx.runLater
import ktfx.setValue
import ktfx.windows.chooseFile
import ktfx.windows.minSize
import ktfx.windows.size2
import ktfx.windows.stage
import java.io.File
import java.net.URL
import java.util.ResourceBundle

class WageController : ActionController() {
    @FXML
    lateinit var titleLabel: Label

    @FXML
    lateinit var disableRecessButton: Button

    @FXML
    lateinit var processButton: Button

    @FXML
    lateinit var anchorPane: AnchorPane

    @FXML
    lateinit var flowPane: FlowPane

    private lateinit var browseButton: Button
    private lateinit var saveWageButton: Button
    private lateinit var historyButton: Button

    private val filePathProperty = SimpleStringProperty()
    private var filePath by filePathProperty

    override fun NodeContainer.onCreateActions() {
        browseButton =
            styledJfxButton(null, ImageView(R.image_act_browse), R.style_flat) {
                tooltip(getString(R.string_browse))
                onAction { browse() }
            }
        saveWageButton =
            styledJfxButton(null, ImageView(R.image_act_save), R.style_flat) {
                tooltip(getString(R.string_save_wage))
                disableProperty().bind(flowPane.children.emptyBinding)
                onAction {
                    saveWage()
                    TextDialog(
                        this@WageController,
                        R.string_save_wage,
                        getString(R.string__wage_saved),
                    ).show()
                }
            }
        historyButton =
            styledJfxButton(null, ImageView(R.image_act_history), R.style_flat) {
                tooltip(getString(R.string_history))
                onAction { history() }
            }
    }

    override fun initialize(location: URL, resources: ResourceBundle) {
        super.initialize(location, resources)

        titleProperty.bind(
            flowPane.children.stringBindingBy {
                when {
                    it.isEmpty() -> null
                    else -> "${flowPane.children.size} ${getString(R.string_employee)}"
                }
            },
        )
        titleLabel.textProperty().bind(
            flowPane.children.stringBindingBy {
                when {
                    it.isEmpty() -> getString(R.string__wage_record_empty)
                    else -> filePath
                }
            },
        )
        disableRecessButton.disableProperty().bind(flowPane.children.emptyBinding)
        bindProcessButton()
        runLater {
            flowPane.prefWrapLengthProperty().bind(flowPane.scene.widthProperty())
        }
    }

    @FXML
    fun disableRecess() = DisableRecessPopover(this, attendeePanes).show(disableRecessButton)

    @FXML
    fun process() =
        stage {
            title = getString(R.string_wage_record)
            val loader = FXMLLoader(getResource(R.layout_controller_wage_record), resourceBundle)
            scene {
                stylesheets += STYLESHEET_OPENPSS
                addChild(loader.pane)
            }
            size2 = 1024.0 to 600.0
            minSize = 1024.0 to 600.0
            loader.controller.addExtra(EXTRA_ATTENDEES, attendees)
        }.showAndWait()

    private fun saveWage() = attendees.forEach { it.saveWage() }

    private fun history() = desktop?.open(WageDirectory)

    private fun browse() =
        anchorPane.scene.window
            .chooseFile {
                extensionFilters +=
                    ExtensionFilter(
                        getString(R.string_input_file),
                        *Reader.of(WAGE_READER).extensions,
                    )
            }?.let { file -> (ReadWageAction(this)) { read(file) } }

    private fun read(file: File) {
        filePath = file.absolutePath
        val loadingPane =
            borderPane {
                prefWidthProperty().bind(anchorPane.widthProperty())
                prefHeightProperty().bind(anchorPane.heightProperty())
                center = ktfx.jfoenix.layouts.jfxSpinner { setMaxSize(96.0, 96.0) }
            }
        anchorPane.children += loadingPane
        flowPane.children.clear()
        GlobalScope.launch(Dispatchers.IO) {
            try {
                Reader.of(WAGE_READER).read(file).forEach { attendee ->
                    attendee.mergeDuplicates()
                    GlobalScope.launch(Dispatchers.JavaFx) {
                        flowPane.children +=
                            AttendeePane(this@WageController, attendee).apply {
                                deleteMenu.onAction {
                                    flowPane.children -= this@apply
                                    bindProcessButton()
                                }
                                deleteOthersMenu.run {
                                    disableProperty().bind(flowPane.children.sizeBinding lessEq 1)
                                    onAction {
                                        flowPane.children -=
                                            flowPane.children
                                                .toMutableSet()
                                                .also { it -= this@apply }
                                        bindProcessButton()
                                    }
                                }
                                deleteToTheRightMenu.run {
                                    disableProperty().bind(
                                        flowPane.children.booleanBindingBy {
                                            it.indexOf(this@apply) == flowPane.children.lastIndex
                                        },
                                    )
                                    onAction {
                                        flowPane.children -=
                                            flowPane.children
                                                .takeLast(
                                                    flowPane.children.lastIndex -
                                                        flowPane.children.indexOf(this@apply),
                                                ).toSet()
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
                if (BuildConfig.DEBUG) e.printStackTrace()
                GlobalScope.launch(Dispatchers.JavaFx) {
                    anchorPane.children -= loadingPane
                    bindProcessButton()
                    TextDialog(
                        this@WageController,
                        R.string_reading_failed,
                        e.message.toString(),
                    ).show()
                }
            }
        }
    }

    private inline val attendeePanes: List<AttendeePane>
        get() = flowPane.children.map { (it as AttendeePane) }

    private inline val attendees: List<Attendee>
        get() = attendeePanes.map { it.attendee }

    /**
     * As attendees are populated, process button need to be rebinded according to new requirements.
     */
    private fun bindProcessButton() =
        processButton.disableProperty().bind(
            flowPane.children.emptyBinding or
                booleanBindingOf(
                    flowPane.children,
                    *flowPane.children
                        .map { (it as AttendeePane).attendanceList.items }
                        .toTypedArray(),
                ) { attendees.any { it.attendances.size % 2 != 0 } },
        )
}
