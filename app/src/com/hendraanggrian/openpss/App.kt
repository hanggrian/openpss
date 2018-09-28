package com.hendraanggrian.openpss

import com.hendraanggrian.openpss.BuildConfig.DEBUG
import com.hendraanggrian.openpss.BuildConfig.NAME
import com.hendraanggrian.openpss.db.schemas.Employee
import com.hendraanggrian.openpss.db.schemas.Employees
import com.hendraanggrian.openpss.db.transaction
import com.hendraanggrian.openpss.i18n.Resourced
import com.hendraanggrian.openpss.io.properties.PreferencesFile
import com.hendraanggrian.openpss.ui.controller
import com.hendraanggrian.openpss.ui.main.ChangePasswordDialog
import com.hendraanggrian.openpss.ui.main.LoginDialog
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
import ktfx.scene.control.styledInfoAlert
import ktfx.stage.icon
import ktfx.stage.setMinSize
import org.apache.log4j.BasicConfigurator
import java.util.ResourceBundle

class App : Application(), Resourced {

    companion object {
        const val STYLE_NAVIGATION_PANE = "navigation-pane"
        const val STYLE_DISPLAY_LABEL = "display-label"
        const val STYLE_DEFAULT_BUTTON = "default-button"
        const val STYLE_SEARCH_TEXTFIELD = "search-textfield"
        const val STYLE_BORDERLESS_TITLEDPANE = "borderless-titledpane"

        @JvmStatic fun main(args: Array<String>) = launch<App>(*args)
    }

    override lateinit var resources: ResourceBundle

    override fun init() {
        resources = PreferencesFile.language.toResourcesBundle()
        if (DEBUG) BasicConfigurator.configure()
    }

    override fun start(stage: Stage) {
        stage.icon = Image(R.image.display_launcher)

        LoginDialog(this).showAndWait().filter { it is Employee }.ifPresent { employee ->
            employee as Employee

            stage.apply {
                val loader = FXMLLoader(getResource(R.layout.controller_main), resources)
                title = "$NAME - ${employee.name}".let { if (DEBUG) "$it - DEBUG" else it }
                scene = scene(loader.pane) {
                    stylesheets += getStyle(R.style.openpss)
                }
                setMinSize(850.0, 450.0)
                loader.controller.employee = employee
            }.show()

            if (employee.isFirstTimeLogin) ChangePasswordDialog(this).showAndWait().ifPresent { newPassword ->
                transaction {
                    Employees { it.name.equal(employee.name) }.projection { password }.update(newPassword)
                    styledInfoAlert(getStyle(R.style.openpss), getString(R.string.successfully_changed_password)).show()
                }
            }
        }
    }
}