package com.hendraanggrian.openpss

import com.hendraanggrian.defaults.WritableDefaults
import com.hendraanggrian.defaults.toDefaults
import com.hendraanggrian.openpss.ui.Stylesheets
import com.hendraanggrian.openpss.ui.login.LoginPane
import com.hendraanggrian.openpss.ui.wage.EClockingReader
import com.hendraanggrian.openpss.util.controller
import com.hendraanggrian.openpss.util.getResource
import com.hendraanggrian.openpss.util.pane
import java.util.Properties
import java.util.ResourceBundle
import javafx.application.Application
import javafx.application.Platform
import javafx.fxml.FXMLLoader
import javafx.scene.image.Image
import javafx.stage.Stage
import kotlin.system.exitProcess
import ktfx.layouts.addNode
import ktfx.layouts.scene
import ktfx.windows.icon
import ktfx.windows.setMinSize
import org.apache.log4j.BasicConfigurator

class App : Application(), StringResources, ValueResources {

    companion object {

        @JvmStatic
        fun main(args: Array<String>) = ktfx.launch<App>(*args)

        fun exit() {
            Platform.exit() // exit JavaFX
            exitProcess(0) // exit Java
        }
    }

    private lateinit var defaults: WritableDefaults
    override lateinit var resourceBundle: ResourceBundle
    override lateinit var valueProperties: Properties

    override fun init() {
        defaults = SettingsFile.toDefaults().also {
            it.setDefault()
            if (FxSetting.KEY_WAGEREADER !in it) {
                it[FxSetting.KEY_WAGEREADER] = EClockingReader.name
            }
        }
        resourceBundle = defaults.language.toResourcesBundle()
        valueProperties = App::class.java
            .getResourceAsStream(R.value.properties_value)
            .use { stream -> Properties().apply { load(stream) } }
        if (BuildConfig2.DEBUG) {
            BasicConfigurator.configure()
        }
    }

    override fun start(stage: Stage) {
        stage.icon = Image(R.image.logo_small)
        stage.isResizable = false
        stage.title = getString(R2.string.openpss_login)
        stage.scene = scene {
            stylesheets += Stylesheets.OPENPSS
            addNode(LoginPane(this@App, defaults)) {
                onSuccess = { employee ->
                    val loader = FXMLLoader(getResource(R.layout.controller_main), resourceBundle)
                    this@scene.run {
                        addNode(loader.pane)
                    }
                    val controller = loader.controller
                    controller.login = employee

                    stage.isResizable = true
                    stage.setMinSize(900.0, 500.0)
                }
            }
        }
        stage.show()
    }
}
