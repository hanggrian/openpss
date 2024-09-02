package com.hanggrian.openpss

import com.hanggrian.openpss.io.properties.PreferencesFile
import com.hanggrian.openpss.ui.login.LoginPane
import com.hanggrian.openpss.util.controller
import com.hanggrian.openpss.util.getResource
import com.hanggrian.openpss.util.pane
import javafx.application.Application
import javafx.application.Platform
import javafx.fxml.FXMLLoader
import javafx.scene.image.Image
import javafx.stage.Stage
import ktfx.launchApplication
import ktfx.layouts.scene
import ktfx.time.s
import ktfx.windows.icon
import ktfx.windows.minSize
import ktfx.windows.size2
import org.slf4j.LoggerFactory
import java.util.Properties
import java.util.ResourceBundle
import kotlin.system.exitProcess

class OpenPssApp :
    Application(),
    Resources {
    override lateinit var resourceBundle: ResourceBundle
    override lateinit var dimenResources: Properties
    override lateinit var colorResources: Properties

    override fun init() {
        if (BuildConfig.DEBUG) {
            LOGGER.info("DEBUG mode is on")
        }
        resourceBundle = PreferencesFile.language.toResourcesBundle()
        dimenResources = getProperties(R.dimen_dimen)
        colorResources = getProperties(R.color_color)
    }

    override fun start(stage: Stage) {
        stage.icon = Image(R.image_logo_small)
        stage.isResizable = false
        stage.title = getString(R.string_openpss_login)
        stage.scene =
            scene {
                stylesheets += STYLESHEET_OPENPSS
                addChild(
                    LoginPane(this@OpenPssApp).apply {
                        onSuccess = { employee ->
                            val loader =
                                FXMLLoader(getResource(R.layout_controller_main), resourceBundle)
                            this@scene.run {
                                addChild(loader.pane)
                            }
                            val controller = loader.controller
                            controller.login = employee

                            stage.isResizable = true
                            stage.size2 = 720.0 to 480.0
                            stage.minSize = 720.0 to 480.0
                        }
                    },
                )
            }
        stage.show()
    }

    companion object {
        val DURATION_SHORT = 3.s
        val DURATION_LONG = 6.s

        private val LOGGER = LoggerFactory.getLogger(OpenPssApp::class.java)!!

        @JvmStatic
        fun main(args: Array<String>) = launchApplication<OpenPssApp>(*args)

        fun exit() {
            Platform.exit() // exit JavaFX
            exitProcess(0) // exit Java
        }
    }
}
