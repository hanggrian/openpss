package com.hendraanggrian.openpss.ui.login

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
import com.hendraanggrian.prefs.jvm.PropertiesPrefs
import com.jfoenix.controls.JFXButton
import javafx.geometry.HPos
import javafx.geometry.Pos
import javafx.scene.Node
import javafx.scene.control.Button
import javafx.scene.control.PasswordField
import javafx.scene.control.TextField
import javafx.scene.layout.StackPane
import javafx.scene.text.TextAlignment
import ktfx.collections.toObservableList
import ktfx.controls.gap
import ktfx.controls.horizontalPadding
import ktfx.controls.paddings
import ktfx.controls.verticalPadding
import ktfx.coroutines.listener
import ktfx.coroutines.onAction
import ktfx.jfoenix.layouts.jfxButton
import ktfx.jfoenix.layouts.jfxComboBox
import ktfx.jfoenix.layouts.jfxPasswordField
import ktfx.jfoenix.layouts.jfxTextField
import ktfx.jfoenix.layouts.jfxToggleButton
import ktfx.jfoenix.listeners.onDialogClosed
import ktfx.layouts.KtfxStackPane
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
import ktfx.text.pt
import ktfx.toBinding
import ktfx.toBooleanBinding

class LoginPane<T>(resources: T, override val prefs: PropertiesPrefs) : KtfxStackPane(),
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
        text = prefs[Setting.KEY_SERVER_HOST]
        promptText = getString(R2.string.server_host)
    }
    private val serverPortField = IntField().apply {
        value = prefs.getInt(Setting.KEY_SERVER_PORT)!!
        promptText = getString(R2.string.server_port)
    }

    init {
        minWidth = WIDTH
        maxWidth = WIDTH
        gridPane {
            alignment = Pos.CENTER_RIGHT
            gap = getDouble(R.value.padding_medium)
            paddings = getDouble(R.value.padding_medium)
            label(getString(R2.string.language)) row 0 col 0 hgrow true halign HPos.RIGHT
            jfxComboBox(Language.values().toObservableList()) {
                selectionModel.select(prefs.language)
                valueProperty().listener { _, _, value ->
                    prefs[Setting.KEY_LANGUAGE] = value.fullCode
                    prefs.save()
                    TextDialog(
                        this@LoginPane,
                        R2.string.restart_required,
                        getString(R2.string._restart_required)
                    ).apply {
                        onDialogClosed { App.exit() }
                    }.show(this@LoginPane)
                }
            } row 0 col 1
            vbox(8.0) {
                alignment = Pos.CENTER
                verticalPadding = 32.0
                horizontalPadding = 24.0
                imageView(R.image.logo_small)
                label(getString(R2.string.openpss_login)) {
                    styleClass.addAll(R.style.bold, R.style.display2)
                }
                label(getString(R2.string._login_desc1)) {
                    font = 16.pt
                    textAlignment = TextAlignment.CENTER
                    isWrapText = true
                }
                employeeField = jfxTextField(prefs[Setting.KEY_EMPLOYEE]) {
                    font = 16.pt
                    promptText = getString(R2.string.employee)
                    runLater(::requestFocus)
                } topMargin 24.0
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
                } topMargin 24.0
                anchorPane {
                    jfxButton(getString(R2.string.about)) {
                        verticalPadding = 8.0
                        horizontalPadding = 16.0
                        font = 16.pt
                        styleClass += R.style.flat
                        onAction { AboutDialog(this@LoginPane).show() }
                    } leftAnchor 0.0
                    loginButton = jfxButton(getString(R2.string.login)) {
                        verticalPadding = 8.0
                        horizontalPadding = 16.0
                        font = 16.pt
                        styleClass += R.style.raised
                        buttonType = JFXButton.ButtonType.RAISED
                        disableProperty().bind(employeeField.textProperty().toBooleanBinding { it!!.isBlank() })
                        onAction {
                            PasswordDialog().show {
                                prefs[Setting.KEY_EMPLOYEE] = employeeField.text
                                prefs[Setting.KEY_SERVER_HOST] = serverHostField.text
                                prefs[Setting.KEY_SERVER_PORT] = serverPortField.text
                                prefs.save()
                                OpenPSSApi.init(
                                    prefs[Setting.KEY_SERVER_HOST],
                                    prefs.getInt(Setting.KEY_SERVER_PORT)
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
                    } rightAnchor 0.0
                } topMargin 24.0
            } row 1 col (0 to 2)
        }
    }

    inner class ConnectionSettingsPopover : BasePopOver(this, R2.string.connection_settings) {
        override val focusedNode: Node? get() = serverHostField

        init {
            gridPane {
                gap = getDouble(R.value.padding_medium)
                label(getString(R2.string.server_host)) col 0 row 0
                addChild(serverHostField) col 1 row 0
                label(getString(R2.string.server_port)) col 0 row 1
                addChild(serverPortField) col 1 row 1
            }
        }
    }

    inner class PasswordDialog : ResultableDialog<Unit>(this@LoginPane, R2.string.password_required) {
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
                disableProperty().bind(textField.textProperty().toBooleanBinding { it!!.isBlank() })
                passwordField.onActionProperty().bind(disableProperty().toBinding { if (it) null else onAction })
                textField.onActionProperty().bind(disableProperty().toBinding { if (it) null else onAction })
            }
        }
    }
}
