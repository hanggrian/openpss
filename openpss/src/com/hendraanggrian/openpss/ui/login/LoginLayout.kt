package com.hendraanggrian.openpss.ui.login

import com.hendraanggrian.openpss.App
import com.hendraanggrian.openpss.BuildConfig
import com.hendraanggrian.openpss.R
import com.hendraanggrian.openpss.control.bold
import com.hendraanggrian.openpss.control.dialog.MaterialAlert
import com.hendraanggrian.openpss.control.dialog.MaterialResultableDialog
import com.hendraanggrian.openpss.control.popover.MaterialPopover
import com.hendraanggrian.openpss.db.login
import com.hendraanggrian.openpss.db.schemas.Employee
import com.hendraanggrian.openpss.i18n.Language
import com.hendraanggrian.openpss.i18n.Resourced
import com.hendraanggrian.openpss.io.properties.LoginFile
import com.hendraanggrian.openpss.io.properties.PreferencesFile
import com.hendraanggrian.openpss.ui.main.help.AboutDialog
import com.hendraanggrian.openpss.ui.main.help.GitHubApi
import com.hendraanggrian.openpss.util.forceExit
import com.jfoenix.controls.JFXButton
import javafx.geometry.HPos
import javafx.geometry.Pos
import javafx.scene.control.Button
import javafx.scene.control.ButtonType
import javafx.scene.control.PasswordField
import javafx.scene.control.TextField
import javafx.scene.image.Image
import javafx.scene.layout.Priority
import javafx.scene.text.TextAlignment
import kotlinx.coroutines.experimental.Dispatchers
import kotlinx.coroutines.experimental.GlobalScope
import kotlinx.coroutines.experimental.javafx.JavaFx
import kotlinx.coroutines.experimental.launch
import ktfx.NodeManager
import ktfx.application.later
import ktfx.beans.value.isBlank
import ktfx.beans.value.or
import ktfx.collections.toObservableList
import ktfx.coroutines.listener
import ktfx.coroutines.onAction
import ktfx.jfoenix.jfxButton
import ktfx.jfoenix.jfxPasswordField
import ktfx.jfoenix.jfxTextField
import ktfx.jfoenix.jfxToggleButton
import ktfx.layouts._StackPane
import ktfx.layouts.anchorPane
import ktfx.layouts.choiceBox
import ktfx.layouts.gridPane
import ktfx.layouts.hbox
import ktfx.layouts.hyperlink
import ktfx.layouts.imageView
import ktfx.layouts.label
import ktfx.layouts.stackPane
import ktfx.layouts.text
import ktfx.layouts.textFlow
import ktfx.layouts.vbox
import ktfx.scene.control.errorAlert
import ktfx.scene.control.infoAlert
import ktfx.scene.control.warningAlert
import ktfx.scene.layout.gap
import ktfx.scene.layout.paddingAll
import ktfx.scene.layout.updatePadding
import ktfx.scene.text.fontSize

class LoginLayout(resourced: Resourced) : _StackPane(), Resourced by resourced {

    private companion object {
        const val WIDTH = 400.0
        const val HEIGHT = 500.0
    }

    private lateinit var employeeField: TextField
    private lateinit var loginButton: Button
    private lateinit var passwordField: PasswordField
    private lateinit var textField: TextField

    private lateinit var onSuccess: (Employee) -> Unit

    private val serverHostField = HostField().apply {
        text = LoginFile.DB_HOST
        promptText = getString(R.string.ip_address)
        prefWidth = 128.0
        textProperty().listener { _, _, newValue -> LoginFile.DB_HOST = newValue }
    }
    private val serverPortField = com.hendraanggrian.openpss.control.intField {
        value = LoginFile.DB_PORT
        promptText = getString(R.string.port)
        prefWidth = 64.0
        valueProperty().listener { _, _, newValue -> LoginFile.DB_PORT = newValue.toInt() }
    }
    private val serverUserField = ktfx.layouts.textField(LoginFile.DB_USER) {
        promptText = getString(R.string.server_user)
        textProperty().listener { _, _, newValue -> LoginFile.DB_USER = newValue }
    }
    private val serverPasswordField = ktfx.layouts.passwordField {
        text = LoginFile.DB_PASSWORD
        promptText = getString(R.string.server_password)
        textProperty().listener { _, _, newValue -> LoginFile.DB_PASSWORD = newValue }
    }

