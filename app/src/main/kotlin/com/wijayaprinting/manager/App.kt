package com.wijayaprinting.manager

import com.wijayaprinting.data.connectDatabase
import com.wijayaprinting.manager.io.MySQLFile
import com.wijayaprinting.manager.io.PreferencesFile
import com.wijayaprinting.manager.scene.control.IPField
import com.wijayaprinting.manager.scene.control.IntField
import com.wijayaprinting.manager.scene.utils.attachButtons
import com.wijayaprinting.manager.scene.utils.setGaps
import com.wijayaprinting.manager.utils.icon
import com.wijayaprinting.manager.utils.setIconOnOSX
import javafx.application.Application
import javafx.application.Platform.exit
import javafx.fxml.FXMLLoader
import javafx.geometry.Pos.CENTER
import javafx.scene.Scene
import javafx.scene.control.ButtonType.CANCEL
import javafx.scene.control.ButtonType.OK
import javafx.scene.control.ChoiceBox
import javafx.scene.control.PasswordField
import javafx.scene.control.TextField
import javafx.scene.image.Image
import javafx.scene.image.ImageView
import javafx.stage.Stage
import kotfx.bindings.bindingOf
import kotfx.bindings.not
import kotfx.bindings.or
import kotfx.controls.*
import kotfx.controls.popups.tooltip
import kotfx.dialogs.dialogWait
import kotfx.dialogs.errorAlertWait
import kotfx.dialogs.infoAlertWait
import kotfx.dialogs.okButton
import kotfx.layouts.gridPane
import kotfx.layouts.hbox
import kotfx.properties.bind
import kotfx.properties.bindBidirectional
import org.apache.log4j.BasicConfigurator.configure
import java.awt.Desktop.getDesktop
import java.awt.Toolkit
import java.net.InetAddress.getByName
import java.net.URI

class App : Application() {

    lateinit var languageBox: ChoiceBox<Language>
    lateinit var usernameField: TextField
    lateinit var ipField: IPField
    lateinit var portField: IntField

    lateinit var passwordField: PasswordField

    companion object {
        private const val IP_LOOKUP_TIMEOUT = 3000

        @JvmStatic
        fun main(vararg args: String) = launch(App::class.java, *args)
    }

    override fun init() {
        if (BuildConfig.DEBUG) configure()
        setResources(Language.parse(PreferencesFile[PreferencesFile.LANGUAGE].value).getResources("string"))
    }

    override fun start(stage: Stage) {
        stage.icon = Image(R.png.logo_launcher)
        setIconOnOSX(Toolkit.getDefaultToolkit().getImage(App::class.java.getResource(R.png.logo_launcher)))

        dialogWait<Any>(getString(R.string.app_name)) {
            header(getString(R.string.login))
            graphic(ImageView(R.png.ic_launcher))
            resizable(false)
            content(gridPane {
                setGaps(8)
                label(getString(R.string.language)) col 0 row 0
                languageBox = choiceBox(Language.listAll()) { maxWidth = Double.MAX_VALUE } col 1 row 0 colSpan 2
                label(getString(R.string.username)) col 0 row 1
                usernameField = textField { promptText = getString(R.string.username) } col 1 row 1 colSpan 2
                label(getString(R.string.server)) col 0 row 2
                ipField = IPField().apply {
                    promptText = getString(R.string.ip_address)
                    prefWidth = 128.0
                }.add() col 1 row 2
                portField = IntField().apply {
                    promptText = getString(R.string.port)
                    prefWidth = 64.0
                }.add() col 2 row 2

                val initialLanguage = Language.parse(PreferencesFile[PreferencesFile.LANGUAGE].value)
                languageBox.selectionModel.select(initialLanguage)
                languageBox.selectionModel.selectedItemProperty().addListener { _, _, newValue ->
                    PreferencesFile.apply { get(PreferencesFile.LANGUAGE).set(newValue.locale) }.save()
                    close()
                    infoAlertWait(getString(R.string.language_changed))
                    exit()
                }
                usernameField.textProperty() bindBidirectional MySQLFile[MySQLFile.USERNAME]
                ipField.textProperty() bindBidirectional MySQLFile[MySQLFile.IP]
                portField.textProperty() bindBidirectional MySQLFile[MySQLFile.PORT]
                run { usernameField.requestFocus() }
            })
            expandableContent(gridPane {
                setGaps(8)
                label("An open-source software.\nFor more information and update, visit:") col 0 row 0 colSpan 2
                hyperlink("https://github.com/WijayaPrinting/") { setOnAction { getDesktop().browse(URI(text)) } } col 0 row 1 colSpan 2
                label("Manager") col 0 row 2
                label(BuildConfig.VERSION) col 1 row 2
                label("Data") col 0 row 3
                label(com.wijayaprinting.data.BuildConfig.VERSION) col 1 row 3
            })

            button(CANCEL)
            okButton(getString((R.string.login))) {
                MySQLFile.save()
                if (!getByName(ipField.text).isReachable(IP_LOOKUP_TIMEOUT)) errorAlertWait(getString(R.string.ip_address_unreachable))
                else dialogWait<String>(getString(R.string.password)) {
                    header(getString(R.string.password_required))
                    graphic(ImageView(R.png.ic_key))
                    content(hbox {
                        spacing = 8.0
                        alignment = CENTER
                        label(getString(R.string.password))
                        passwordField = passwordField { promptText = getString(R.string.password) }
                        val passwordToggle = toggleButton { attachButtons(R.png.btn_visibility, R.png.btn_visibility_off) }
                        passwordField.tooltipProperty() bind bindingOf(passwordField.textProperty(), passwordToggle.selectedProperty()) { if (!passwordToggle.isSelected) null else tooltip(passwordField.text) }
                        run { passwordField.requestFocus() }
                        if (BuildConfig.DEBUG) {
                            passwordField.text = "justforApp1e!"
                        }
                    })
                    buttons(CANCEL, OK)
                    OK.asNode().disableProperty() bind passwordField.textProperty().isEmpty
                    setResultConverter { if (it == OK) passwordField.text else null }
                }.filter { it != null }.ifPresent { password ->
                    try {
                        connectDatabase(ipField.text, portField.text, usernameField.text, password)
                        result = usernameField.text
                        close()
                    } catch (e: Exception) {
                        errorAlertWait(e.message ?: "Unknown error!")
                    }
                }
            }.asNode().disableProperty() bind (usernameField.textProperty().isEmpty or not(ipField.validProperty) or portField.textProperty().isEmpty)
        }.filter { it is String }.ifPresent {
            val minSize = Pair(960.0, 640.0)
            stage.apply {
                scene = Scene(FXMLLoader.load(App::class.java.getResource(R.fxml.layout_main), resources), minSize.first, minSize.second)
                title = getString(R.string.app_name)
                minWidth = minSize.first
                minHeight = minSize.second
            }.show()
        }
    }
}