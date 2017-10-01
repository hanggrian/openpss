package com.wijayaprinting.javafx

import com.wijayaprinting.javafx.dialog.LoginDialog
import com.wijayaprinting.javafx.io.JavaFXFile
import javafx.application.Application
import javafx.stage.Stage
import kotfx.runLater
import org.apache.commons.lang3.SystemUtils.IS_OS_MAC_OSX
import java.util.*

/**
 * @author Hendra Anggrian (hendraanggrian@gmail.com)
 */
abstract class WPApp : Application() {

    protected lateinit var dialog: LoginDialog
    protected lateinit var resources: ResourceBundle
    protected lateinit var stage: Stage

    abstract fun onStart()
    abstract fun onSuccess(employeeName: String)

    override fun init() {
        resources = Language.parse(JavaFXFile()[JavaFXFile.LANGUAGE].value).getResources("string")
    }

    override fun start(primaryStage: Stage) {
        stage = primaryStage
        dialog = LoginDialog(resources)
        onStart()
        dialog.showAndWait()
                .filter { it is String }
                .ifPresent { onSuccess(it as String) }
    }
}