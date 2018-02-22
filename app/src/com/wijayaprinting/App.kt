package com.wijayaprinting

import com.wijayaprinting.BuildConfig.DEBUG
import com.wijayaprinting.db.dao.Employee
import com.wijayaprinting.db.schema.Employees
import com.wijayaprinting.db.transaction
import com.wijayaprinting.io.properties.ConfigFile
import com.wijayaprinting.ui.Resourced
import com.wijayaprinting.ui.controller
import com.wijayaprinting.ui.main.LoginDialog
import com.wijayaprinting.ui.main.ResetPasswordDialog
import com.wijayaprinting.ui.pane
import com.wijayaprinting.util.getResource
import javafx.application.Application
import javafx.fxml.FXMLLoader
import javafx.scene.Scene
import javafx.scene.image.Image
import javafx.stage.Stage
import kotfx.dialogs.infoAlert
import kotfx.icon
import kotfx.minSize
import kotlinx.nosql.equal
import kotlinx.nosql.update
import org.apache.log4j.BasicConfigurator.configure
import java.util.ResourceBundle

class App : Application(), Resourced {

    companion object {
        @JvmStatic fun main(args: Array<String>) = Application.launch(App::class.java, *args)
    }

    override lateinit var language: Language
    override lateinit var resources: ResourceBundle

    override fun init() {
        if (DEBUG) configure()
        language = Language.from(ConfigFile.language.get())
        resources = language.resources
    }

    override fun start(stage: Stage) {
        stage.icon = Image(R.image.logo_launcher)

        LoginDialog(this).showAndWait().filter { it is Employee }.ifPresent { employee ->
            employee as Employee

            stage.apply {
                val loader = FXMLLoader(getResource(R.layout.controller_main), resources)
                title = getString(R.string.app_name)
                scene = Scene(loader.pane)
                minSize(1000, 650)
                loader.controller._employee = employee
            }.show()

            if (employee.firstTimeLogin) ResetPasswordDialog(this).showAndWait().ifPresent { newPassword ->
                transaction {
                    Employees.find { name.equal(employee.name) }.projection { password }.update(newPassword)
                    infoAlert(getString(R.string.change_password_successful)).showAndWait()
                }
            }
        }
    }
}