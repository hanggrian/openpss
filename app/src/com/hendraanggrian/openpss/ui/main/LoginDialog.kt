package com.hendraanggrian.openpss.ui.main

import com.hendraanggrian.openpss.BuildConfig.DEBUG
import com.hendraanggrian.openpss.BuildConfig.NAME
import com.hendraanggrian.openpss.R
import com.hendraanggrian.openpss.control.HostField
import com.hendraanggrian.openpss.control.IntField
import com.hendraanggrian.openpss.control.PasswordBox
import com.hendraanggrian.openpss.control.dialog.Dialog
import com.hendraanggrian.openpss.control.hostField
import com.hendraanggrian.openpss.control.intField
import com.hendraanggrian.openpss.control.onActionFilter
import com.hendraanggrian.openpss.db.login
import com.hendraanggrian.openpss.i18n.Language
import com.hendraanggrian.openpss.i18n.Resourced
import com.hendraanggrian.openpss.io.properties.LoginFile
import com.hendraanggrian.openpss.io.properties.PreferencesFile
import com.hendraanggrian.openpss.ui.main.help.AboutDialog
import com.hendraanggrian.openpss.ui.main.help.GitHubApi
import com.hendraanggrian.openpss.util.forceExit
import com.hendraanggrian.openpss.util.getStyle
import javafx.geometry.Pos.CENTER_RIGHT
import javafx.scene.control.ButtonBar.ButtonData.OK_DONE
import javafx.scene.control.ButtonType.CANCEL
import javafx.scene.control.PasswordField
import javafx.scene.control.TextField
import ktfx.application.later
import ktfx.beans.value.isBlank
import ktfx.beans.value.or
import ktfx.collections.toObservableList
import ktfx.coroutines.listener
import ktfx.coroutines.onAction
import ktfx.layouts.choiceBox
import ktfx.layouts.gridPane
import ktfx.layouts.hbox
import ktfx.layouts.hyperlink
import ktfx.layouts.label
import ktfx.layouts.passwordField
import ktfx.layouts.textField
import ktfx.scene.control.cancelButton
import ktfx.scene.control.customButton
import ktfx.scene.control.styledErrorAlert
import ktfx.scene.control.styledInfoAlert
import ktfx.scene.control.styledWarningAlert
import ktfx.scene.layout.gap
import kotlinx.coroutines.experimental.Dispatchers
import kotlinx.coroutines.experimental.GlobalScope
import kotlinx.coroutines.experimental.javafx.JavaFx
import kotlinx.coroutines.experimental.launch

class LoginDialog(resourced: Resourced) : Dialog<Any>(resourced, graphicId = R.image.header_launcher) {

    private lateinit var employeeField: TextField
    private lateinit var passwordBox: PasswordBox
    private lateinit var serverHostField: HostField
    private lateinit var serverPortField: IntField
    private lateinit var serverUserField: TextField
    private lateinit var serverPasswordField: PasswordField

