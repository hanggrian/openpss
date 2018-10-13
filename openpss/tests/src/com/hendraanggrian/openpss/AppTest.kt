package com.hendraanggrian.openpss

import javafx.application.Application
import javafx.fxml.FXMLLoader
import javafx.scene.Scene
import javafx.stage.Stage
import ktfx.application.launch
import java.net.URL

class AppTest : Application() {

    companion object {
        @JvmStatic fun main(args: Array<String>) = launch<AppTest>(*args)
    }

    override fun start(stage: Stage) {
        stage.scene = Scene(FXMLLoader.load(URL(AppTest::class.java.getResource("/test_segmentedpane.fxml").toExternalForm())))
        stage.show()
    }
}