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
import javafx.scene.image.Image
import javafx.stage.Stage
import kotfx.icon
import kotfx.infoAlert
import kotfx.loadFXML
import kotfx.toScene
import kotlinx.coroutines.experimental.javafx.JavaFx
import kotlinx.coroutines.experimental.launch
import kotlinx.nosql.equal
import kotlinx.nosql.update
import org.apache.commons.lang3.SystemUtils.IS_OS_MAC_OSX
import org.apache.log4j.BasicConfigurator.configure
import java.awt.Toolkit.getDefaultToolkit
import java.lang.Class.forName
import java.net.URL
import java.util.*

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
        if (IS_OS_MAC_OSX) setOSXIcon(getResource(R.image.logo_launcher))

        LoginDialog(this).showAndWait().filter { it is Employee }.ifPresent { employee ->
            employee as Employee

            stage.apply {
                val loader = getResource(R.layout.controller_main).loadFXML(resources)
                title = getString(R.string.app_name)
                scene = loader.pane.toScene()
                minWidth = 1000.0
                minHeight = 650.0
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

    private fun setOSXIcon(url: URL) = launch(JavaFx) {
        forName("com.apple.eawt.Application")
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