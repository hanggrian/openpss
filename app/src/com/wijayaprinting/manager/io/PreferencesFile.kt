package com.wijayaprinting.manager.io

import com.wijayaprinting.manager.internal.Language

open class PreferencesFile : WPFile("preferences",
        Pair(LANGUAGE, Language.LOCALE_EN)
) {

    companion object : PreferencesFile() {
        const val LANGUAGE = "language"
        const val RECORD_AFFECTION = "record_affection"
    }
}