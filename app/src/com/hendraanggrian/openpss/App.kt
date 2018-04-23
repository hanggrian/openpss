package com.hendraanggrian.openpss

import com.hendraanggrian.openpss.BuildConfig.APP_NAME
import com.hendraanggrian.openpss.BuildConfig.DEBUG
import com.hendraanggrian.openpss.db.schemas.Employee
import com.hendraanggrian.openpss.db.schemas.Employees
import com.hendraanggrian.openpss.db.transaction
import com.hendraanggrian.openpss.io.properties.LoginFile
import com.hendraanggrian.openpss.core.fx.Resourced
import com.hendraanggrian.openpss.ui.main.LoginDialog
import com.hendraanggrian.openpss.ui.main.ResetPasswordDialog
import com.hendraanggrian.openpss.utils.controller
import com.hendraanggrian.openpss.utils.getResource
import com.hendraanggrian.openpss.utils.pane
import com.hendraanggrian.openpss.utils.style
import javafx.application.Application
import javafx.fxml.FXMLLoader
import javafx.scene.Scene
import javafx.scene.image.Image
import javafx.stage.Stage
import kotlinx.nosql.equal
import kotlinx.nosql.update
import ktfx.application.launch
import ktfx.scene.control.infoAlert
import ktfx.stage.icon
import ktfx.stage.setMinSize
import org.apache.log4j.BasicConfigurator.configure
import java.util.Locale
import java.util.ResourceBundle
import java.util.ResourceBundle.getBundle

class App : Application(), Resourced {

    companion object {
        @JvmStatic fun main(args: Array<String>) = launch<App>(*args)
    }

    override lateinit var resources: ResourceBundle

    override fun init() {
        if (DEBUG) configure()
        resources = getBundle("string", Locale(LoginFile.LANGUAGE))
    }

    override fun start(stage: Stage) {
        stage.icon = Image(R.image.logo_launcher)

        LoginDialog(this).showAndWait().filter { it is Employee }.ifPresent { employee ->
            employee as Employee

            stage.apply {
                val loader = FXMLLoader(getResource(R.layout.controller_main), resources)
                title = if (DEBUG) "$APP_NAME [DEBUG]" else APP_NAME
                scene = Scene(loader.pane).apply { style() }
                setMinSize(1000.0, 650.0)
                loader.controller._employee = employee
            }.show()

            if (employee.firstTimeLogin) ResetPasswordDialog(this).showAndWait().ifPresent { newPassword ->
                transaction {
                    Employees.find { name.equal(employee.name) }.projection { password }.update(newPassword)
                    infoAlert(getString(R.string.successfully_changed_password)) { style() }.show()
                }
            }
        }
    }
}