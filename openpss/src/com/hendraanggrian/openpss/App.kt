package com.hendraanggrian.openpss

import com.hendraanggrian.openpss.BuildConfig.DEBUG
import com.hendraanggrian.openpss.content.Resources
import com.hendraanggrian.openpss.content.STYLESHEET_OPENPSS
import com.hendraanggrian.openpss.io.properties.PreferencesFile
import com.hendraanggrian.openpss.ui.login.LoginPane
import com.hendraanggrian.openpss.util.controller
import com.hendraanggrian.openpss.util.getResource
import com.hendraanggrian.openpss.util.pane
import javafx.application.Application
import javafx.application.Platform
import javafx.fxml.FXMLLoader
import javafx.scene.image.Image
import javafx.stage.Stage
import ktfx.application.launchApp
import ktfx.layouts.scene
import ktfx.stage.icon
import ktfx.stage.setMinSize
import org.apache.log4j.BasicConfigurator
import java.util.Properties
import java.util.ResourceBundle

class App : Application(), Resources {

    companion object {

        const val STRETCH_POINT = 900.0

        const val DURATION_SHORT = 3000L
        const val DURATION_LONG = 6000L

        @JvmStatic fun main(args: Array<String>) = launchApp<App>(*args)

        fun exit() {
            Platform.exit() // exit JavaFX
            System.exit(0) // exit Java
        }
    }

    override lateinit var resourceBundle: ResourceBundle

    override lateinit var dimenResources: Properties

    override lateinit var colorResources: Properties

    override fun init() {
        resourceBundle = PreferencesFile.language.toResourcesBundle()
        dimenResources = getProperties(R.dimen.properties_dimen)
        colorResources = getProperties(R.color.properties_color)
        if (DEBUG) {
            BasicConfigurator.configure()
        }
    }

    override fun start(stage: Stage) {
        stage.icon = Image(R.image.logo_small)
        stage.isResizable = false
        stage.title = getString(R.string.openpss_login)
        stage.scene = scene {
            stylesheets += STYLESHEET_OPENPSS
            LoginPane(this@App).apply {
                onSuccess = { employee ->
                    val loader = FXMLLoader(getResource(R.layout.controller_main), resourceBundle)
                    this@scene.run {
                        loader.pane()
                    }
                    val controller = loader.controller
                    controller.login = employee

                    stage.isResizable = true
                    stage.title = BuildConfig.NAME.let { if (BuildConfig.DEBUG) "$it - DEBUG" else it }
                    stage.setMinSize(800.0, 480.0)
                }
            }()
        }
        stage.show()
    }
}