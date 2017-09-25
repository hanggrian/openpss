package com.wijayaprinting.javafx

import com.wijayaprinting.mysql.dao.Staff
import javafx.stage.Stage

/**
 * @author Hendra Anggrian (hendraanggrian@gmail.com)
 */
class AppTest : WijayaPrintingApp() {

    companion object {
        @JvmStatic
        fun main(vararg args: String) = launch(AppTest::class.java, *args)
    }

    override val requiredStaffLevel: Int get() = Staff.LEVEL_EMPLOYEE

    override fun launch(stage: Stage) {

    }
}