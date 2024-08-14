@file:Suppress(
    "ktlint:rulebook:qualifier-consistency",
    "ktlint:rulebook:exception-subclass-catching",
)

package com.hanggrian.openpss.ui.login

import com.hanggrian.openpss.BuildConfig
import com.hanggrian.openpss.Context
import com.hanggrian.openpss.Language
import com.hanggrian.openpss.OpenPssApp
import com.hanggrian.openpss.R
import com.hanggrian.openpss.Resources
import com.hanggrian.openpss.control.IntField
import com.hanggrian.openpss.db.login
import com.hanggrian.openpss.db.schemas.Employee
import com.hanggrian.openpss.io.properties.LoginFile
import com.hanggrian.openpss.io.properties.PreferencesFile
import com.hanggrian.openpss.popup.dialog.ResultableDialog
import com.hanggrian.openpss.popup.dialog.TextDialog
import com.hanggrian.openpss.popup.popover.Popover
import com.hanggrian.openpss.ui.main.help.AboutDialog
import com.hanggrian.openpss.ui.main.help.GitHubApi
import com.jfoenix.controls.JFXButton
import javafx.scene.Node
import javafx.scene.control.Button
import javafx.scene.control.PasswordField
import javafx.scene.control.TextField
import javafx.scene.layout.StackPane
import javafx.scene.text.TextAlignment
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.javafx.JavaFx
import kotlinx.coroutines.launch
import ktfx.bindings.bindingOf
import ktfx.bindings.booleanBindingBy
import ktfx.bindings.or
import ktfx.collections.toObservableList
import ktfx.controls.CENTER
import ktfx.controls.H_RIGHT
import ktfx.controls.RIGHT
import ktfx.controls.insetsOf
import ktfx.coroutines.listener
import ktfx.coroutines.onAction
import ktfx.jfoenix.layouts.jfxComboBox
import ktfx.jfoenix.layouts.jfxPasswordField
import ktfx.jfoenix.layouts.jfxTextField
import ktfx.jfoenix.layouts.jfxToggleButton
import ktfx.jfoenix.layouts.styledJfxButton
import ktfx.layouts.KtfxStackPane
import ktfx.layouts.gridPane
import ktfx.layouts.hbox
import ktfx.layouts.hyperlink
import ktfx.layouts.imageView
import ktfx.layouts.label
import ktfx.layouts.stackPane
import ktfx.layouts.styledLabel
import ktfx.layouts.text
import ktfx.layouts.textFlow
import ktfx.layouts.vbox
import ktfx.runLater
import ktfx.text.pt
import java.util.Properties
import java.util.ResourceBundle

