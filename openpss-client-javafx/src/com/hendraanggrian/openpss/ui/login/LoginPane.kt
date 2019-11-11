package com.hendraanggrian.openpss.ui.login

import com.hendraanggrian.defaults.WritableDefaults
import com.hendraanggrian.openpss.App
import com.hendraanggrian.openpss.BuildConfig2
import com.hendraanggrian.openpss.FxComponent
import com.hendraanggrian.openpss.Language
import com.hendraanggrian.openpss.R
import com.hendraanggrian.openpss.R2
import com.hendraanggrian.openpss.Setting
import com.hendraanggrian.openpss.StringResources
import com.hendraanggrian.openpss.ValueResources
import com.hendraanggrian.openpss.api.OpenPSSApi
import com.hendraanggrian.openpss.control.IntField
import com.hendraanggrian.openpss.language
import com.hendraanggrian.openpss.schema.Employee
import com.hendraanggrian.openpss.ui.BasePopOver
import com.hendraanggrian.openpss.ui.ResultableDialog
import com.hendraanggrian.openpss.ui.TextDialog
import com.hendraanggrian.openpss.ui.main.help.AboutDialog
import com.hendraanggrian.openpss.ui.main.help.GitHubHelper
import com.jfoenix.controls.JFXButton
import javafx.geometry.Pos
import javafx.scene.Node
import javafx.scene.control.Button
import javafx.scene.control.PasswordField
import javafx.scene.control.TextField
import javafx.scene.layout.StackPane
import javafx.scene.text.TextAlignment
import ktfx.bindings.bindingOf
import ktfx.bindings.isBlank
import ktfx.collections.toObservableList
import ktfx.coroutines.listener
import ktfx.coroutines.onAction
import ktfx.jfoenix.layouts.jfxButton
import ktfx.jfoenix.layouts.jfxComboBox
import ktfx.jfoenix.layouts.jfxPasswordField
import ktfx.jfoenix.layouts.jfxTextField
import ktfx.jfoenix.layouts.jfxToggleButton
import ktfx.jfoenix.listeners.onDialogClosed
import ktfx.layouts.KtfxStackPane
import ktfx.layouts.addNode
import ktfx.layouts.anchorPane
import ktfx.layouts.gap
import ktfx.layouts.gridPane
import ktfx.layouts.hbox
import ktfx.layouts.hyperlink
import ktfx.layouts.imageView
import ktfx.layouts.label
import ktfx.layouts.paddingAll
import ktfx.layouts.stackPane
import ktfx.layouts.text
import ktfx.layouts.textFlow
import ktfx.layouts.updatePadding
import ktfx.layouts.vbox
import ktfx.runLater
import ktfx.text.pt

class LoginPane<T>(resources: T, override val defaults: WritableDefaults) : KtfxStackPane(),
    FxComponent,
    StringResources by resources,
    ValueResources by resources
    where T : StringResources, T : ValueResources {

    private companion object {
        const val WIDTH = 400.0
    }

    private val employeeField: TextField
    private val loginButton: Button
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
        value = defaults.getInt(Setting.KEY_SERVER_PORT)!!
        promptText = getString(R2.string.server_port)
    }

    init {
        minWidth = WIDTH
        maxWidth = WIDTH
        gridPane {
            alignment = Pos.CENTER_RIGHT
            gap = getDouble(R.value.padding_medium)
            paddingAll = getDouble(R.value.padding_medium)
            label(getString(R2.string.language)) {
                gridAt(0, 0)
                hgrow()
                halignRight()
            }
            jfxComboBox(Language.values().toObservableList()) {
                gridAt(0, 1)
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
                        onDialogClosed { App.exit() }
                    }.show(this@LoginPane)
                }
            }
            vbox(8.0) {
                gridAt(1, 0)
                colSpans = 2
                alignment = Pos.CENTER
                updatePadding(32.0, 24.0, 32.0, 24.0)
                imageView(R.image.logo_small)
                label(getString(R2.string.openpss_login)) {
                    styleClass.addAll(R.style.bold, R.style.display2)
                }
                label(getString(R2.string._login_desc1)) {
                    font = 16.pt
                    textAlignment = TextAlignment.CENTER
                    isWrapText = true
                }
                employeeField = jfxTextField(defaults[Setting.KEY_EMPLOYEE]) {
                    font = 16.pt
                    marginTop = 24.0
                    promptText = getString(R2.string.employee)
                    runLater(::requestFocus)
                }
                textFlow {
                    hyperlink(getString(R2.string.connection_settings)) {
                        onAction {
                            ConnectionSettingsPopover().show(this@hyperlink)
                        }
                    }
                }
                textFlow {
                    marginTop = 24.0
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
                }
                anchorPane {
                    marginTop = 24.0
                    jfxButton(getString(R2.string.about)) {
                        anchorLeft = 0.0
                        updatePadding(8.0, 16.0, 8.0, 16.0)
                        font = 16.pt
                        styleClass += R.style.flat
                        onAction { AboutDialog(this@LoginPane).show() }
                    }
                    loginButton = jfxButton(getString(R2.string.login)) {
                        anchorRight = 0.0
                        updatePadding(8.0, 16.0, 8.0, 16.0)
                        font = 16.pt
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
                                OpenPSSApi.init(
                                    defaults[Setting.KEY_SERVER_HOST],
                                    defaults.getInt(Setting.KEY_SERVER_PORT)
                                )
                                onSuccess?.invoke(runCatching {
                                    OpenPSSApi.login(employeeField.text, passwordField.text)
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
                    }
                }
            }
        }
    }

    inner class ConnectionSettingsPopover : BasePopOver(this, R2.string.connection_settings) {

        override val focusedNode: Node? get() = serverHostField

        init {
            gridPane {
                gap = getDouble(R.value.padding_medium)
                label(getString(R2.string.server_host)) {
                    gridAt(0, 0)
                }
                addNode(serverHostField) {
                    gridAt(0, 1)
                }
                label(getString(R2.string.server_port)) {
                    gridAt(1, 0)
                }
                addNode(serverPortField) {
                    gridAt(1, 1)
                }
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
                passwordField.onActionProperty().bind(bindingOf(disableProperty()) {
                    if (isDisable) null else onAction
                })
                textField.onActionProperty().bind(bindingOf(disableProperty()) {
                    if (isDisable) null else onAction
                })
            }
        }
    }
}
