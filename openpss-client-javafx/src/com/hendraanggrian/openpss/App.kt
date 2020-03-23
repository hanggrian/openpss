package com.hendraanggrian.openpss

import com.hendraanggrian.openpss.ui.Stylesheets
import com.hendraanggrian.openpss.ui.login.LoginPane
import com.hendraanggrian.openpss.ui.wage.EClockingReader
import com.hendraanggrian.openpss.util.controller
import com.hendraanggrian.openpss.util.getResource
import com.hendraanggrian.openpss.util.pane
import com.hendraanggrian.prefs.Prefs
import com.hendraanggrian.prefs.jvm.PropertiesPrefs
import com.hendraanggrian.prefs.jvm.get
import java.util.Properties
import java.util.ResourceBundle
import javafx.application.Application
import javafx.application.Platform
import javafx.fxml.FXMLLoader
import javafx.scene.image.Image
import javafx.stage.Stage
import kotlin.system.exitProcess
import ktfx.controls.icon
import ktfx.controls.setMinSize
import ktfx.launchApplication
import ktfx.layouts.addChild
import ktfx.layouts.scene
import org.apache.log4j.BasicConfigurator

class App : Application(), StringResources, ValueResources {

    companion object {
        @JvmStatic fun main(args: Array<String>) = launchApplication<App>(*args)

        fun exit() {
            Platform.exit() // exit JavaFX
            exitProcess(0) // exit Java
        }
    }

    private lateinit var prefs: PropertiesPrefs
    override lateinit var resourceBundle: ResourceBundle
    override lateinit var valueProperties: Properties

    override fun init() {
        prefs = Prefs[SettingsFile].also {
            it.setDefault()
            if (FxSetting.KEY_WAGEREADER !in it) {
                it[FxSetting.KEY_WAGEREADER] = EClockingReader.name
            }
        }
        resourceBundle = prefs.language.toResourcesBundle()
        valueProperties = App::class.java
            .getResourceAsStream(R.value._value)
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
            addChild(LoginPane(this@App, prefs)) {
                onSuccess = { employee ->
                    val loader = FXMLLoader(getResource(R.layout.controller_main), resourceBundle)
                    this@scene.run { addChild(loader.pane) }
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
