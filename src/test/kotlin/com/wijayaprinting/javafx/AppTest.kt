package com.wijayaprinting.javafx

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

    override val loginTitle: String get() = "AppTest.kt"

    override fun launch(employeeName: String, stage: Stage) {
        infoAlert(employeeName, "Successfully logged in.").showAndWait()
    }
}