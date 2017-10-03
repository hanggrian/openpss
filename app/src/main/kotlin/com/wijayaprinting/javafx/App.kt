package com.wijayaprinting.javafx

import com.wijayaprinting.javafx.io.MySQLFile
import com.wijayaprinting.javafx.io.PreferencesFile
import com.wijayaprinting.javafx.scene.control.IPField
import com.wijayaprinting.javafx.scene.control.IntField
import com.wijayaprinting.javafx.scene.utils.attachButtons
import com.wijayaprinting.javafx.scene.utils.setGap
import com.wijayaprinting.javafx.utils.icon
import com.wijayaprinting.javafx.utils.setIconOnOSX
import com.wijayaprinting.mysql.MySQL
import javafx.application.Application
import javafx.event.ActionEvent
import javafx.fxml.FXMLLoader
import javafx.geometry.Pos
import javafx.scene.Scene
import javafx.scene.control.*
import javafx.scene.image.Image
import javafx.scene.image.ImageView
import javafx.scene.layout.GridPane
import javafx.scene.layout.VBox
import javafx.scene.text.Font
import javafx.stage.Stage
import kotfx.bindings.bindingOf
import kotfx.bindings.not
import kotfx.bindings.or
import kotfx.dialogs.errorAlert
import kotfx.dialogs.infoAlert
import kotfx.exitFXApplication
import kotfx.runLater
import java.awt.Toolkit
import java.net.InetAddress

/**
 * @author Hendra Anggrian (hendraanggrian@gmail.com)
 */
class App : Application() {

    companion object {
        @JvmStatic
        fun main(vararg args: String) = launch(App::class.java, *args)
    }

    override fun init() {
        initResources(Language.parse(PreferencesFile()[PreferencesFile.LANGUAGE].value).getResources("string"))
    }

    override fun start(stage: Stage) {
        stage.icon = Image(R.png.logo_launcher)
        setIconOnOSX(Toolkit.getDefaultToolkit().getImage(App::class.java.getResource(R.png.logo_launcher)))

        LoginDialog()
                .apply {
                    icon = Image(R.png.ic_launcher)
                    title = "${getString(R.string.app_name)} ${BuildConfig.VERSION}"
                    graphic.children.add(ImageView(Image(R.png.ic_launcher)))
                }
                .showAndWait()
                .filter { it is String }
                .ifPresent {
                    val minSize = Pair(720.0, 640.0)
                    stage.apply {
                        scene = Scene(FXMLLoader.load(App::class.java.getResource(R.fxml.layout_main), resources), minSize.first, minSize.second)
                        icons.add(Image(R.png.ic_launcher))
                        title = "${getString(R.string.app_name)} ${BuildConfig.VERSION}"
                        minWidth = minSize.first
                        minHeight = minSize.second
                    }.show()
                }
    }

    class LoginDialog : Dialog<Any>() {
        companion object {
            private const val IP_LOOKUP_TIMEOUT = 3000
        }

        val preferencesFile = PreferencesFile()
        val mysqlFile = MySQLFile()

        val graphic = Graphic()
        val content = Content()
        val expandableContent = ExpandableContent()
        val loginButton = ButtonType(getString(R.string.login), ButtonBar.ButtonData.OK_DONE)

        init {
            this.title = "${getString(R.string.app_name)} ${BuildConfig.VERSION}"
            this.headerText = getString(R.string.login)
            setGraphic(graphic)
            isResizable = false

            dialogPane.content = content
            dialogPane.expandableContent = expandableContent

            dialogPane.buttonTypes.addAll(ButtonType.CANCEL, loginButton)
            dialogPane.lookupButton(loginButton).addEventFilter(ActionEvent.ACTION) { event ->
                event.consume()
                mysqlFile.save()
                when (InetAddress.getByName(expandableContent.ipField.text).isReachable(IP_LOOKUP_TIMEOUT)) {
                    false -> errorAlert(getString(R.string.ip_address_unreachable)).showAndWait()
                    true -> {
                        try {
                            MySQL.connect(
                                    expandableContent.ipField.text,
                                    expandableContent.portField.text,
                                    content.usernameField.text,
                                    content.passwordField.text)
                            result = content.usernameField.text
                            close()
                        } catch (e: Exception) {
                            errorAlert(e.message ?: "Unknown error!").showAndWait()
                        }
                    }
                }
            }
            dialogPane.lookupButton(loginButton).disableProperty().bind(content.usernameField.textProperty().isEmpty
                    or content.passwordField.textProperty().isEmpty
                    or not(expandableContent.ipField.validProperty)
                    or expandableContent.portField.textProperty().isEmpty)

            content.usernameField.textProperty().bindBidirectional(mysqlFile[MySQLFile.USERNAME])
            expandableContent.ipField.textProperty().bindBidirectional(mysqlFile[MySQLFile.IP])
            expandableContent.portField.textProperty().bindBidirectional(mysqlFile[MySQLFile.PORT])

            runLater {
                if (content.usernameField.text.isEmpty()) content.usernameField.requestFocus()
                else content.passwordField.requestFocus()
                dialogPane.isExpanded = !expandableContent.ipField.isValid || expandableContent.portField.text.isEmpty()
            }
        }

        inner class Graphic : VBox(Label("MySQL ${com.wijayaprinting.mysql.BuildConfig.VERSION} | JavaFX ${BuildConfig.VERSION}").apply { font = Font(9.0) }) {
            init {
                alignment = Pos.CENTER_RIGHT
            }
        }

        inner class Content : GridPane() {
            val languageLabel = Label(getString(R.string.language))
            val languageBox = ChoiceBox<Language>(Language.listAll())
            val usernameLabel = Label(getString(R.string.username))
            val usernameField = TextField(getString(R.string.username))
            val passwordLabel = Label(getString(R.string.password))
            val passwordField = PasswordField().apply {
                promptText = getString(R.string.password)
                tooltip = Tooltip()
            }
            val passwordToggle = ToggleButton().apply { attachButtons(R.png.btn_visibility, R.png.btn_visibility_off) }

            init {
                setGap(8.0)
                add(languageLabel, 0, 0)
                add(languageBox, 1, 0)
                add(usernameLabel, 0, 1)
                add(usernameField, 1, 1, 2, 1)
                add(passwordLabel, 0, 2)
                add(passwordField, 1, 2)
                add(passwordToggle, 2, 2)

                val initialLanguage = Language.parse(preferencesFile[PreferencesFile.LANGUAGE].value)
                languageBox.selectionModel.select(initialLanguage)
                languageBox.selectionModel.selectedItemProperty().addListener { _, _, newValue ->
                    preferencesFile.apply { get(PreferencesFile.LANGUAGE).set(newValue.locale) }.save()
                    close()
                    infoAlert(getString(R.string.language_changed)).showAndWait()
                    exitFXApplication()
                }
                passwordField.tooltipProperty().bind(bindingOf(passwordField.textProperty(), passwordToggle.selectedProperty()) {
                    if (!passwordToggle.isSelected) null
                    else Tooltip(passwordField.text)
                })
            }
        }

        inner class ExpandableContent : GridPane() {
            val ipLabel = Label(getString(R.string.ip_address))
            val ipField = IPField().apply { promptText = "127.0.0.1" }
            val portLabel = Label(getString(R.string.port))
            val portField = IntField().apply { promptText = "3306" }

            init {
                setGap(8.0)
                add(ipLabel, 0, 0)
                add(ipField, 1, 0)
                add(portLabel, 0, 1)
                add(portField, 1, 1)
            }
        }
    }
}