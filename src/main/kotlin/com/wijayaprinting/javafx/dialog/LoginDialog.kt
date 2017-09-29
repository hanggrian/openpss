package com.wijayaprinting.javafx.dialog

import com.wijayaprinting.javafx.Language
import com.wijayaprinting.javafx.R
import com.wijayaprinting.javafx.Resourced
import com.wijayaprinting.javafx.control.*
import com.wijayaprinting.javafx.io.JavaFXFile
import com.wijayaprinting.javafx.io.JavaFXFile.Companion.IP
import com.wijayaprinting.javafx.io.JavaFXFile.Companion.LANGUAGE
import com.wijayaprinting.javafx.io.JavaFXFile.Companion.PORT
import com.wijayaprinting.javafx.io.JavaFXFile.Companion.USERNAME
import com.wijayaprinting.javafx.utils.safeTransaction
import com.wijayaprinting.mysql.MySQL
import javafx.event.ActionEvent
import javafx.scene.control.*
import javafx.scene.image.Image
import javafx.scene.image.ImageView
import javafx.scene.layout.GridPane
import javafx.stage.Stage
import kotfx.bindings.bindingOf
import kotfx.bindings.not
import kotfx.bindings.or
import kotfx.dialogs.choiceDialog
import kotfx.dialogs.infoAlert
import kotfx.exitFXApplication
import kotfx.runLater
import java.util.*

/**
 * @author Hendra Anggrian (hendraanggrian@gmail.com)
 */
internal class LoginDialog(override val resources: ResourceBundle, title: String) : Dialog<Any>(), Resourced {

    val file = JavaFXFile()

    val content = Content()
    val expandableContent = ExpandableContent()
    val languageButton = ButtonType(getString(R.strings.language), ButtonBar.ButtonData.CANCEL_CLOSE)
    val loginButton = ButtonType(getString(R.strings.login), ButtonBar.ButtonData.OK_DONE)

    init {
        this.title = title
        headerText = getString(R.strings.wp_login)
        graphic = ImageView(Image(R.png.ic_launcher_96px))
        isResizable = false

        (dialogPane.scene.window as Stage).icons.add(Image(R.png.ic_launcher_96px))
        dialogPane.content = content
        dialogPane.expandableContent = expandableContent

        dialogPane.buttonTypes.addAll(languageButton, loginButton)
        dialogPane.lookupButton(languageButton).addEventFilter(ActionEvent.ACTION) { event ->
            event.consume()
            val initialLanguage = Language.parse(file[LANGUAGE].value)
            choiceDialog<Language>(getString(R.strings.language), initialLanguage, *Language.listAll().toTypedArray())
                    .showAndWait()
                    .filter { it != initialLanguage }
                    .ifPresent {
                        file.apply { get(LANGUAGE).set(it.locale) }.save()
                        close()
                        infoAlert(getString(R.strings._notice_language_changed)).showAndWait()
                        exitFXApplication()
                    }
        }
        dialogPane.lookupButton(loginButton).addEventFilter(ActionEvent.ACTION) { event ->
            event.consume()
            file.save()
            connect()
            if (safeTransaction { }) {
                result = content.usernameField.text
                close()
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

    fun connect() = MySQL.connect(
            expandableContent.ipField.text,
            expandableContent.portField.text,
            content.usernameField.text,
            content.passwordField.text)

    inner class Content : GridPane() {
        val usernameLabel = Label(getString(R.strings.username))
        val usernameField = PromptTextField(getString(R.strings.username))
        val passwordLabel = Label(getString(R.strings.password))
        val passwordField = PromptPasswordField(getString(R.strings.password)).apply { tooltip = Tooltip() }
        val passwordToggle = ImageToggleButton(R.png.ic_visibility_18dp, R.png.ic_visibility_off_18dp)

        init {
            hgap = 8.0
            vgap = 8.0

            add(usernameLabel, 0, 0)
            add(usernameField, 1, 0, 2, 1)
            add(passwordLabel, 0, 1)
            add(passwordField, 1, 1)
            add(passwordToggle, 2, 1)

            passwordField.tooltipProperty().bind(bindingOf(passwordField.textProperty(), passwordToggle.selectedProperty()) {
                if (!passwordToggle.isSelected) null
                else Tooltip(passwordField.text)
            })
        }
    }

    inner class ExpandableContent : GridPane() {
        val ipLabel = Label("IP Address")
        val ipField = IPField("127.0.0.1")
        val portLabel = Label("Port")
        val portField = IntField("3306")

        init {
            hgap = 8.0
            vgap = 8.0

            add(ipLabel, 0, 0)
            add(ipField, 1, 0)
            add(portLabel, 0, 1)
            add(portField, 1, 1)
        }
    }
}