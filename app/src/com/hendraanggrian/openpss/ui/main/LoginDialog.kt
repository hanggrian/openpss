package com.hendraanggrian.openpss.ui.main

import com.hendraanggrian.openpss.BuildConfig.APP_NAME
import com.hendraanggrian.openpss.BuildConfig.DEBUG
import com.hendraanggrian.openpss.Language
import com.hendraanggrian.openpss.R
import com.hendraanggrian.openpss.db.login
import com.hendraanggrian.openpss.io.properties.ConfigFile
import com.hendraanggrian.openpss.io.properties.MongoFile
import com.hendraanggrian.openpss.scene.control.HostField
import com.hendraanggrian.openpss.scene.control.IntField
import com.hendraanggrian.openpss.scene.control.hostField
import com.hendraanggrian.openpss.scene.control.intField
import com.hendraanggrian.openpss.ui.Resourced
import com.mongodb.ServerAddress.defaultPort
import javafx.event.ActionEvent.ACTION
import javafx.geometry.Pos.CENTER_RIGHT
import javafx.scene.control.ButtonBar.ButtonData.OK_DONE
import javafx.scene.control.Dialog
import javafx.scene.control.PasswordField
import javafx.scene.control.TextField
import javafx.scene.control.Tooltip
import javafx.scene.image.Image
import javafx.scene.image.ImageView
import kotlinfx.application.exit
import kotlinfx.application.later
import kotlinfx.beans.binding.`else`
import kotlinfx.beans.binding.`if`
import kotlinfx.beans.binding.bindingOf
import kotlinfx.beans.binding.or
import kotlinfx.beans.binding.then
import kotlinfx.collections.toObservableList
import kotlinfx.coroutines.FX
import kotlinfx.coroutines.listener
import kotlinfx.coroutines.onAction
import kotlinfx.layouts.choiceBox
import kotlinfx.layouts.gridPane
import kotlinfx.layouts.hbox
import kotlinfx.layouts.hyperlink
import kotlinfx.layouts.label
import kotlinfx.layouts.passwordField
import kotlinfx.layouts.textField
import kotlinfx.layouts.toggleButton
import kotlinfx.layouts.tooltip
import kotlinfx.scene.control.cancelButton
import kotlinfx.scene.control.customButton
import kotlinfx.scene.control.errorAlert
import kotlinfx.scene.control.icon
import kotlinfx.scene.control.infoAlert
import kotlinfx.scene.layout.gaps
import kotlinfx.scene.layout.prefSize
import kotlinx.coroutines.experimental.CommonPool
import kotlinx.coroutines.experimental.launch

class LoginDialog(resourced: Resourced) : Dialog<Any>(), Resourced by resourced {

    private lateinit var employeeField: TextField
    private lateinit var passwordField: PasswordField

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
            gaps = 8
            label(getString(R.string.language)) col 0 row 0
            choiceBox(Language.values().toObservableList()) {
                maxWidth = Double.MAX_VALUE
                selectionModel.select(Language.from(ConfigFile.language.get()))
                valueProperty().listener(CommonPool) { _, _, language ->
                    ConfigFile.language.set(language.code)
                    ConfigFile.save()
                    launch(FX) {
                        close()
                        infoAlert(getString(R.string.please_restart)).showAndWait().ifPresent { exit() }
                    }
                }
            } col 1 row 0 colSpan 2
            label(getString(R.string.employee)) col 0 row 1
            employeeField = textField {
                promptText = getString(R.string.employee)
                textProperty().bindBidirectional(ConfigFile.employee)
            } col 1 row 1 colSpan 2
            label(getString(R.string.password)) col 0 row 2
            passwordField = passwordField { promptText = getString(R.string.password) } col 1 row 2
            toggleButton {
                tooltip(getString(R.string.see_password))
                graphic = kotlinfx.layouts.imageView { imageProperty().bind(`if`(this@toggleButton.selectedProperty()) then Image(R.image.btn_visibility) `else` Image(R.image.btn_visibility_off)) }
                passwordField.tooltipProperty().bind(bindingOf(passwordField.textProperty(), selectedProperty()) { if (!isSelected) null else Tooltip(passwordField.text) })
            } col 2 row 2
        }
        dialogPane.expandableContent = gridPane {
            gaps = 8
            label(getString(R.string.server_host_port)) col 0 row 0
            serverHostField = hostField {
                promptText = getString(R.string.ip_address)
                prefSize(width = 128)
                textProperty().bindBidirectional(MongoFile.host)
            } col 1 row 0
            serverPortField = intField {
                promptText = getString(R.string.port)
                prefSize(width = 64)
                textProperty().bindBidirectional(MongoFile.port)
                if (value == 0) text = defaultPort().toString()
            } col 2 row 0
            label(getString(R.string.server_user)) col 0 row 1
            serverUserField = textField {
                promptText = getString(R.string.server_user)
                textProperty().bindBidirectional(MongoFile.user)
            } col 1 row 1 colSpan 2
            label(getString(R.string.server_password)) col 0 row 2
            serverPasswordField = passwordField {
                promptText = getString(R.string.server_password)
                textProperty().bindBidirectional(MongoFile.password)
            } col 1 row 2 colSpan 2
            hbox {
                alignment = CENTER_RIGHT
                hyperlink(getString(R.string.about)) {
                    onAction { AboutDialog(this@LoginDialog).showAndWait() }
                } marginLeft 8
            } col 0 row 3 colSpan 3
        }
        cancelButton()
        customButton(getString(R.string.login), OK_DONE) {
            disableProperty().bind(employeeField.textProperty().isEmpty
                or passwordField.textProperty().isEmpty
                or !serverHostField.validProperty
                or serverPortField.textProperty().isEmpty
                or serverUserField.textProperty().isEmpty
                or serverPasswordField.textProperty().isEmpty)
            addEventFilter(ACTION) {
                it.consume()
                launch {
                    ConfigFile.save()
                    MongoFile.save()
                    try {
                        val employee = login(serverHostField.text, serverPortField.value, serverUserField.text, serverPasswordField.text, employeeField.text, passwordField.text)
                        launch(FX) {
                            result = employee
                            close()
                        }
                    } catch (e: Exception) {
                        if (DEBUG) e.printStackTrace()
                        launch(FX) { errorAlert(e.message.toString()).showAndWait() }
                    }
                }
            }
        }
        later {
            if (employeeField.text.isBlank()) employeeField.requestFocus() else passwordField.requestFocus()
            dialogPane.isExpanded = !MongoFile.isValid()
            if (DEBUG) {
                passwordField.text = "Test"
            }
        }
    }
}