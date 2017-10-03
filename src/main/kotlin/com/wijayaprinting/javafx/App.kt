package com.wijayaprinting.javafx

import com.wijayaprinting.javafx.dialog.LoginDialog
import com.wijayaprinting.javafx.io.JavaFXFile
import com.wijayaprinting.javafx.utils.*
import javafx.application.Application
import javafx.fxml.FXMLLoader
import javafx.scene.Scene
import javafx.scene.image.Image
import javafx.scene.image.ImageView
import javafx.stage.Stage
import java.awt.Toolkit

/**
 * @author Hendra Anggrian (hendraanggrian@gmail.com)
 */
class App : Application() {

    companion object {
        @JvmStatic
        fun main(vararg args: String) = launch(App::class.java, *args)
    }

    override fun init() {
        initResources(Language.parse(JavaFXFile()[JavaFXFile.LANGUAGE].value).getResources("string"))
    }

    override fun start(stage: Stage) {
        stage.icon = Image(R.png.logo_launcher)
        setIconOnOSX(Toolkit.getDefaultToolkit().getImage(App::class.java.getResource(R.png.logo_launcher)))

        LoginDialog()
                .apply {
                    icon = Image(R.png.ic_launcher)
                    title = "${getString(R.string.app_name)} ${BuildConfig.VERSION}"
                    graphic.children.add(ImageView(Image(R.png.ic_launcher)))
                }
                .showAndWait()
                .filter { it is String }
                .ifPresent {
                    val minSize = Pair(720.0, 640.0)
                    stage.apply {
                        scene = Scene(FXMLLoader.load(App::class.java.getResource(R.fxml.layout_main), resources), minSize.first, minSize.second)
                        icons.add(Image(R.png.ic_launcher))
                        title = "${getString(R.string.app_name)} ${BuildConfig.VERSION}"
                        minWidth = minSize.first
                        minHeight = minSize.second
                    }.show()
                }
    }
}