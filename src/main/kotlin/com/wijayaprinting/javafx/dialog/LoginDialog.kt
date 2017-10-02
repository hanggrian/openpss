package com.wijayaprinting.javafx.dialog

import com.wijayaprinting.javafx.Language
import com.wijayaprinting.javafx.R
import com.wijayaprinting.javafx.Resourced
import com.wijayaprinting.javafx.control.button.ImageToggleButton
import com.wijayaprinting.javafx.control.field.IPField
import com.wijayaprinting.javafx.control.field.IntField
import com.wijayaprinting.javafx.control.field.PasswordField
import com.wijayaprinting.javafx.control.field.TextField
import com.wijayaprinting.javafx.io.JavaFXFile
import com.wijayaprinting.javafx.io.JavaFXFile.Companion.IP
import com.wijayaprinting.javafx.io.JavaFXFile.Companion.LANGUAGE
import com.wijayaprinting.javafx.io.JavaFXFile.Companion.PORT
import com.wijayaprinting.javafx.io.JavaFXFile.Companion.USERNAME
import com.wijayaprinting.javafx.layout.GridPane
import com.wijayaprinting.mysql.MySQL
import javafx.event.ActionEvent
import javafx.geometry.Pos
import javafx.scene.control.*
import javafx.scene.layout.VBox
import javafx.scene.text.Font
import kotfx.bindings.bindingOf
import kotfx.bindings.not
import kotfx.bindings.or
import kotfx.dialogs.errorAlert
import kotfx.dialogs.infoAlert
import kotfx.exitFXApplication
import kotfx.runLater
import java.net.InetAddress
import java.util.*

/**
 * @author Hendra Anggrian (hendraanggrian@gmail.com)
 */
class LoginDialog(override val resources: ResourceBundle) : Dialog<Any>(), Resourced {

    companion object {
        private const val IP_LOOKUP_TIMEOUT = 3000
    }

    val file = JavaFXFile()

    val graphic = Graphic()
    val content = Content()
    val expandableContent = ExpandableContent()
    val loginButton = ButtonType(getString(R.javafx.login), ButtonBar.ButtonData.OK_DONE)

    init {
        this.title = getString(R.javafx.wijaya_printing_login)
        this.headerText = getString(R.javafx.wijaya_printing_login)
        setGraphic(graphic)
        isResizable = false

        dialogPane.content = content
        dialogPane.expandableContent = expandableContent

        dialogPane.buttonTypes.addAll(ButtonType.CANCEL, loginButton)
        dialogPane.lookupButton(loginButton).addEventFilter(ActionEvent.ACTION) { event ->
            event.consume()
            file.save()
            when (InetAddress.getByName(expandableContent.ipField.text).isReachable(IP_LOOKUP_TIMEOUT)) {
                false -> errorAlert(getString(R.javafx.ip_address_unreachable)).showAndWait()
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

        content.usernameField.textProperty().bindBidirectional(file[USERNAME])
        expandableContent.ipField.textProperty().bindBidirectional(file[IP])
        expandableContent.portField.textProperty().bindBidirectional(file[PORT])

        runLater {
            if (content.usernameField.text.isEmpty()) content.usernameField.requestFocus()
            else content.passwordField.requestFocus()
            dialogPane.isExpanded = !expandableContent.ipField.isValid || expandableContent.portField.text.isEmpty()
        }
    }

    inner class Graphic : VBox(Label("MySQL ${com.wijayaprinting.mysql.BuildConfig.VERSION} | JavaFX ${com.wijayaprinting.javafx.BuildConfig.VERSION}").apply { font = Font(9.0) }) {
        init {
            alignment = Pos.CENTER_RIGHT
        }
    }

    inner class Content : GridPane(8.0) {
        val languageLabel = Label(getString(R.javafx.language))
        val languageBox = ChoiceBox<Language>(Language.listAll())
        val usernameLabel = Label(getString(R.javafx.username))
        val usernameField = TextField(getString(R.javafx.username))
        val passwordLabel = Label(getString(R.javafx.password))
        val passwordField = PasswordField(getString(R.javafx.password)).apply { tooltip = Tooltip() }
        val passwordToggle = ImageToggleButton(R.png.btn_visibility, R.png.btn_visibility_off)

        init {
            add(languageLabel, 0, 0)
            add(languageBox, 1, 0)
            add(usernameLabel, 0, 1)
            add(usernameField, 1, 1, 2, 1)
            add(passwordLabel, 0, 2)
            add(passwordField, 1, 2)
            add(passwordToggle, 2, 2)

            val initialLanguage = Language.parse(file[LANGUAGE].value)
            languageBox.selectionModel.select(initialLanguage)
            languageBox.selectionModel.selectedItemProperty().addListener { _, _, newValue ->
                file.apply { get(LANGUAGE).set(newValue.locale) }.save()
                close()
                infoAlert(getString(R.javafx.language_changed)).showAndWait()
                exitFXApplication()
            }
            passwordField.tooltipProperty().bind(bindingOf(passwordField.textProperty(), passwordToggle.selectedProperty()) {
                if (!passwordToggle.isSelected) null
                else Tooltip(passwordField.text)
            })
        }
    }

    inner class ExpandableContent : GridPane(8.0) {
        val ipLabel = Label(getString(R.javafx.ip_address))
        val ipField = IPField("127.0.0.1")
        val portLabel = Label(getString(R.javafx.port))
        val portField = IntField("3306")

        init {
            add(ipLabel, 0, 0)
            add(ipField, 1, 0)
            add(portLabel, 0, 1)
            add(portField, 1, 1)
        }
    }
}