package com.wijayaprinting.manager

import com.wijayaprinting.data.Data
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
import com.wijayaprinting.manager.utils.multithread
import com.wijayaprinting.manager.utils.setIconOnOSX
import io.reactivex.rxkotlin.subscribeBy
import javafx.application.Application
import javafx.application.Platform.exit
import javafx.beans.property.BooleanProperty
import javafx.beans.property.SimpleBooleanProperty
import javafx.event.ActionEvent.ACTION
import javafx.geometry.Pos.CENTER
import javafx.scene.control.ButtonBar.ButtonData.LEFT
import javafx.scene.control.ButtonType
import javafx.scene.control.ButtonType.CANCEL
import javafx.scene.control.ButtonType.OK
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
            headerText = getString(R.string.login)
            graphic = ImageView(R.png.ic_launcher)
            isResizable = false
            lateinit var usernameField: TextField
            lateinit var ipField: IPField
            lateinit var portField: IntField
            content = gridPane {
                setGaps(8)
                label(getString(R.string.language)) col 0 row 0
                choiceBox(Language.listAll()) {
                    maxWidth = Double.MAX_VALUE
                    selectionModel.select(Language.parse(PreferencesFile[PreferencesFile.LANGUAGE].value))
                    selectionModel.selectedItemProperty().addListener { _, _, newValue ->
                        PreferencesFile[PreferencesFile.LANGUAGE].set(newValue.locale)
                        PreferencesFile.save()
                        close()
                        infoAlert(getString(R.string.language_changed)).showAndWait().ifPresent { exit() }
                    }
                } col 1 row 0 colSpan 2
                label(getString(R.string.username)) col 0 row 1
                usernameField = textField {
                    promptText = getString(R.string.username)
                    textProperty() bindBidirectional MySQLFile[MySQLFile.USERNAME]
                } col 1 row 1 colSpan 2
                label(getString(R.string.server)) col 0 row 2
                ipField = ipField {
                    promptText = getString(R.string.ip_address)
                    prefWidth = 128.0
                    textProperty() bindBidirectional MySQLFile[MySQLFile.IP]
                } col 1 row 2
                portField = intField {
                    promptText = getString(R.string.port)
                    prefWidth = 64.0
                    textProperty() bindBidirectional MySQLFile[MySQLFile.PORT]
                } col 2 row 2

                runFX { usernameField.requestFocus() }
            }

            addButton(ButtonType(getString(R.string.about), LEFT)).addEventFilter(ACTION) { event ->
                event.consume()
                AboutDialog(resources).showAndWait()
            }
            addButtons(CANCEL, OK).apply {
                addEventFilter(ACTION) { event ->
                    event.consume()
                    MySQLFile.save()
                    dialog<String>(getString(R.string.password)) {
                        headerText = getString(R.string.password_required)
                        graphic = ImageView(R.png.ic_key)
                        lateinit var passwordField: PasswordField
                        content = hbox {
                            spacing = 8.0
                            alignment = CENTER
                            label(getString(R.string.password))
                            passwordField = passwordField { promptText = getString(R.string.password) }
                            val passwordToggle = toggleButton { attachButtons(R.png.btn_visibility, R.png.btn_visibility_off) }
                            passwordField.tooltipProperty() bind bindingOf(passwordField.textProperty(), passwordToggle.selectedProperty()) { if (!passwordToggle.isSelected) null else tooltip(passwordField.text) }
                            runFX { passwordField.requestFocus() }
                            if (BuildConfig.DEBUG) {
                                passwordField.text = "justforApp1e!"
                            }
                        }
                        addButtons(CANCEL, OK).disableProperty() bind passwordField.textProperty().isEmpty
                        setResultConverter { if (it == OK) passwordField.text else null }
                    }.showAndWait().filter { it != null }.ifPresent { password ->
                        Data.connect(ipField.text, portField.text, usernameField.text, password)
                                .multithread()
                                .subscribeBy({ errorAlert(it.message.toString()).showAndWait() }) { fullAccess ->
                                    fullAccessProperty.set(fullAccess)
                                    result = usernameField.text
                                    close()
                                }
                    }
                }
                disableProperty() bind (usernameField.textProperty().isEmpty or not(ipField.validProperty) or portField.textProperty().isEmpty)
            }
        }.showAndWait().filter { it is String }.ifPresent {
            val minSize = Pair(960.0, 640.0)
            stage.apply {
                title = getString(R.string.app_name)
                scene = getResource(R.fxml.layout_main).loadFXML(resources).load<Pane>().toScene(minSize.first, minSize.second)
                minWidth = minSize.first
                minHeight = minSize.second
            }.show()
        }
    }
}