package com.wijayaprinting.javafx

import com.wijayaprinting.javafx.dialog.LoginDialog
import com.wijayaprinting.javafx.io.PreferencesFile
import com.wijayaprinting.javafx.io.WPFolder
import com.wijayaprinting.mysql.dao.Staff
import javafx.application.Application
import javafx.stage.Stage
import org.apache.commons.lang3.SystemUtils.IS_OS_MAC_OSX
import java.awt.Image
import java.awt.Toolkit

/**
 * @author Hendra Anggrian (hendraanggrian@gmail.com)
 */
abstract class WPApp : Application() {

    abstract val requiredStaffLevel: Int

    abstract fun launch(staff: Staff, stage: Stage)

    override fun init() {
        WPFolder()
    }

    override fun start(primaryStage: Stage) {
        primaryStage.icons.add(javafx.scene.image.Image(R.png.ic_launcher_512px))
        setImageOnOSX(Toolkit.getDefaultToolkit().getImage(WPApp::class.java.getResource(R.png.ic_launcher_512px)))

        val resources = Language.parse(PreferencesFile().language.value).getResources("strings")
        LoginDialog(resources, requiredStaffLevel)
                .showAndWait()
                .filter { it is Staff }
                .ifPresent { launch(it as Staff, primaryStage) }
    }

    protected fun setImageOnOSX(image: Image) {
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