package com.wijayaprinting.javafx

import javafx.scene.Scene
import javafx.scene.image.Image
import javafx.scene.image.ImageView

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

    override fun onSuccess(employeeName: String) {
        stage.apply {
            scene = Scene(Controller.inflate(AppTest::class.java.getResource("/layout_test.fxml"), resources, employeeName))
        }.show()
    }
}