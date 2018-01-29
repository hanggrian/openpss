package com.wijayaprinting

import com.wijayaprinting.BuildConfig.DEBUG
import com.wijayaprinting.db.dao.Employee
import com.wijayaprinting.db.schema.Employees
import com.wijayaprinting.db.transaction
import com.wijayaprinting.io.properties.ConfigFile
import com.wijayaprinting.ui.*
import com.wijayaprinting.ui.main.LoginDialog
import com.wijayaprinting.util.getResource
import javafx.application.Application
import javafx.scene.control.ButtonType.CANCEL
import javafx.scene.control.ButtonType.OK
import javafx.scene.control.PasswordField
import javafx.scene.image.Image
import javafx.scene.image.ImageView
import javafx.stage.Stage
import kotfx.*
import kotlinx.nosql.equal
import kotlinx.nosql.update
import org.apache.commons.lang3.SystemUtils.IS_OS_MAC_OSX
import org.apache.log4j.BasicConfigurator.configure
import java.awt.Toolkit.getDefaultToolkit
import java.lang.Class.forName
import java.net.URL
import java.util.*

class App : Application(), Resourced, EmployeeHolder {

    companion object {
        @JvmStatic fun main(args: Array<String>) = launch(App::class.java, *args)
    }

    override lateinit var language: Language
    override lateinit var resources: ResourceBundle
    override lateinit var _employee: Employee

    override fun init() {
        if (DEBUG) configure()
        language = Language.from(ConfigFile.language.get())
        resources = language.resources
    }

    override fun start(stage: Stage) {
        stage.icon = Image(R.image.logo_launcher)
        setOSXIcon(getResource(R.image.logo_launcher))
        LoginDialog(this).showAndWait().filter { it is Employee }.ifPresent { employee ->
            _employee = employee as Employee

            stage.apply {
                val loader = getResource(R.layout.controller_main).loadFXML(resources)
                title = getString(R.string.app_name)
                scene = loader.pane.toScene()
                minWidth = 960.0
                minHeight = 640.0
                loader.controller._employee = employee
            }.show()

            if (employee.firstTimeLogin) dialog<String>(getString(R.string.change_password), getString(R.string.change_password), ImageView(R.image.ic_key)) {
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
                button(OK).disableProperty().bind(changePasswordField.textProperty().isEmpty
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
        if (IS_OS_MAC_OSX) forName("com.apple.eawt.Application")
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