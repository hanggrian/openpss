package com.wijayaprinting.manager

import com.wijayaprinting.data.Employee
import com.wijayaprinting.data.WP
import com.wijayaprinting.manager.dialog.AboutDialog
import com.wijayaprinting.manager.internal.Language
import com.wijayaprinting.manager.internal.Resourceful
import com.wijayaprinting.manager.io.MySQLFile
import com.wijayaprinting.manager.io.PreferencesFile
import com.wijayaprinting.manager.scene.control.IPField
import com.wijayaprinting.manager.scene.control.IntField
import com.wijayaprinting.manager.scene.control.intField
import com.wijayaprinting.manager.scene.control.ipField
import com.wijayaprinting.manager.scene.utils.attachButtons
import com.wijayaprinting.manager.scene.utils.setGaps
import com.wijayaprinting.manager.utils.forceClose
import com.wijayaprinting.manager.utils.multithread
import com.wijayaprinting.manager.utils.setIconOnOSX
import io.reactivex.rxkotlin.subscribeBy
import javafx.application.Application
import javafx.application.Platform.exit
import javafx.beans.property.BooleanProperty
import javafx.beans.property.SimpleBooleanProperty
import javafx.event.ActionEvent.ACTION
import javafx.scene.control.ButtonBar.ButtonData.BACK_PREVIOUS
import javafx.scene.control.ButtonBar.ButtonData.NEXT_FORWARD
import javafx.scene.control.Dialog
import javafx.scene.control.Hyperlink
import javafx.scene.control.PasswordField
import javafx.scene.control.TextField
import javafx.scene.image.Image
import javafx.scene.image.ImageView
import javafx.scene.layout.Pane
import javafx.stage.Stage
import kotfx.*
import org.apache.log4j.BasicConfigurator.configure
import java.awt.Toolkit.getDefaultToolkit
import java.util.*

class App : Application(), Resourceful {

    companion object {
        var fullAccessProperty: BooleanProperty = SimpleBooleanProperty()

        @JvmStatic fun main(vararg args: String) = launch(App::class.java, *args)
    }

    override val resources: ResourceBundle = Language.parse(PreferencesFile[PreferencesFile.LANGUAGE].value).getResources("string")

    override fun init() {
        if (BuildConfig.DEBUG) configure()
    }

    override fun start(stage: Stage) {
        stage.icon = Image(R.png.logo_launcher)
        setIconOnOSX(getDefaultToolkit().getImage(getResource(R.png.logo_launcher)))
        dialog<Any>(getString(R.string.app_name)) {
            lateinit var employeeField: TextField
            lateinit var passwordField: PasswordField
            lateinit var serverIPField: IPField
            lateinit var serverPortField: IntField
            lateinit var serverUserField: TextField
            lateinit var serverPasswordField: PasswordField
            headerText = getString(R.string.login)
            graphic = ImageView(R.png.ic_launcher)
            isResizable = false
            content = gridPane {
                setGaps(8)
                label(getString(R.string.language)) col 0 row 0
                choiceBox(Language.listAll()) {
                    maxWidth = Double.MAX_VALUE
                    selectionModel.select(Language.parse(PreferencesFile[PreferencesFile.LANGUAGE].value))
                    selectionModel.selectedItemProperty().addListener { _, _, language ->
                        PreferencesFile[PreferencesFile.LANGUAGE].set(language.locale)
                        PreferencesFile.save()
                        forceClose()
                        infoAlert(getString(R.string.language_changed)).showAndWait().ifPresent { exit() }
                    }
                } col 1 row 0 colSpan 2
                label(getString(R.string.employee)) col 0 row 1
                employeeField = textField {
                    promptText = getString(R.string.employee)
                    textProperty() bindBidirectional MySQLFile[MySQLFile.USERNAME]
                } col 1 row 1 colSpan 2
                label(getString(R.string.password)) col 0 row 2
                passwordField = passwordField { promptText = getString(R.string.password) } col 1 row 2
                toggleButton {
                    attachButtons(R.png.btn_visibility, R.png.btn_visibility_off)
                    passwordField.tooltipProperty() bind bindingOf(passwordField.textProperty(), selectedProperty()) { if (!isSelected) null else tooltip(passwordField.text) }
                } col 2 row 2
            }
            expandableContent = gridPane {
                setGaps(8)
                label(getString(R.string.server_ip_port)) col 0 row 0
                serverIPField = ipField {
                    promptText = getString(R.string.ip_address)
                    prefWidth = 128.0
                    textProperty() bindBidirectional MySQLFile[MySQLFile.IP]
                } col 1 row 0
                serverPortField = intField {
                    promptText = getString(R.string.port)
                    prefWidth = 64.0
                    textProperty() bindBidirectional MySQLFile[MySQLFile.PORT]
                } col 2 row 0
                label(getString(R.string.server_user)) col 0 row 1
                serverUserField = textField { promptText = getString(R.string.server_user) } col 1 row 1 colSpan 2
                label(getString(R.string.server_password)) col 0 row 2
                serverPasswordField = passwordField { promptText = getString(R.string.server_password) } col 1 row 2 colSpan 2
            }

            button(getString(R.string.about), BACK_PREVIOUS).addEventFilter(ACTION) { event ->
                event.consume()
                AboutDialog(resources).showAndWait()
            }
            button(getString(R.string.login), NEXT_FORWARD).apply {
                disableProperty() bind (employeeField.textProperty().isEmpty or not(serverIPField.validProperty) or serverPortField.textProperty().isEmpty)
                addEventFilter(ACTION) { event ->
                    event.consume()
                    MySQLFile.save()
                    WP.login(serverIPField.text, serverPortField.text, serverUserField.text, serverPasswordField.text, employeeField.text, passwordField.text)
                            .multithread()
                            .subscribeBy({ errorAlert(it.message.toString()).showAndWait() }) { employee ->
                                result = employee
                                forceClose()
                            }
                }
            }

            runFX {
                if (employeeField.text.isBlank()) employeeField.requestFocus() else passwordField.requestFocus()
                isExpanded = listOf(serverIPField, serverPortField, serverUserField, serverPasswordField).any { it.text.isBlank() }
            }
        }.showAndWait().filter { it is Employee }.ifPresent {
            val minSize = Pair(960.0, 640.0)
            stage.apply {
                title = getString(R.string.app_name)
                scene = getResource(R.fxml.layout_main).loadFXML(resources).load<Pane>().toScene(minSize.first, minSize.second)
                minWidth = minSize.first
                minHeight = minSize.second
            }.show()
        }
    }

    private val Dialog<*>.detailsButton: Hyperlink get() = dialogPane.lookup(".details-button") as Hyperlink
}