    init {
        title = NAME
        headerText = getString(R.string.login)
        isResizable = false
        gridPane {
            gap = R.dimen.padding_medium.toDouble()
            label(getString(R.string.language)) col 0 row 0
            choiceBox(Language.values().toObservableList()) {
                maxWidth = Double.MAX_VALUE
                selectionModel.select(PreferencesFile.language)
                valueProperty().listener(Dispatchers.Default) { _, _, value ->
                    PreferencesFile.language = value
                    PreferencesFile.save()
                    GlobalScope.launch(Dispatchers.JavaFx) {
                        close()
                        later {
                            styledInfoAlert(getStyle(R.style.openpss), getString(R.string.please_restart))
                                .showAndWait().ifPresent { forceExit() }
                        }
                    }
                }
            } col 1 row 0
            label(getString(R.string.employee)) col 0 row 1
            employeeField = textField(LoginFile.EMPLOYEE) {
                promptText = getString(R.string.employee)
                textProperty().listener { _, _, newValue -> LoginFile.EMPLOYEE = newValue }
            } col 1 row 1
            label(getString(R.string.password)) col 0 row 2
            passwordBox = PasswordBox(this@LoginDialog)() col 1 row 2
            hbox {
                alignment = CENTER_RIGHT
                hyperlink(getString(R.string.check_for_updates)) {
                    onAction {
                        GitHubApi.checkUpdates(this@LoginDialog, { title, actions ->
                            styledWarningAlert(
                                getStyle(R.style.openpss),
                                title = title,
                                buttonTypes = *arrayOf(CANCEL)
                            ) {
                                dialogPane.content = ktfx.layouts.vbox {
                                    actions.forEach { action ->
                                        hyperlink(action.text) { onAction = action }
                                    }
                                }
                            }.show()
                        }) { title, content ->
                            styledInfoAlert(getStyle(R.style.openpss), title, contentText = content).show()
                        }
                    }
                } marginLeft R.dimen.padding_medium.toDouble()
                hyperlink(getString(R.string.about)) {
                    onAction { AboutDialog(this@LoginDialog).show() }
                } marginLeft R.dimen.padding_medium.toDouble()
            } col 0 row 3 colSpans 2
        }
        gridPane {
            gap = R.dimen.padding_medium.toDouble()
            label(getString(R.string.server_host_port)) col 0 row 0
            serverHostField = hostField {
                text = LoginFile.DB_HOST
                promptText = getString(R.string.ip_address)
                prefWidth = 128.0
                textProperty().listener { _, _, newValue -> LoginFile.DB_HOST = newValue }
            } col 1 row 0
            serverPortField = intField {
                value = LoginFile.DB_PORT
                promptText = getString(R.string.port)
                prefWidth = 64.0
                valueProperty().listener { _, _, newValue -> LoginFile.DB_PORT = newValue.toInt() }
            } col 2 row 0
            label(getString(R.string.server_user)) col 0 row 1
            serverUserField = textField(LoginFile.DB_USER) {
                promptText = getString(R.string.server_user)
                textProperty().listener { _, _, newValue -> LoginFile.DB_USER = newValue }
            } col 1 row 1 colSpans 2
            label(getString(R.string.server_password)) col 0 row 2
            serverPasswordField = passwordField {
                text = LoginFile.DB_PASSWORD
                promptText = getString(R.string.server_password)
                textProperty().listener { _, _, newValue -> LoginFile.DB_PASSWORD = newValue }
            } col 1 row 2 colSpans 2
        }
        cancelButton()
        customButton(getString(R.string.login), OK_DONE) {
            disableProperty().bind(
                employeeField.textProperty().isBlank()
                    or passwordBox.textProperty().isBlank()
                    or !serverHostField.validProperty()
                    or serverPortField.textProperty().isBlank()
                    or serverUserField.textProperty().isBlank()
                    or serverPasswordField.textProperty().isBlank()
            )
            onActionFilter(Dispatchers.Default) {
                LoginFile.save()
                try {
                    val employee = login(
                        serverHostField.text,
                        serverPortField.value,
                        serverUserField.text,
                        serverPasswordField.text,
                        employeeField.text,
                        passwordBox.text
                    )
                    GlobalScope.launch(Dispatchers.JavaFx) {
                        result = employee
                        close()
                    }
                } catch (e: Exception) {
                    if (DEBUG) e.printStackTrace()
                    GlobalScope.launch(Dispatchers.JavaFx) {
                        styledErrorAlert(getStyle(R.style.openpss), e.message.toString()).show()
                    }
                }
            }
        }
        later {
            if (employeeField.text.isBlank()) employeeField.requestFocus() else passwordBox.requestFocus()
            dialogPane.isExpanded = !LoginFile.isDbValid()
            if (DEBUG) passwordBox.text = "hendraganteng"
        }
    }
}