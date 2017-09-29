package com.wijayaprinting.javafx

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

    override fun onStart() {
        dialog.title = "Test title"
        dialog.headerText = "Test header text"
        dialog.graphic.children.add(ImageView(Image(AppTest::class.java.getResourceAsStream("/ic_launcher_96px.png"))))
    }

    override fun onSuccess(employeeName: String, stage: Stage) {
        infoAlert(employeeName, "Successfully logged in.").showAndWait()
    }
}