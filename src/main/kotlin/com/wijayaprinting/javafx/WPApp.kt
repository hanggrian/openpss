package com.wijayaprinting.javafx

import com.wijayaprinting.javafx.dialog.LoginDialog
import com.wijayaprinting.javafx.io.JavaFXFile
import javafx.application.Application
import javafx.scene.Node
import javafx.stage.Stage
import org.apache.commons.lang3.SystemUtils.IS_OS_MAC_OSX

/**
 * @author Hendra Anggrian (hendraanggrian@gmail.com)
 */
abstract class WPApp : Application() {

    abstract val loginHeader: String

    abstract val loginGraphic: Node

    abstract fun launch(employeeName: String, stage: Stage)

    override fun start(primaryStage: Stage) {
        val resources = Language.parse(JavaFXFile()[JavaFXFile.LANGUAGE].value).getResources("javafx")
        LoginDialog(resources, loginHeader, loginGraphic)
                .showAndWait()
                .filter { it is String }
                .ifPresent { launch(it as String, primaryStage) }
    }

    protected fun setImageOnOSX(image: java.awt.Image) {
        if (IS_OS_MAC_OSX) {
            Class.forName("com.apple.eawt.Application")
                    .newInstance()
                    .javaClass
                    .getMethod("getApplication")
                    .invoke(null).let { application ->
                application.javaClass
                        .getMethod("setDockIconImage", java.awt.Image::class.java)
                        .invoke(application, image)
            }
        }
    }
}