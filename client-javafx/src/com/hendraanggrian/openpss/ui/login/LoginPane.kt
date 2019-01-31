package com.hendraanggrian.openpss.ui.login

import com.hendraanggrian.defaults.WritableDefaults
import com.hendraanggrian.openpss.BuildConfig2
import com.hendraanggrian.openpss.FxComponent
import com.hendraanggrian.openpss.Language
import com.hendraanggrian.openpss.OpenPssApplication
import com.hendraanggrian.openpss.R
import com.hendraanggrian.openpss.R2
import com.hendraanggrian.openpss.Setting
import com.hendraanggrian.openpss.StringResources
import com.hendraanggrian.openpss.ValueResources
import com.hendraanggrian.openpss.control.IntField
import com.hendraanggrian.openpss.data.Employee
import com.hendraanggrian.openpss.language
import com.hendraanggrian.openpss.ui.BasePopOver
import com.hendraanggrian.openpss.ui.ResultableDialog
import com.hendraanggrian.openpss.ui.TextDialog
import com.hendraanggrian.openpss.ui.main.help.AboutDialog
import com.hendraanggrian.openpss.ui.main.help.GitHubHelper
import com.jfoenix.controls.JFXButton
import javafx.geometry.HPos
import javafx.geometry.Pos
import javafx.scene.Node
import javafx.scene.control.Button
import javafx.scene.control.PasswordField
import javafx.scene.control.TextField
import javafx.scene.layout.Priority
import javafx.scene.layout.StackPane
import javafx.scene.text.TextAlignment
import ktfx.bindings.buildBinding
import ktfx.bindings.isBlank
import ktfx.collections.toObservableList
import ktfx.controls.gap
import ktfx.controls.paddingAll
import ktfx.controls.updatePadding
import ktfx.coroutines.listener
import ktfx.coroutines.onAction
import ktfx.jfoenix.jfxButton
import ktfx.jfoenix.jfxComboBox
import ktfx.jfoenix.jfxPasswordField
import ktfx.jfoenix.jfxTextField
import ktfx.jfoenix.jfxToggleButton
import ktfx.jfoenix.onDialogClosed
import ktfx.layouts._StackPane
import ktfx.layouts.anchorPane
import ktfx.layouts.gridPane
import ktfx.layouts.hbox
import ktfx.layouts.hyperlink
import ktfx.layouts.imageView
import ktfx.layouts.label
import ktfx.layouts.stackPane
import ktfx.layouts.text
import ktfx.layouts.textFlow
import ktfx.layouts.vbox
import ktfx.runLater
import ktfx.text.updateFont

