package com.hendraanggrian.openpss

import com.hendraanggrian.openpss.BuildConfig.APP_NAME
import com.hendraanggrian.openpss.BuildConfig.DEBUG
import com.hendraanggrian.openpss.db.schema.Employee
import com.hendraanggrian.openpss.db.schema.Employees
import com.hendraanggrian.openpss.db.transaction
import com.hendraanggrian.openpss.io.properties.ConfigFile
import com.hendraanggrian.openpss.ui.Resourced
import com.hendraanggrian.openpss.ui.controller
import com.hendraanggrian.openpss.ui.main.LoginDialog
import com.hendraanggrian.openpss.ui.main.ResetPasswordDialog
import com.hendraanggrian.openpss.ui.pane
import com.hendraanggrian.openpss.util.getResource
import javafx.application.Application
import javafx.fxml.FXMLLoader
import javafx.scene.Scene
import javafx.scene.image.Image
import javafx.stage.Stage
import kfx.application.deploy
import kfx.scene.control.infoAlert
import kfx.stage.icon
import kfx.stage.setSizeMin
import kotlinx.nosql.equal
import kotlinx.nosql.update
import org.apache.log4j.BasicConfigurator.configure
import java.util.ResourceBundle

class App : Application(), Resourced {

    companion object {
        @JvmStatic fun main(args: Array<String>) = deploy<App>(*args)
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
                title = APP_NAME
                scene = Scene(loader.pane)
                setSizeMin(1000, 650)
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