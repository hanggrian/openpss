package com.hendraanggrian.openpss.ui.login

import com.hendraanggrian.openpss.BuildConfig
import com.hendraanggrian.openpss.OpenPSSApplication
import com.hendraanggrian.openpss.R
import com.hendraanggrian.openpss.content.Language
import com.hendraanggrian.openpss.data.Employee
import com.hendraanggrian.openpss.io.SettingsFile
import com.hendraanggrian.openpss.ui.FxComponent
import com.hendraanggrian.openpss.ui.Resources
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
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.javafx.JavaFx
import kotlinx.coroutines.launch
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
import ktfx.later
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
import ktfx.text.updateFont
import java.util.Properties
import java.util.ResourceBundle

class LoginPane(private val resourced: Resources) : _StackPane(),
    FxComponent {

    private companion object {
        const val WIDTH = 400.0
    }

    private lateinit var employeeField: TextField
    private lateinit var loginButton: Button
    private lateinit var passwordField: PasswordField
    private lateinit var textField: TextField

    var onSuccess: ((Employee) -> Unit)? = null

    override val resourceBundle: ResourceBundle get() = resourced.resourceBundle
    override val valueProperties: Properties get() = resourced.valueProperties

    override val login: Employee get() = throw UnsupportedOperationException()
    override val rootLayout: StackPane get() = this

    init {
        minWidth = WIDTH
        maxWidth = WIDTH
        gridPane {
            alignment = Pos.CENTER_RIGHT
            gap = getDouble(R.value.padding_medium)
            paddingAll = getDouble(R.value.padding_medium)
            label(getString(R.string.language)) row 0 col 0 hpriority Priority.ALWAYS halign HPos.RIGHT
            jfxComboBox(Language.values().toObservableList()) {
                selectionModel.select(SettingsFile.language)
                valueProperty().listener(Dispatchers.Default) { _, _, value ->
                    SettingsFile.language = value
                    SettingsFile.save()
                    GlobalScope.launch(Dispatchers.JavaFx) {
                        later {
                            TextDialog(
                                this@LoginPane,
                                R.string.restart_required,
                                getString(R.string._restart_required)
                            )
                                .apply { onDialogClosed { OpenPSSApplication.exit() } }
                                .show(this@LoginPane)
                        }
                    }
                }
            } row 0 col 1
            vbox(8.0) {
                alignment = Pos.CENTER
                updatePadding(32, 24, 32, 24)
                imageView(R.image.logo_small)
                label(getString(R.string.openpss_login)) {
                    styleClass.addAll(R.style.bold, R.style.display2)
                }
                label(getString(R.string._login_desc1)) {
                    textAlignment = TextAlignment.CENTER
                    isWrapText = true
                    updateFont(16)
                }
                employeeField = jfxTextField(SettingsFile.EMPLOYEE) {
                    textProperty().listener { _, _, value -> SettingsFile.EMPLOYEE = value }
                    updateFont(16)
                    promptText = getString(R.string.employee)
                    later { requestFocus() }
                } marginTop 24
                textFlow {
                    var version = BuildConfig.VERSION
                    if (BuildConfig.DEBUG) {
                        version += " DEBUG"
                    }
                    text(getString(R.string._login_desc2, version)) {
                        wrappingWidth = employeeField.prefWidth
                    }
                    hyperlink(getString(R.string.check_for_updates)) {
                        onAction {
                            GitHubHelper.checkUpdates(this@LoginPane)
                        }
                    }
                } marginTop 24
                anchorPane {
                    jfxButton(getString(R.string.about)) {
                        updatePadding(8, 16, 8, 16)
                        updateFont(16)
                        styleClass += R.style.flat
                        onAction { AboutDialog(this@LoginPane).show() }
                    } anchorLeft 0
                    loginButton = jfxButton(getString(R.string.login)) {
                        updatePadding(8, 16, 8, 16)
                        updateFont(16)
                        styleClass += R.style.raised
                        buttonType = JFXButton.ButtonType.RAISED
                        disableProperty().bind(employeeField.textProperty().isBlank())
                        onAction {
                            PasswordDialog().show {
                                SettingsFile.save()
                                GlobalScope.launch(Dispatchers.JavaFx) {
                                    runCatching {
                                        val employee = api.login(employeeField.text, passwordField.text)
                                        onSuccess?.invoke(employee)
                                    }.onFailure {
                                        if (BuildConfig.DEBUG) it.printStackTrace()
                                        TextDialog(
                                            this@LoginPane,
                                            R.string.login_failed,
                                            it.message.toString()
                                        )
                                            .show(this@LoginPane)
                                    }
                                }
                            }
                        }
                        employeeField.onActionProperty().bindBidirectional(onActionProperty())
                    } anchorRight 0
                } marginTop 24
            } row 1 col 0 colSpans 2
        }
    }

    inner class PasswordDialog : ResultableDialog<Unit>(this@LoginPane, R.string.password_required) {

        override val focusedNode: Node? get() = passwordField

        init {
            hbox(getDouble(R.value.padding_medium)) {
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
                    if (BuildConfig.DEBUG) {
                        passwordField.text = Employee.DEFAULT_PASSWORD
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