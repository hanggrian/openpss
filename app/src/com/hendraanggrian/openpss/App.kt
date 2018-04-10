package com.hendraanggrian.openpss

import com.hendraanggrian.openpss.BuildConfig.APP_NAME
import com.hendraanggrian.openpss.BuildConfig.DEBUG
import com.hendraanggrian.openpss.db.schemas.Employee
import com.hendraanggrian.openpss.db.schemas.Employees
import com.hendraanggrian.openpss.db.transaction
import com.hendraanggrian.openpss.io.properties.LoginFile
import com.hendraanggrian.openpss.ui.Resourced
import com.hendraanggrian.openpss.ui.controller
import com.hendraanggrian.openpss.ui.main.LoginDialog
import com.hendraanggrian.openpss.ui.main.ResetPasswordDialog
import com.hendraanggrian.openpss.ui.pane
import com.hendraanggrian.openpss.utils.getResource
import javafx.application.Application
import javafx.collections.ObservableList
import javafx.fxml.FXMLLoader
import javafx.scene.Scene
import javafx.scene.image.Image
import javafx.stage.Stage
import kotlinx.nosql.equal
import kotlinx.nosql.update
import ktfx.application.launch
import ktfx.collections.observableListOf
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

        inline val supportedLocales: ObservableList<Locale> get() = observableListOf(Locale("en"), Locale("in"))
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
                title = if (DEBUG) "$APP_NAME - debug mode" else APP_NAME
                scene = Scene(loader.pane)
                setMinSize(1000.0, 650.0)
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