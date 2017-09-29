package com.wijayaprinting.javafx

import javafx.scene.Node
import javafx.scene.image.Image
import javafx.scene.image.ImageView
import javafx.stage.Stage
import kotfx.dialogs.infoAlert

/**
 * @author Hendra Anggrian (hendraanggrian@gmail.com)
 */
class AppTest : WPApp() {

    companion object {
        @JvmStatic
        fun main(vararg args: String) = launch(AppTest::class.java, *args)
    }

    override val loginHeader: String get() = "Test"
    override val loginGraphic: Node get() = ImageView(Image(AppTest::class.java.getResourceAsStream("/ic_launcher_96px.png")))

    override fun launch(employeeName: String, stage: Stage) {
        infoAlert(employeeName, "Successfully logged in.").showAndWait()
    }
}