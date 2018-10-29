package com.hendraanggrian.openpss

import com.hendraanggrian.openpss.BuildConfig.DEBUG
import com.hendraanggrian.openpss.db.schemas.Employees
import com.hendraanggrian.openpss.db.transaction
import com.hendraanggrian.openpss.i18n.Resources
import com.hendraanggrian.openpss.io.properties.PreferencesFile
import com.hendraanggrian.openpss.ui.controller
import com.hendraanggrian.openpss.ui.login.LoginPane
import com.hendraanggrian.openpss.ui.main.ChangePasswordDialog
import com.hendraanggrian.openpss.ui.pane
import com.hendraanggrian.openpss.util.getResource
import com.hendraanggrian.openpss.util.getStyle
import javafx.application.Application
import javafx.application.Platform
import javafx.fxml.FXMLLoader
import javafx.scene.image.Image
import javafx.stage.Stage
import kotlinx.nosql.equal
import kotlinx.nosql.update
import ktfx.application.launch
import ktfx.jfoenix.jfxSnackbar
import ktfx.layouts.scene
import ktfx.stage.icon
import ktfx.stage.setMinSize
import org.apache.log4j.BasicConfigurator
import java.util.ResourceBundle

class App : Application(), Resources {

    companion object {
        const val STRETCH_POINT = 1100.0

        const val DURATION_SHORT = 3000L
        const val DURATION_LONG = 6000L

        const val STYLE_BUTTON_FLAT = "button-flat"
        const val STYLE_BUTTON_RAISED = "button-raised"
        const val STYLE_LABEL_DISPLAY = "label-display"

        @JvmStatic fun main(args: Array<String>) = launch<App>(*args)

        fun exit() {
            Platform.exit() // exit JavaFX
            System.exit(0) // exit Java
        }
    }

    override lateinit var resources: ResourceBundle

    override fun init() {
        resources = PreferencesFile.language.toResourcesBundle()
        if (DEBUG) {
            BasicConfigurator.configure()
        }
    }

    override fun start(stage: Stage) {
        stage.icon = Image(R.image.logo_small)
        stage.isResizable = false
        stage.title = getString(R.string.openpss_login)
        stage.scene = scene {
            stylesheets += getStyle(R.style.openpss)
            LoginPane(this@App).apply {
                onSuccess = { employee ->
                    val loader = FXMLLoader(getResource(R.layout.controller_main), resources)
                    this@scene.run {
                        loader.pane()
                    }
                    val controller = loader.controller
                    controller.login = employee

                    stage.isResizable = true
                    stage.title = BuildConfig.NAME.let { if (BuildConfig.DEBUG) "$it - DEBUG" else it }
                    stage.setMinSize(800.0, 600.0)

                    if (employee.isFirstTimeLogin) {
                        ChangePasswordDialog(this).show { newPassword ->
                            transaction {
                                Employees { it.name.equal(employee.name) }.projection { password }.update(newPassword!!)
                                jfxSnackbar(getString(R.string.successfully_changed_password), App.DURATION_LONG)
                            }
                        }
                    }
                }
            }()
        }
        stage.show()
    }
}