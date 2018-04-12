package com.hendraanggrian.openpss.ui.main

import com.hendraanggrian.openpss.App
import com.hendraanggrian.openpss.BuildConfig.APP_NAME
import com.hendraanggrian.openpss.BuildConfig.DEBUG
import com.hendraanggrian.openpss.R
import com.hendraanggrian.openpss.db.login
import com.hendraanggrian.openpss.io.properties.LoginFile
import com.hendraanggrian.openpss.scene.control.HostField
import com.hendraanggrian.openpss.scene.control.IntField
import com.hendraanggrian.openpss.scene.control.hostField
import com.hendraanggrian.openpss.scene.control.intField
import com.hendraanggrian.openpss.ui.Resourced
import com.hendraanggrian.openpss.utils.onActionFilter
import javafx.geometry.Pos.CENTER_RIGHT
import javafx.scene.control.ButtonBar.ButtonData.OK_DONE
import javafx.scene.control.Dialog
import javafx.scene.control.PasswordField
import javafx.scene.control.TextField
import javafx.scene.image.Image
import javafx.scene.image.ImageView
import kotlinx.coroutines.experimental.CommonPool
import kotlinx.coroutines.experimental.launch
import ktfx.application.exit
import ktfx.application.later
import ktfx.beans.binding.`when`
import ktfx.beans.binding.otherwise
import ktfx.beans.binding.then
import ktfx.beans.value.isBlank
import ktfx.beans.value.or
import ktfx.coroutines.FX
import ktfx.coroutines.listener
import ktfx.coroutines.onAction
import ktfx.layouts.anchorPane
import ktfx.layouts.choiceBox
import ktfx.layouts.gridPane
import ktfx.layouts.hbox
import ktfx.layouts.hyperlink
import ktfx.layouts.label
import ktfx.layouts.passwordField
import ktfx.layouts.textField
import ktfx.layouts.toggleButton
import ktfx.layouts.tooltip
import ktfx.listeners.converter
import ktfx.scene.control.button
import ktfx.scene.control.cancelButton
import ktfx.scene.control.errorAlert
import ktfx.scene.control.icon
import ktfx.scene.control.infoAlert
import ktfx.scene.layout.gap
import java.util.Locale

class LoginDialog(resourced: Resourced) : Dialog<Any>(), Resourced by resourced {

    private lateinit var employeeField: TextField
    private lateinit var passwordField1: PasswordField
    private lateinit var passwordField2: TextField
    private lateinit var serverHostField: HostField
    private lateinit var serverPortField: IntField
    private lateinit var serverUserField: TextField
    private lateinit var serverPasswordField: PasswordField

    init {
        icon = Image(R.image.ic_launcher)
        title = APP_NAME
        headerText = getString(R.string.login)
        graphic = ImageView(R.image.ic_launcher)
        isResizable = false
        dialogPane.content = gridPane {
            gap = 8.0
            label(getString(R.string.language)) col 0 row 0
            choiceBox(App.supportedLocales) {
                maxWidth = Double.MAX_VALUE
                selectionModel.select(Locale(LoginFile.LANGUAGE))
                converter { toString { it!!.getDisplayLanguage(it) } }
                valueProperty().listener(CommonPool) { _, _, locale ->
                    LoginFile.LANGUAGE = locale.language
                    LoginFile.save()
                    launch(FX) {
                        close()
                        later { infoAlert(getString(R.string.please_restart)).showAndWait().ifPresent { exit() } }
                    }
                }
            } col 1 row 0 colSpans 2
            label(getString(R.string.employee)) col 0 row 1
            employeeField = textField(LoginFile.EMPLOYEE) {
                promptText = getString(R.string.employee)
                textProperty().listener { _, _, newValue -> LoginFile.EMPLOYEE = newValue }
            } col 1 row 1 colSpans 2
            label(getString(R.string.password)) col 0 row 2
            anchorPane {
                passwordField1 = passwordField { promptText = getString(R.string.password) }
                passwordField2 = textField {
                    isVisible = false
                    promptText = getString(R.string.password)
                }
                passwordField1.textProperty().bindBidirectional(passwordField2.textProperty())
            } col 1 row 2
            toggleButton {
                tooltip(getString(R.string.see_password))
                graphic = ktfx.layouts.imageView {
                    imageProperty().bind(`when`(this@toggleButton.selectedProperty())
                        then Image(R.image.btn_visibility_on) otherwise Image(R.image.btn_visibility_off))
                }
                selectedProperty().listener { _, _, selected ->
                    passwordField1.isVisible = !selected
                    passwordField2.isVisible = selected
                }
            } col 2 row 2
        }
        dialogPane.expandableContent = gridPane {
            gap = 8.0
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
                valueProperty.listener { _, _, newValue -> LoginFile.DB_PORT = newValue.toInt() }
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
            hbox {
                alignment = CENTER_RIGHT
                hyperlink(getString(R.string.about)) {
                    onAction { AboutDialog(this@LoginDialog).showAndWait() }
                } marginLeft 8.0
            } col 0 row 3 colSpans 3
        }
        cancelButton()
        button(getString(R.string.login), OK_DONE) {
            disableProperty().bind(employeeField.textProperty().isBlank()
                or passwordField1.textProperty().isBlank()
                or !serverHostField.validProperty
                or serverPortField.textProperty().isBlank()
                or serverUserField.textProperty().isBlank()
                or serverPasswordField.textProperty().isBlank())
            onActionFilter(CommonPool) {
                LoginFile.save()
                try {
                    val employee = login(
                        serverHostField.text,
                        serverPortField.value,
                        serverUserField.text,
                        serverPasswordField.text,
                        employeeField.text,
                        passwordField1.text)
                    launch(FX) {
                        result = employee
                        close()
                    }
                } catch (e: Exception) {
                    if (DEBUG) e.printStackTrace()
                    launch(FX) { errorAlert(e.message.toString()).show() }
                }
            }
        }
        later {
            if (employeeField.text.isBlank()) employeeField.requestFocus() else passwordField1.requestFocus()
            dialogPane.isExpanded = !LoginFile.isDbValid()
            if (DEBUG) passwordField1.text = "Test"
        }
    }
}