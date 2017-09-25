package com.wijayaprinting.javafx

import com.wijayaprinting.javafx.dialog.LoginDialog
import com.wijayaprinting.javafx.io.HomeFolder
import com.wijayaprinting.javafx.io.PreferencesFile
import com.wijayaprinting.mysql.dao.Staff
import javafx.application.Application
import javafx.stage.Stage

/**
 * @author Hendra Anggrian (hendraanggrian@gmail.com)
 */
abstract class WijayaPrintingApp : Application() {

    override fun init() {
        HomeFolder()
    }

    override fun start(primaryStage: Stage) {
        val resources = Language.parse(PreferencesFile().language.value).getResources("strings", WijayaPrintingApp::class.java.classLoader)
        LoginDialog(resources, Staff.LEVEL_ADMIN)
                .showAndWait()
                .ifPresent { launch(primaryStage) }
    }

    abstract fun launch(stage: Stage)
}