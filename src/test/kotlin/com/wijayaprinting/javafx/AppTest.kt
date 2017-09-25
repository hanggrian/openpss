package com.wijayaprinting.javafx

import javafx.stage.Stage

/**
 * @author Hendra Anggrian (hendraanggrian@gmail.com)
 */
class AppTest : WijayaPrintingApp() {

    companion object {
        @JvmStatic
        fun main(vararg args: String) = launch(AppTest::class.java, *args)
    }

    override fun launch(stage: Stage) {

    }
}