    init {
        setMinSize(
            WIDTH,
            HEIGHT
        )
        setMaxSize(
            WIDTH,
            HEIGHT
        )
        gridPane {
            alignment = Pos.CENTER_RIGHT
            gap = 8.0
            paddingAll = 8.0
            label(getString(R.string.language)) row 0 col 0 hpriority Priority.ALWAYS halign HPos.RIGHT
            choiceBox(Language.values().toObservableList()) {
                selectionModel.select(PreferencesFile.language)
                valueProperty().listener(Dispatchers.Default) { _, _, value ->
                    PreferencesFile.language = value
                    PreferencesFile.save()
                    GlobalScope.launch(Dispatchers.JavaFx) {
                        later {
                            checkNotNull(R.string._restart_required)
                            MaterialAlert(
                                this@LoginLayout,
                                R.string.restart_required,
                                R.string._restart_required
                            ).apply {
                                setOnDialogClosed {
                                    forceExit()
                                }
                            }.show(this@LoginLayout)
                        }
                    }
                }
            } row 0 col 1
            vbox(8.0) {
                alignment = Pos.CENTER
                updatePadding(32.0, 24.0, 32.0, 24.0)
                imageView(Image(R.image.display_launcher)) {
                    fitWidth = 64.0
                    fitHeight = 64.0
                }
                label(getString(R.string.openpss_login)) { font = bold(18.0) }
                label(getString(R.string._login_desc1)) {
                    textAlignment = TextAlignment.CENTER
                    isWrapText = true
                    fontSize = 16.0
                }
                employeeField = jfxTextField(LoginFile.EMPLOYEE) {
                    fontSize = 16.0
                    promptText = getString(R.string.employee)
                    later { requestFocus() }
                } marginTop 24.0
                textFlow {
                    hyperlink(getString(R.string.connection_settings)) {
                        onAction {
                            ConnectionSettingsPopover().showAt(this@hyperlink)
                        }
                    }
                }
                textFlow {
                    text(getString(R.string._login_desc2, BuildConfig.VERSION)) {
                        wrappingWidth = employeeField.prefWidth
                    }
                    hyperlink(getString(R.string.check_for_updates)) {
                        onAction {
                            GitHubApi.checkUpdates(this@LoginLayout, { title, actions ->
                                warningAlert(title, ButtonType.CANCEL) {
                                    dialogPane.stylesheets += com.hendraanggrian.openpss.util.getStyle(R.style.openpss)
                                    dialogPane.content = ktfx.layouts.vbox {
                                        actions.forEach { action ->
                                            hyperlink(action.text) { onAction = action }
                                        }
                                    }
                                }.show()
                            }) { title, content ->
                                infoAlert(title, content = content) {
                                    dialogPane.stylesheets += com.hendraanggrian.openpss.util.getStyle(R.style.openpss)
                                }.show()
                            }
                        }
                    }
                } marginTop 24.0
                anchorPane {
                    jfxButton(getString(R.string.about)) {
                        updatePadding(8.0, 16.0, 8.0, 16.0)
                        fontSize = 16.0
                        styleClass += App.STYLE_BUTTON_FLAT
                        onAction { AboutDialog(this@LoginLayout).show() }
                    } anchorLeft 0.0
                    loginButton = jfxButton(getString(R.string.login)) {
                        updatePadding(8.0, 16.0, 8.0, 16.0)
                        fontSize = 16.0
                        styleClass += App.STYLE_BUTTON_RAISED
                        buttonType = JFXButton.ButtonType.RAISED
                        disableProperty().bind(
                            employeeField.textProperty().isBlank()
                                or !serverHostField.validProperty()
                                or serverPortField.textProperty().isBlank()
                                or serverUserField.textProperty().isBlank()
                                or serverPasswordField.textProperty().isBlank()
                        )
                        onAction {
                            PasswordDialog().show(this@LoginLayout) { _ ->
                                GlobalScope.launch(Dispatchers.IO) {
                                    LoginFile.save()
                                    try {
                                        val employee = login(
                                            serverHostField.text,
                                            serverPortField.value,
                                            serverUserField.text,
                                            serverPasswordField.text,
                                            employeeField.text,
                                            passwordField.text
                                        )
                                        GlobalScope.launch(Dispatchers.JavaFx) {
                                            onSuccess(employee)
                                        }
                                    } catch (e: Exception) {
                                        if (BuildConfig.DEBUG) e.printStackTrace()
                                        GlobalScope.launch(Dispatchers.JavaFx) {
                                            errorAlert(e.message.toString()) {
                                                dialogPane.stylesheets += com.hendraanggrian.openpss.util.getStyle(R.style.openpss)
                                            }.show()
                                        }
                                    }
                                }
                            }
                        }
                        employeeField.onActionProperty().bindBidirectional(onActionProperty())
                    } anchorRight 0.0
                } marginTop 24.0
            } row 1 col 0 colSpans 2
        }
    }

    fun setOnSuccess(onSuccess: (Employee) -> Unit) {
        this.onSuccess = onSuccess
    }

    inner class ConnectionSettingsPopover : MaterialPopover(this, R.string.connection_settings) {

        override fun NodeManager.onCreate() {
            gridPane {
                paddingAll = 16.0
                gap = R.dimen.padding_medium.toDouble()
                label(getString(R.string.server_host_port)) col 0 row 0
                serverHostField() col 1 row 0
                serverPortField() col 2 row 0
                label(getString(R.string.server_user)) col 0 row 1
                serverUserField() col 1 row 1 colSpans 2
                label(getString(R.string.server_password)) col 0 row 2
                serverPasswordField() col 1 row 2 colSpans 2
            }
        }
    }

    inner class PasswordDialog : MaterialResultableDialog<Unit>(this, R.string.password_required) {

        override fun NodeManager.onCreate() {
            setOnDialogOpened {
                passwordField.requestFocus()
            }
            setOnDialogClosed {
                employeeField.requestFocus()
            }
            hbox(R.dimen.padding_medium.toDouble()) {
                stackPane {
                    alignment = Pos.CENTER
                    passwordField = jfxPasswordField {
                        promptText = getString(R.string.password)
                    }
                    textField = jfxTextField {
                        promptText = getString(R.string.password)
                        isVisible = false
                        passwordField.textProperty().bindBidirectional(textProperty())
                    }
                }
                jfxToggleButton {
                    text = getString(R.string.view)
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
        }

        override fun NodeManager.onCreateActions() {
            defaultButton = jfxButton(getString(R.string.login)) {
                styleClass += App.STYLE_BUTTON_RAISED
                buttonType = JFXButton.ButtonType.RAISED
                disableProperty().bind(textField.textProperty().isBlank())
                passwordField.onActionProperty().bindBidirectional(onActionProperty())
            }
        }
    }
}