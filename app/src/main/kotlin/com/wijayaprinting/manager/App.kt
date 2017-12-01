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
import javafx.application.Platform.runLater
import javafx.event.ActionEvent
import javafx.fxml.FXMLLoader
import javafx.geometry.Pos.CENTER
import javafx.scene.Scene
import javafx.scene.control.*
import javafx.scene.control.ButtonType.CANCEL
import javafx.scene.control.ButtonType.OK
import javafx.scene.image.Image
import javafx.scene.image.ImageView
import javafx.scene.layout.GridPane
import javafx.scene.layout.HBox
import javafx.stage.Stage
import javafx.util.Callback
import kotfx.bindings.bindingOf
import kotfx.bindings.not
import kotfx.bindings.or
import kotfx.dialogs.dialog
import kotfx.dialogs.errorAlert
import kotfx.dialogs.infoAlert
import org.apache.log4j.BasicConfigurator.configure
import java.awt.Desktop.getDesktop
import java.awt.Toolkit
import java.net.InetAddress.getByName
import java.net.URI

class App : Application() {

    companion object {
        private const val IP_LOOKUP_TIMEOUT = 3000

        @JvmStatic
        fun main(vararg args: String) = launch(App::class.java, *args)
    }

    override fun init() {
        if (BuildConfig.DEBUG) configure()
        setResources(Language.parse(PreferencesFile()[PreferencesFile.LANGUAGE].value).getResources("string"))
    }

    override fun start(stage: Stage) {
        stage.icon = Image(R.png.logo_launcher)
        setIconOnOSX(Toolkit.getDefaultToolkit().getImage(App::class.java.getResource(R.png.logo_launcher)))

        LoginDialog()
                .showAndWait()
                .filter { it is String }
                .ifPresent {
                    val minSize = Pair(960.0, 640.0)
                    stage.apply {
                        scene = Scene(FXMLLoader.load(App::class.java.getResource(R.fxml.layout_main), resources), minSize.first, minSize.second)
                        title = getString(R.string.app_name)
                        minWidth = minSize.first
                        minHeight = minSize.second
                    }.show()
                }
    }

    inner class LoginDialog : Dialog<Any>() {
        val content = Content()
        val expandableContent = ExpandableContent()
        val loginButton = ButtonType(getString(R.string.login), ButtonBar.ButtonData.OK_DONE)

        init {
            title = getString(R.string.app_name)
            headerText = getString(R.string.login)
            graphic = ImageView(R.png.ic_launcher)
            isResizable = false

            dialogPane.content = content
            dialogPane.expandableContent = expandableContent

            dialogPane.buttonTypes.addAll(CANCEL, loginButton)
            dialogPane.lookupButton(loginButton).addEventFilter(ActionEvent.ACTION) { event ->
                event.consume()
                MySQLFile.save()
                if (!getByName(content.ipField.text).isReachable(IP_LOOKUP_TIMEOUT)) {
                    errorAlert(getString(R.string.ip_address_unreachable)).showAndWait()
                    return@addEventFilter
                }
                dialog<String>(getString(R.string.password), ImageView(R.png.ic_key), getString(R.string.password_required)) {
                    val passwordLabel = Label(getString(R.string.password))
                    val passwordField = PasswordField().apply { promptText = getString(R.string.password) }
                    val passwordToggle = ToggleButton().apply { attachButtons(R.png.btn_visibility, R.png.btn_visibility_off) }
                    buttonTypes.addAll(CANCEL, OK)
                    lookupButton(OK).disableProperty().bind(passwordField.textProperty().isEmpty)
                    passwordField.tooltipProperty().bind(bindingOf(passwordField.textProperty(), passwordToggle.selectedProperty()) {
                        if (!passwordToggle.isSelected) null
                        else Tooltip(passwordField.text)
                    })
                    content = HBox().apply {
                        spacing = 8.0
                        alignment = CENTER
                        children.addAll(passwordLabel, passwordField, passwordToggle)
                    }
                    runLater { passwordField.requestFocus() }

                    if (BuildConfig.DEBUG) {
                        passwordField.text = "justforApp1e!"
                    }

                    Callback { if (it == OK) passwordField.text else null }
                }.showAndWait().filter { it != null }.ifPresent { password ->
                    try {
                        connectDatabase(content.ipField.text, content.portField.text, content.usernameField.text, password)
                        result = content.usernameField.text
                        close()
                    } catch (e: Exception) {
                        errorAlert(e.message ?: "Unknown error!").showAndWait()
                    }
                }
            }
            dialogPane.lookupButton(loginButton).disableProperty().bind(content.usernameField.textProperty().isEmpty
                    or not(content.ipField.validProperty)
                    or content.portField.textProperty().isEmpty)

            content.usernameField.textProperty().bindBidirectional(MySQLFile[MySQLFile.USERNAME])
            content.ipField.textProperty().bindBidirectional(MySQLFile[MySQLFile.IP])
            content.portField.textProperty().bindBidirectional(MySQLFile[MySQLFile.PORT])

            runLater { content.usernameField.requestFocus() }
        }

        inner class Content : GridPane() {
            val languageLabel = Label(getString(R.string.language))
            val languageBox = ChoiceBox<Language>(Language.listAll()).apply { maxWidth = Double.MAX_VALUE }
            val usernameLabel = Label(getString(R.string.username))
            val usernameField = TextField(getString(R.string.username))
            val serverLabel = Label(getString(R.string.server))
            val ipField = IPField().apply {
                promptText = getString(R.string.ip_address)
                prefWidth = 128.0
            }
            val portField = IntField().apply {
                promptText = getString(R.string.port)
                prefWidth = 64.0
            }

            init {
                setGaps(8.0)
                add(languageLabel, 0, 0)
                add(languageBox, 1, 0, 2, 1)
                add(usernameLabel, 0, 1)
                add(usernameField, 1, 1, 2, 1)
                add(serverLabel, 0, 2)
                add(ipField, 1, 2)
                add(portField, 2, 2)

                val initialLanguage = Language.parse(PreferencesFile[PreferencesFile.LANGUAGE].value)
                languageBox.selectionModel.select(initialLanguage)
                languageBox.selectionModel.selectedItemProperty().addListener { _, _, newValue ->
                    PreferencesFile.apply { get(PreferencesFile.LANGUAGE).set(newValue.locale) }.save()
                    close()
                    infoAlert(getString(R.string.language_changed)).showAndWait()
                    exit()
                }
            }
        }

        inner class ExpandableContent : GridPane() {
            val aboutLabel = Label("An open-source software.\nFor more information and update, visit:")
            val hyperlink = Hyperlink("https://github.com/WijayaPrinting/").apply { setOnAction { getDesktop().browse(URI(text)) } }
            val javafxLabel = Label("JavaFX")
            val javafxLabel2 = Label(BuildConfig.VERSION)
            val mysqlLabel = Label("MySQL")
            val mysqlLabel2 = Label(com.wijayaprinting.data.BuildConfig.VERSION)

            init {
                setGaps(8.0)
                add(aboutLabel, 0, 0, 2, 1)
                add(hyperlink, 0, 1, 2, 1)
                add(javafxLabel, 0, 2)
                add(javafxLabel2, 1, 2)
                add(mysqlLabel, 0, 3)
                add(mysqlLabel2, 1, 3)
            }
        }
    }
}