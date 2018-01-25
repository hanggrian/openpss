package com.wijayaprinting

import com.wijayaprinting.BuildConfig.DEBUG
import com.wijayaprinting.db.Database
import com.wijayaprinting.db.dao.Employee
import com.wijayaprinting.db.schema.Employees
import com.wijayaprinting.db.transaction
import com.wijayaprinting.io.properties.ConfigFile
import com.wijayaprinting.io.properties.DatabaseFile
import com.wijayaprinting.ui.*
import com.wijayaprinting.ui.scene.control.HostField
import com.wijayaprinting.ui.scene.control.IntField
import com.wijayaprinting.ui.scene.control.hostField
import com.wijayaprinting.ui.scene.control.intField
import com.wijayaprinting.util.forceExit
import com.wijayaprinting.util.getResource
import com.wijayaprinting.util.multithread
import io.reactivex.rxkotlin.subscribeBy
import javafx.application.Application
import javafx.application.ConditionalFeature.UNIFIED_WINDOW
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
import javafx.stage.StageStyle.UNIFIED
import kotfx.*
import kotlinx.nosql.equal
import kotlinx.nosql.update
import org.apache.commons.lang3.SystemUtils.IS_OS_MAC_OSX
import org.apache.log4j.BasicConfigurator.configure
import java.awt.Toolkit.getDefaultToolkit
import java.net.URL
import java.util.*

class App : Application(), Resourced, EmployeeHolder {

    companion object {
        @JvmStatic fun main(args: Array<String>) = launch(App::class.java, *args)
    }

    override lateinit var resources: ResourceBundle
    override lateinit var _employee: Employee

    override fun init() {
        if (DEBUG) configure()
        resources = Language.from(ConfigFile.language.get()).resources
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
                choiceBox(Language.values().toObservableList()) {
                    maxWidth = Double.MAX_VALUE
                    selectionModel.select(Language.from(ConfigFile.language.get()))
                    selectionModel.selectedItemProperty().addListener { _, _, language ->
                        ConfigFile.language.set(language.locale)
                        ConfigFile.save()
                        close()
                        infoAlert(getString(R.string.please_restart)).showAndWait().ifPresent { forceExit() }
                    }
                } col 1 row 0 colSpan 2
                label(getString(R.string.employee)) col 0 row 1
                employeeField = textField {
                    promptText = getString(R.string.employee)
                    textProperty() bindBidirectional ConfigFile.employee
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
                    textProperty() bindBidirectional DatabaseFile.host
                } col 1 row 0
                serverPortField = intField {
                    promptText = getString(R.string.port)
                    prefWidth = 64.0
                    textProperty() bindBidirectional DatabaseFile.port
                } col 2 row 0
                label(getString(R.string.server_user)) col 0 row 1
                serverUserField = textField {
                    promptText = getString(R.string.server_user)
                    textProperty() bindBidirectional DatabaseFile.user
                } col 1 row 1 colSpan 2
                label(getString(R.string.server_password)) col 0 row 2
                serverPasswordField = passwordField {
                    promptText = getString(R.string.server_password)
                    textProperty() bindBidirectional DatabaseFile.password
                } col 1 row 2 colSpan 2
                hbox {
                    alignment = CENTER_RIGHT
                    hyperlink(getString(R.string.test_connection)) {
                        setOnAction {
                            Database.testConnection(serverHostField.text, serverPortField.value, serverUserField.text, serverPasswordField.text)
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
                    ConfigFile.save()
                    DatabaseFile.save()
                    Database.login(serverHostField.text, serverPortField.value, serverUserField.text, serverPasswordField.text, employeeField.text, passwordField.text)
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
            runLater {
                if (employeeField.text.isBlank()) employeeField.requestFocus() else passwordField.requestFocus()
                isExpanded = !DatabaseFile.isValid
                if (DEBUG) {
                    passwordField.text = "Test"
                }
            }
        }.showAndWait().filter { it is Employee }.ifPresent { employee ->
            _employee = employee as Employee

            stage.apply {
                if (UNIFIED_WINDOW.isSupported) initStyle(UNIFIED)
                val loader = getResource(R.fxml.layout_main).loadFXML(resources)
                title = getString(R.string.app_name)
                scene = loader.pane.toScene()
                minWidth = 960.0
                minHeight = 640.0
                loader.controller._employee = employee
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
                runLater { changePasswordField.requestFocus() }
            }.showAndWait().filter { it is String }.ifPresent { newPassword ->
                transaction {
                    Employees.find { name.equal(employeeName) }.projection { password }.update(newPassword)
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