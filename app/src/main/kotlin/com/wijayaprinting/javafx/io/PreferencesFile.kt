package com.wijayaprinting.javafx.io

import com.wijayaprinting.javafx.Language
import com.wijayaprinting.javafx.controller.AttendanceRecordController

open class PreferencesFile : WPFile("preferences",
        Pair(LANGUAGE, Language.LOCALE_EN),
        Pair(RECORD_AFFECTION, AttendanceRecordController.AFFECT_DAILY.toString())
) {
    companion object : PreferencesFile() {
        const val LANGUAGE = "language"
        const val RECORD_AFFECTION = "record_affection"
    }
}