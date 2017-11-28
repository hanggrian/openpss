package com.wijayaprinting.javafx.io

import com.wijayaprinting.javafx.Language

open class PreferencesFile : WPFile("preferences",
        Pair(LANGUAGE, Language.LOCALE_EN)
) {
    companion object : PreferencesFile() {
        const val LANGUAGE = "language"
        const val RECORD_AFFECTION = "record_affection"
    }
}