package com.hendraanggrian.openpss

import com.hendraanggrian.openpss.BuildConfig.DEBUG
import com.hendraanggrian.openpss.db.schemas.Employees
import com.hendraanggrian.openpss.db.transaction
import com.hendraanggrian.openpss.i18n.Resourced
import com.hendraanggrian.openpss.io.properties.PreferencesFile
import com.hendraanggrian.openpss.ui.controller
import com.hendraanggrian.openpss.ui.login.LoginLayout
import com.hendraanggrian.openpss.ui.main.ChangePasswordDialog
import com.hendraanggrian.openpss.ui.pane
import com.hendraanggrian.openpss.util.getResource
import com.hendraanggrian.openpss.util.getStyle
import javafx.application.Application
import javafx.fxml.FXMLLoader
import javafx.scene.image.Image
import javafx.stage.Stage
import kotlinx.nosql.equal
import kotlinx.nosql.update
import ktfx.application.launch
import ktfx.layouts.scene
import ktfx.scene.control.infoAlert
import ktfx.stage.icon
import ktfx.stage.setMinSize
import org.apache.log4j.BasicConfigurator
import java.util.ResourceBundle

class App : Application(), Resourced {

    companion object {
        const val STRETCH_POINT = 1100.0

        const val STYLE_BUTTON_FLAT = "button-flat"
        const val STYLE_BUTTON_RAISED = "button-raised"

        @JvmStatic fun main(args: Array<String>) = launch<App>(*args)
    }

    override lateinit var resources: ResourceBundle

    override fun init() {
        resources = PreferencesFile.language.toResourcesBundle()
        if (DEBUG) {
            BasicConfigurator.configure()
        }
    }

    override fun start(stage: Stage) {
        stage.icon = Image(R.image.display_launcher)
        stage.isResizable = false
        stage.title = getString(R.string.openpss_login)
        stage.scene = scene {
            stylesheets += getStyle(R.style.openpss)
            LoginLayout(this@App).apply {
                setOnSuccess { employee ->
                    val loader = FXMLLoader(getResource(R.layout.controller_main), resources)
                    this@scene.run {
                        loader.pane()
                    }
                    val controller = loader.controller
                    controller.employee = employee

                    stage.isResizable = true
                    stage.title = BuildConfig.NAME.let { if (BuildConfig.DEBUG) "$it - DEBUG" else it }
                    stage.setMinSize(800.0, 600.0)

                    if (employee.isFirstTimeLogin) {
                        ChangePasswordDialog(this).show(controller.dialogContainer) { newPassword ->
                            transaction {
                                Employees { it.name.equal(employee.name) }.projection { password }.update(newPassword!!)
                                infoAlert(getString(R.string.successfully_changed_password)) {
                                    dialogPane.stylesheets += getStyle(R.style.openpss)
                                }.show()
                            }
                        }
                    }
                }
            }()
        }
        stage.show()
    }
}