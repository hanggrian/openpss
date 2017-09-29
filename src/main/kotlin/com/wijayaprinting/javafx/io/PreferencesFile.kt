package com.wijayaprinting.javafx.io

import com.wijayaprinting.javafx.Language

/**
 * @author Hendra Anggrian (hendraanggrian@gmail.com)
 */
class PreferencesFile : PropertiesFile("preferences",
        Pair(LANGUAGE, Language.LOCALE_EN)
) {
    companion object {
        const val LANGUAGE = "language"
    }
}