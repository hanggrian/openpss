package com.wijayaprinting

import com.wijayaprinting.BuildConfig.DEBUG
import com.wijayaprinting.controls.HostField
import com.wijayaprinting.controls.IntField
import com.wijayaprinting.controls.hostField
import com.wijayaprinting.controls.intField
import com.wijayaprinting.core.Resourced
import com.wijayaprinting.data.Language
import com.wijayaprinting.dialogs.AboutDialog
import com.wijayaprinting.io.NoSQLFile
import com.wijayaprinting.io.PreferencesFile
import com.wijayaprinting.nosql.Employee
import com.wijayaprinting.nosql.Employees
import com.wijayaprinting.nosql.NoSQL
import com.wijayaprinting.nosql.transaction
import com.wijayaprinting.util.gap
import com.wijayaprinting.util.multithread
import com.wijayaprinting.util.pane
import io.reactivex.rxkotlin.subscribeBy
import javafx.application.Application
import javafx.application.Platform.exit
import javafx.event.ActionEvent.ACTION
import javafx.geometry.Pos.CENTER_RIGHT
import javafx.scene.control.ButtonBar.ButtonData.OK_DONE
import javafx.scene.control.ButtonType.CANCEL
import javafx.scene.control.ButtonType.OK
import javafx.scene.control.PasswordField
import javafx.scene.control.TextField
import javafx.scene.image.Image
import javafx.scene.image.ImageView
import javafx.stage.Stage
import kotfx.*
import kotlinx.nosql.equal
import kotlinx.nosql.update
import org.apache.commons.lang3.SystemUtils.IS_OS_MAC_OSX
import org.apache.log4j.BasicConfigurator.configure
import java.awt.Toolkit.getDefaultToolkit
import java.net.URL
import java.util.*

class App : Application(), Resourced {

    companion object {
        lateinit var EMPLOYEE: String
        var FULL_ACCESS: Boolean = false

        @JvmStatic fun main(args: Array<String>) = launch(App::class.java, *args)
    }

    override val resources: ResourceBundle = Language.valueOf(PreferencesFile.language.get()).getResources("string")

    override fun init() {
        if (DEBUG) configure()
    }

