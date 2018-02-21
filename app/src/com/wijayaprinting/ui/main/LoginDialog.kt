package com.wijayaprinting.ui.main

import com.wijayaprinting.BuildConfig.DEBUG
import com.wijayaprinting.Language
import com.wijayaprinting.R
import com.wijayaprinting.db.Database
import com.wijayaprinting.io.properties.ConfigFile
import com.wijayaprinting.io.properties.MongoFile
import com.wijayaprinting.scene.control.HostField
import com.wijayaprinting.scene.control.IntField
import com.wijayaprinting.scene.control.hostField
import com.wijayaprinting.scene.control.intField
import com.wijayaprinting.ui.Resourced
import javafx.event.ActionEvent.ACTION
import javafx.geometry.Pos.CENTER_RIGHT
import javafx.scene.control.ButtonBar.ButtonData.OK_DONE
import javafx.scene.control.ButtonType.CANCEL
import javafx.scene.control.Dialog
import javafx.scene.control.PasswordField
import javafx.scene.control.TextField
import javafx.scene.control.Tooltip
import javafx.scene.image.Image
import javafx.scene.image.ImageView
import kotfx.bindings.`else`
import kotfx.bindings.`if`
import kotfx.bindings.bindingOf
import kotfx.bindings.not
import kotfx.bindings.or
import kotfx.bindings.then
import kotfx.collections.toObservableList
import kotfx.coroutines.eventFilter
import kotfx.coroutines.launchFX
import kotfx.coroutines.onAction
import kotfx.dialogs.addButton
import kotfx.dialogs.content
import kotfx.dialogs.errorAlert
import kotfx.dialogs.expandableContent
import kotfx.dialogs.icon
import kotfx.dialogs.infoAlert
import kotfx.dialogs.isExpanded
import kotfx.exit
import kotfx.gap
import kotfx.layout.choiceBox
import kotfx.layout.gridPane
import kotfx.layout.hbox
import kotfx.layout.hyperlink
import kotfx.layout.label
import kotfx.layout.passwordField
import kotfx.layout.textField
import kotfx.layout.toggleButton
import kotfx.layout.tooltip
import kotfx.runLater
import kotlinx.coroutines.experimental.javafx.JavaFx

class LoginDialog(resourced: Resourced) : Dialog<Any>(), Resourced by resourced {

    private lateinit var employeeField: TextField
    private lateinit var passwordField: PasswordField

    private lateinit var serverHostField: HostField
    private lateinit var serverPortField: IntField
    private lateinit var serverUserField: TextField
    private lateinit var serverPasswordField: PasswordField

    init {
        icon = Image(R.image.ic_launcher)
        title = getString(R.string.app_name)
        headerText = getString(R.string.login)
        graphic = ImageView(R.image.ic_launcher)
        isResizable = false
        content = gridPane {
            gap = 8.0
            label(getString(R.string.language)) col 0 row 0
            choiceBox(Language.values().toObservableList()) {
                maxWidth = Double.MAX_VALUE
                selectionModel.select(Language.from(ConfigFile.language.get()))
                valueProperty().addListener { _, _, language ->
                    ConfigFile.language.set(language.locale)
                    ConfigFile.save()
                    close()
                    infoAlert(getString(R.string.please_restart)).showAndWait().ifPresent { exit() }
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
                graphic = kotfx.layout.imageView { imageProperty().bind(`if`(this@toggleButton.selectedProperty()) then Image(R.image.btn_visibility) `else` Image(R.image.btn_visibility_off)) }
                passwordField.tooltipProperty().bind(bindingOf(passwordField.textProperty(), selectedProperty()) { if (!isSelected) null else Tooltip(passwordField.text) })
            } col 2 row 2
        }
        expandableContent = gridPane {
            gap = 8.0
            label(getString(R.string.server_host_port)) col 0 row 0
            serverHostField = hostField {
                promptText = getString(R.string.ip_address)
                prefWidth = 128.0
                textProperty().bindBidirectional(MongoFile.host)
            } col 1 row 0
            serverPortField = intField {
                promptText = getString(R.string.port)
                prefWidth = 64.0
                textProperty().bindBidirectional(MongoFile.port)
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
        addButton(CANCEL)
        addButton(getString(R.string.login), OK_DONE).apply {
            disableProperty().bind(employeeField.textProperty().isEmpty
                or passwordField.textProperty().isEmpty
                or not(serverHostField.validProperty)
                or serverPortField.textProperty().isEmpty
                or serverUserField.textProperty().isEmpty
                or serverPasswordField.textProperty().isEmpty)
            eventFilter(JavaFx, ACTION) {
                it.consume()
                ConfigFile.save()
                MongoFile.save()
                try {
                    result = Database.login(serverHostField.text, serverPortField.value, serverUserField.text, serverPasswordField.text, employeeField.text, passwordField.text)
                    launchFX { close() }
                } catch (e: Exception) {
                    if (DEBUG) e.printStackTrace()
                    launchFX { errorAlert(e.message.toString()).showAndWait() }
                }
            }
        }
        runLater {
            if (employeeField.text.isBlank()) employeeField.requestFocus() else passwordField.requestFocus()
            isExpanded = !MongoFile.isValid
            if (DEBUG) {
                passwordField.text = "Test"
            }
        }
    }
}