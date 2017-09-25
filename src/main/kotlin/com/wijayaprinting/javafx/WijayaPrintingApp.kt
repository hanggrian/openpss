package com.wijayaprinting.javafx

import com.wijayaprinting.javafx.dialog.LoginDialog
import com.wijayaprinting.javafx.io.HomeFolder
import com.wijayaprinting.javafx.io.PreferencesFile
import javafx.application.Application
import javafx.stage.Stage

/**
 * @author Hendra Anggrian (hendraanggrian@gmail.com)
 */
abstract class WijayaPrintingApp : Application() {

    abstract val requiredStaffLevel: Int

    abstract fun launch(stage: Stage)

    override fun init() {
        HomeFolder()
    }

    override fun start(primaryStage: Stage) {
        val resources = Language.parse(PreferencesFile().language.value).getResources("strings", WijayaPrintingApp::class.java.classLoader)
        LoginDialog(resources, requiredStaffLevel)
                .showAndWait()
                .ifPresent { launch(primaryStage) }
    }
}