    override fun start(stage: Stage) {
        stage.icon = Image(R.png.logo_launcher)
        setOSXIcon(getResource(R.png.logo_launcher))
        dialog<Any>(getString(R.string.app_name)) {
            lateinit var employeeField: TextField
            lateinit var passwordField: PasswordField
            lateinit var serverHostField: HostField
            lateinit var serverPortField: IntField
            lateinit var serverUserField: TextField
            lateinit var serverPasswordField: PasswordField
            headerText = getString(R.string.login)
            graphic = ImageView(R.png.ic_launcher)
            isResizable = false
            content = gridPane {
                gap(8)
                label(getString(R.string.language)) col 0 row 0
                choiceBox(Language.listAll()) {
                    maxWidth = Double.MAX_VALUE
                    selectionModel.select(Language.valueOf(PreferencesFile.language.get()))
                    selectionModel.selectedItemProperty().addListener { _, _, language ->
                        PreferencesFile.language.set(language.locale)
                        PreferencesFile.save()
                        close()
                        infoAlert(getString(R.string.language_changed)).showAndWait().ifPresent { exit() }
                    }
                } col 1 row 0 colSpan 2
                label(getString(R.string.employee)) col 0 row 1
                employeeField = textField {
                    promptText = getString(R.string.employee)
                    textProperty() bindBidirectional PreferencesFile.employee
                } col 1 row 1 colSpan 2
                label(getString(R.string.password)) col 0 row 2
                passwordField = passwordField { promptText = getString(R.string.password) } col 1 row 2
                toggleButton {
                    tooltip = kotfx.tooltip(getString(R.string.see_password))
                    graphic = kotfx.imageView { imageProperty() bind (`if`(this@toggleButton.selectedProperty()) then Image(R.png.btn_visibility) `else` Image(R.png.btn_visibility_off)) }
                    passwordField.tooltipProperty() bind bindingOf(passwordField.textProperty(), selectedProperty()) { if (!isSelected) null else tooltip(passwordField.text) }
                } col 2 row 2
            }
            expandableContent = gridPane {
                gap(8)
                label(getString(R.string.server_host_port)) col 0 row 0
                serverHostField = hostField {
                    promptText = getString(R.string.ip_address)
                    prefWidth = 128.0
                    textProperty() bindBidirectional NoSQLFile.host
                } col 1 row 0
                serverPortField = intField {
                    promptText = getString(R.string.port)
                    prefWidth = 64.0
                    textProperty() bindBidirectional NoSQLFile.port
                } col 2 row 0
                label(getString(R.string.server_user)) col 0 row 1
                serverUserField = textField {
                    promptText = getString(R.string.server_user)
                    textProperty() bindBidirectional NoSQLFile.user
                } col 1 row 1 colSpan 2
                label(getString(R.string.server_password)) col 0 row 2
                serverPasswordField = passwordField {
                    promptText = getString(R.string.server_password)
                    textProperty() bindBidirectional NoSQLFile.password
                } col 1 row 2 colSpan 2
                hbox {
                    alignment = CENTER_RIGHT
                    hyperlink(getString(R.string.test_connection)) {
                        setOnAction {
                            NoSQL.testConnection(serverHostField.text, serverPortField.value, serverUserField.text, serverPasswordField.text)
                                    .multithread()
                                    .subscribeBy({
                                        if (DEBUG) it.printStackTrace()
                                        errorAlert(it.message.toString()).showAndWait()
                                    }) { infoAlert(getString(R.string.test_connection_successful)).showAndWait() }
                        }
                    }
                    hyperlink(getString(R.string.about)) {
                        setOnAction { AboutDialog(this@App).showAndWait() }
                    } marginLeft 8
                } col 0 row 3 colSpan 3
            }
            button(CANCEL)
            button(getString(R.string.login), OK_DONE).apply {
                disableProperty() bind (employeeField.textProperty().isEmpty
                        or passwordField.textProperty().isEmpty
                        or not(serverHostField.validProperty)
                        or serverPortField.textProperty().isEmpty
                        or serverUserField.textProperty().isEmpty
                        or serverPasswordField.textProperty().isEmpty)
                addEventFilter(ACTION) {
                    it.consume()
                    PreferencesFile.save()
                    NoSQLFile.save()
                    NoSQL.login(serverHostField.text, serverPortField.value, serverUserField.text, serverPasswordField.text, employeeField.text, passwordField.text)
                            .multithread()
                            .subscribeBy({
                                if (DEBUG) it.printStackTrace()
                                errorAlert(it.message.toString()).showAndWait()
                            }) { employee ->
                                result = employee
                                close()
                            }
                }
            }
            runFX {
                if (employeeField.text.isBlank()) employeeField.requestFocus() else passwordField.requestFocus()
                isExpanded = listOf(serverHostField, serverPortField, serverUserField, serverPasswordField).any { it.text.isBlank() }
                if (DEBUG) {
                    passwordField.text = "123"
                }
            }
        }.showAndWait().filter { it is Employee }.ifPresent { employee ->
            employee as Employee
            EMPLOYEE = employee.name
            FULL_ACCESS = employee.fullAccess

            stage.apply {
                title = getString(R.string.app_name)
                scene = getResource(R.fxml.layout_main).loadFXML(resources).pane.toScene()
                minWidth = 960.0
                minHeight = 640.0
            }.show()

            if (employee.firstTimeLogin) dialog<String>(getString(R.string.change_password), getString(R.string.change_password), ImageView(R.png.ic_key)) {
                lateinit var changePasswordField: PasswordField
                lateinit var confirmPasswordField: PasswordField
                content = gridPane {
                    gap(8)
                    label(getString(R.string.password)) col 0 row 0
                    changePasswordField = passwordField { promptText = getString(R.string.password) } col 1 row 0
                    label(getString(R.string.change_password)) col 0 row 1
                    confirmPasswordField = passwordField { promptText = getString(R.string.change_password) } col 1 row 1
                }
                button(CANCEL)
                button(OK).disableProperty() bind (changePasswordField.textProperty().isEmpty
                        or confirmPasswordField.textProperty().isEmpty
                        or (changePasswordField.textProperty() neq confirmPasswordField.textProperty()))
                setResultConverter { if (it == OK) changePasswordField.text else null }
                runFX { changePasswordField.requestFocus() }
            }.showAndWait().filter { it is String }.ifPresent { newPassword ->
                transaction {
                    Employees.find { name.equal(EMPLOYEE) }.projection { password }.update(newPassword)
                    infoAlert(getString(R.string.change_password_successful)).showAndWait()
                }
            }
        }
    }

    private fun setOSXIcon(url: URL) {
        if (IS_OS_MAC_OSX) Class.forName("com.apple.eawt.Application")
                .newInstance()
                .javaClass
                .getMethod("getApplication")
                .invoke(null)
                .let { application ->
                    application.javaClass
                            .getMethod("setDockIconImage", java.awt.Image::class.java)
                            .invoke(application, getDefaultToolkit().getImage(url))
                }
    }
}