class LoginPane<T>(resources: T, override val defaults: WritableDefaults) : _StackPane(),
    FxComponent,
    StringResources by resources,
    ValueResources by resources
    where T : StringResources, T : ValueResources {

    private companion object {
        const val WIDTH = 400.0
    }

    private lateinit var employeeField: TextField
    private lateinit var loginButton: Button
    private lateinit var passwordField: PasswordField
    private lateinit var textField: TextField

    var onSuccess: ((Employee) -> Unit)? = null

    override val login: Employee get() = throw UnsupportedOperationException()
    override val rootLayout: StackPane get() = this

    private val serverHostField = HostField().apply {
        text = defaults[Setting.KEY_SERVER_HOST]
        promptText = getString(R2.string.server_host)
    }
    private val serverPortField = IntField().apply {
        value = defaults.getInt(Setting.KEY_SERVER_PORT)
        promptText = getString(R2.string.server_port)
    }

    init {
        minWidth = WIDTH
        maxWidth = WIDTH
        gridPane {
            alignment = Pos.CENTER_RIGHT
            gap = getDouble(R.value.padding_medium)
            paddingAll = getDouble(R.value.padding_medium)
            label(getString(R2.string.language)) row 0 col 0 hpriority Priority.ALWAYS halign HPos.RIGHT
            jfxComboBox(Language.values().toObservableList()) {
                selectionModel.select(defaults.language)
                valueProperty().listener { _, _, value ->
                    defaults {
                        this[Setting.KEY_LANGUAGE] = value.fullCode
                    }
                    TextDialog(
                        this@LoginPane,
                        R2.string.restart_required,
                        getString(R2.string._restart_required)
                    ).apply {
                        onDialogClosed { OpenPssApplication.exit() }
                    }.show(this@LoginPane)
                }
            } row 0 col 1
            vbox(8.0) {
                alignment = Pos.CENTER
                updatePadding(32, 24, 32, 24)
                imageView(R.image.logo_small)
                label(getString(R2.string.openpss_login)) {
                    styleClass.addAll(R.style.bold, R.style.display2)
                }
                label(getString(R2.string._login_desc1)) {
                    textAlignment = TextAlignment.CENTER
                    isWrapText = true
                    updateFont(16)
                }
                employeeField = jfxTextField(defaults[Setting.KEY_EMPLOYEE]) {
                    updateFont(16)
                    promptText = getString(R2.string.employee)
                    runLater(::requestFocus)
                } marginTop 24
                textFlow {
                    hyperlink(getString(R2.string.connection_settings)) {
                        onAction {
                            ConnectionSettingsPopover().show(this@hyperlink)
                        }
                    }
                }
                textFlow {
                    var version = BuildConfig2.VERSION
                    if (BuildConfig2.DEBUG) {
                        version += " DEBUG"
                    }
                    text(getString(R2.string._login_desc2, version)) {
                        wrappingWidth = employeeField.prefWidth
                    }
                    hyperlink(getString(R2.string.check_for_updates)) {
                        onAction {
                            GitHubHelper.checkUpdates(this@LoginPane)
                        }
                    }
                } marginTop 24
                anchorPane {
                    jfxButton(getString(R2.string.about)) {
                        updatePadding(8, 16, 8, 16)
                        updateFont(16)
                        styleClass += R.style.flat
                        onAction { AboutDialog(this@LoginPane).show() }
                    } anchorLeft 0
                    loginButton = jfxButton(getString(R2.string.login)) {
                        updatePadding(8, 16, 8, 16)
                        updateFont(16)
                        styleClass += R.style.raised
                        buttonType = JFXButton.ButtonType.RAISED
                        disableProperty().bind(employeeField.textProperty().isBlank())
                        onAction {
                            PasswordDialog().show {
                                defaults {
                                    this[Setting.KEY_EMPLOYEE] = employeeField.text
                                    this[Setting.KEY_SERVER_HOST] = serverHostField.text
                                    this[Setting.KEY_SERVER_PORT] = serverPortField.text
                                }
                                onSuccess?.invoke(runCatching {
                                    api.login(employeeField.text, passwordField.text)
                                }.onFailure {
                                    if (BuildConfig2.DEBUG) it.printStackTrace()
                                    TextDialog(
                                        this@LoginPane,
                                        R2.string.login_failed,
                                        it.message.toString()
                                    ).show(this@LoginPane)
                                }.getOrThrow())
                            }
                        }
                        employeeField.onActionProperty().bindBidirectional(onActionProperty())
                    } anchorRight 0
                } marginTop 24
            } row 1 col 0 colSpans 2
        }
    }

    inner class ConnectionSettingsPopover : BasePopOver(this, R2.string.connection_settings) {

        override val focusedNode: Node? get() = serverHostField

        init {
            gridPane {
                gap = getDouble(R.value.padding_medium)
                label(getString(R2.string.server_host)) col 0 row 0
                serverHostField() col 1 row 0
                label(getString(R2.string.server_port)) col 0 row 1
                serverPortField() col 1 row 1
            }
        }
    }

    inner class PasswordDialog :
        ResultableDialog<Unit>(this@LoginPane, R2.string.password_required) {

        override val focusedNode: Node? get() = passwordField

        init {
            hbox(getDouble(R.value.padding_medium)) {
                stackPane {
                    alignment = Pos.CENTER
                    passwordField = jfxPasswordField {
                        promptText = getString(R2.string.password)
                    }
                    textField = jfxTextField {
                        promptText = getString(R2.string.password)
                        isVisible = false
                        passwordField.textProperty().bindBidirectional(textProperty())
                    }
                    if (BuildConfig2.DEBUG) {
                        passwordField.text = Employee.DEFAULT_PASSWORD
                    }
                }
                jfxToggleButton {
                    text = getString(R2.string.view)
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
                disableProperty().bind(textField.textProperty().isBlank())
                passwordField.onActionProperty().bind(buildBinding(disableProperty()) {
                    if (isDisable) null else onAction
                })
                textField.onActionProperty().bind(buildBinding(disableProperty()) {
                    if (isDisable) null else onAction
                })
            }
        }
    }
}