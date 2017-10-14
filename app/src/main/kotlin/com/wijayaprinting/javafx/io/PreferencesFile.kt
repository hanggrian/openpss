package com.wijayaprinting.javafx.io

import com.wijayaprinting.javafx.Language

class PreferencesFile : WPFile("preferences",
        Pair(LANGUAGE, Language.LOCALE_EN)
) {
    companion object {
        const val LANGUAGE = "language"
    }
}