class LoginPane(private val resourced: Resources) :
    KtfxStackPane(),
    Context {
    private var employeeField: TextField
    private var loginButton: Button
    private lateinit var passwordField: PasswordField
    private lateinit var textField: TextField

    var onSuccess: ((Employee) -> Unit)? = null

    private val serverHostField =
        HostField().apply {
            text = LoginFile.DB_HOST
            promptText = getString(R.string_ip_address)
            prefWidth = 128.0
            textProperty().listener { _, _, newValue -> LoginFile.DB_HOST = newValue }
        }
    private val serverPortField =
        IntField().apply {
            value = LoginFile.DB_PORT
            promptText = getString(R.string_port)
            prefWidth = 64.0
            valueProperty.listener { _, _, newValue -> LoginFile.DB_PORT = newValue.toInt() }
        }
    private val serverUserField =
        ktfx.jfoenix.layouts.jfxTextField(LoginFile.DB_USER) {
            promptText = getString(R.string_server_user)
            textProperty().listener { _, _, newValue -> LoginFile.DB_USER = newValue }
        }
    private val serverPasswordField =
        ktfx.jfoenix.layouts.jfxPasswordField {
            text = LoginFile.DB_PASSWORD
            promptText = getString(R.string_server_password)
            textProperty().listener { _, _, newValue -> LoginFile.DB_PASSWORD = newValue }
        }

    init {
        minWidth = WIDTH
        maxWidth = WIDTH
        gridPane {
            alignment = RIGHT
            hgap = getDouble(R.dimen_padding_medium)
            vgap = getDouble(R.dimen_padding_medium)
            padding = insetsOf(getDouble(R.dimen_padding_medium))
            label(getString(R.string_language))
                .grid(row = 0, col = 0)
                .hgrow()
                .halign(H_RIGHT)
            jfxComboBox(Language.entries.toObservableList()) {
                selectionModel.select(PreferencesFile.language)
                valueProperty().listener(Dispatchers.Default) { _, _, value ->
                    PreferencesFile.language = value
                    PreferencesFile.save()
                    GlobalScope.launch(Dispatchers.JavaFx) {
                        runLater {
                            TextDialog(
                                this@LoginPane,
                                R.string_restart_required,
                                getString(R.string__restart_required),
                            ).apply { setOnDialogClosed { OpenPssApp.exit() } }
                                .show(this@LoginPane)
                        }
                    }
                }
            }.grid(row = 0, col = 1)
            vbox(8.0) {
                alignment = CENTER
                padding = insetsOf(32, 24)
                imageView(R.image_logo_small)
                styledLabel(getString(R.string_openpss_login), null, R.style_bold, R.style_display2)
                label(getString(R.string__login_desc1)) {
                    textAlignment = TextAlignment.CENTER
                    isWrapText = true
                    font = 16.pt
                }
                employeeField =
                    jfxTextField(LoginFile.EMPLOYEE) {
                        textProperty().listener { _, _, value -> LoginFile.EMPLOYEE = value }
                        font = 16.pt
                        promptText = getString(R.string_employee)
                        runLater { requestFocus() }
                    }.margin(insetsOf(top = 24))
                textFlow {
                    hyperlink(getString(R.string_connection_settings)) {
                        onAction { ConnectionSettingsPopover().show(this@hyperlink) }
                    }
                }
                textFlow {
                    var version = BuildConfig.VERSION
                    if (BuildConfig.DEBUG) {
                        version += " DEBUG"
                    }
                    text(getString(R.string__login_desc2, version)) {
                        wrappingWidth = employeeField.prefWidth
                    }
                    hyperlink(getString(R.string_check_for_updates)) {
                        onAction { GitHubApi.checkUpdates(this@LoginPane) }
                    }
                }.margin(insetsOf(top = 24))
                vbox {
                    styledJfxButton(getString(R.string_about), null, R.style_flat) {
                        maxWidth = Double.MAX_VALUE
                        padding = insetsOf(8, 16)
                        font = 16.pt
                        onAction { AboutDialog(this@LoginPane).show() }
                    }
                    loginButton =
                        styledJfxButton(getString(R.string_login), null, R.style_raised) {
                            maxWidth = Double.MAX_VALUE
                            padding = insetsOf(8, 16)
                            font = 16.pt
                            buttonType = JFXButton.ButtonType.RAISED
                            disableProperty().bind(
                                employeeField
                                    .textProperty()
                                    .booleanBindingBy { it.isNullOrBlank() } or
                                    !serverHostField.validProperty or
                                    serverPortField
                                        .textProperty()
                                        .booleanBindingBy { it.isNullOrBlank() } or
                                    serverUserField
                                        .textProperty()
                                        .booleanBindingBy { it.isNullOrBlank() } or
                                    serverPasswordField
                                        .textProperty()
                                        .booleanBindingBy { it.isNullOrBlank() },
                            )
                            onAction {
                                PasswordDialog().show {
                                    GlobalScope.launch(Dispatchers.IO) {
                                        LoginFile.save()
                                        try {
                                            val employee =
                                                login(
                                                    serverHostField.text,
                                                    serverPortField.value,
                                                    serverUserField.text,
                                                    serverPasswordField.text,
                                                    employeeField.text,
                                                    passwordField.text,
                                                )
                                            GlobalScope.launch(Dispatchers.JavaFx) {
                                                onSuccess?.invoke(employee)
                                            }
                                        } catch (e: Exception) {
                                            if (BuildConfig.DEBUG) e.printStackTrace()
                                            GlobalScope.launch(Dispatchers.JavaFx) {
                                                TextDialog(
                                                    this@LoginPane,
                                                    R.string_login_failed,
                                                    e.message.toString(),
                                                ).show(this@LoginPane)
                                            }
                                        }
                                    }
                                }
                            }
                            employeeField.onActionProperty().bindBidirectional(onActionProperty())
                        }
                }.margin(insetsOf(top = 24))
            }.grid(row = 1, col = 0 to 2)
        }
    }

    override val resourceBundle: ResourceBundle get() = resourced.resourceBundle
    override val dimenResources: Properties get() = resourced.dimenResources
    override val colorResources: Properties get() = resourced.colorResources

    override val login: Employee get() = throw UnsupportedOperationException()
    override val stack: StackPane get() = this

    private companion object {
        const val WIDTH = 400.0
    }

    inner class ConnectionSettingsPopover : Popover(this, R.string_connection_settings) {
        init {
            gridPane {
                hgap = getDouble(R.dimen_padding_medium)
                vgap = getDouble(R.dimen_padding_medium)
                label(getString(R.string_server_host_port))
                    .grid(0, 0)
                addChild(serverHostField)
                    .grid(0, 1)
                addChild(serverPortField)
                    .grid(0, 2)
                label(getString(R.string_server_user))
                    .grid(1, 0)
                addChild(serverUserField)
                    .grid(1, 1 to 2)
                label(getString(R.string_server_password))
                    .grid(2, 0)
                addChild(serverPasswordField)
                    .grid(2, 1 to 2)
            }
        }

        override val focusedNode: Node get() = serverHostField
    }

    inner class PasswordDialog :
        ResultableDialog<Unit>(this@LoginPane, R.string_password_required) {
        init {
            hbox(getDouble(R.dimen_padding_medium)) {
                stackPane {
                    alignment = CENTER
                    passwordField =
                        jfxPasswordField { promptText = getString(R.string_password) }
                    textField =
                        jfxTextField {
                            promptText = getString(R.string_password)
                            isVisible = false
                            passwordField.textProperty().bindBidirectional(textProperty())
                            if (BuildConfig.DEBUG) {
                                text = Employee.DEFAULT_PASSWORD
                            }
                        }
                }
                jfxToggleButton {
                    text = getString(R.string_view)
                    passwordField.visibleProperty().bind(!selectedProperty())
                    textField.visibleProperty().bind(selectedProperty())
                    selectedProperty().listener {
                        when {
                            passwordField.isVisible -> passwordField.requestFocus()
                            else -> textField.requestFocus()
                        }
                    }
                }
            }
            defaultButton.run {
                disableProperty().bind(
                    textField.textProperty().booleanBindingBy { it.isNullOrBlank() },
                )
                passwordField.onActionProperty().bind(
                    bindingOf(disableProperty()) { if (isDisable) null else onAction },
                )
                textField.onActionProperty().bind(
                    bindingOf(disableProperty()) { if (isDisable) null else onAction },
                )
            }
        }

        override val focusedNode: Node get() = passwordField
    }
}
