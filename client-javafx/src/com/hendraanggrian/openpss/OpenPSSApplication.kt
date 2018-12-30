package com.hendraanggrian.openpss

import com.hendraanggrian.openpss.io.SettingsFile
import com.hendraanggrian.openpss.ui.Resources
import com.hendraanggrian.openpss.ui.Stylesheets
import com.hendraanggrian.openpss.ui.login.LoginPane
import com.hendraanggrian.openpss.util.controller
import com.hendraanggrian.openpss.util.getResource
import com.hendraanggrian.openpss.util.pane
import javafx.application.Application
import javafx.application.Platform
import javafx.fxml.FXMLLoader
import javafx.scene.image.Image
import javafx.stage.Stage
import ktfx.launch
import ktfx.layouts.scene
import ktfx.windows.icon
import ktfx.windows.setMinSize
import org.apache.log4j.BasicConfigurator
import java.util.Properties
import java.util.ResourceBundle

class OpenPSSApplication : Application(), Resources {

    companion object {
        const val STRETCH_POINT = 900.0

        @JvmStatic
        fun main(args: Array<String>) = launch<OpenPSSApplication>(*args)

        fun exit() {
            Platform.exit() // exit JavaFX
            System.exit(0) // exit Java
        }
    }

    override lateinit var resourceBundle: ResourceBundle

    override lateinit var valueProperties: Properties

    override fun init() {
        resourceBundle = SettingsFile.language.toResourcesBundle()
        valueProperties = getProperties(R.value.properties_value)
        if (BuildConfig.DEBUG) {
            BasicConfigurator.configure()
        }
    }

    override fun start(stage: Stage) {
        stage.icon = Image(R.image.logo_small)
        stage.isResizable = false
        stage.title = getString(R.string.openpss_login)
        stage.scene = scene {
            stylesheets += Stylesheets.OPENPSS
            LoginPane(this@OpenPSSApplication).apply {
                onSuccess = { employee ->
                    val loader = FXMLLoader(getResource(R.layout.controller_main), resourceBundle)
                    this@scene.run {
                        loader.pane()
                    }
                    val controller = loader.controller
                    controller.login = employee

                    stage.isResizable = true
                    stage.setMinSize(800.0, 480.0)
                }
            }()
        }
        stage.show()
    }
}