package com.wijayaprinting.javafx.io

import com.wijayaprinting.javafx.Language

/**
 * @author Hendra Anggrian (hendraanggrian@gmail.com)
 */
class JavaFXFile : WPFile("javafx",
        Pair(LANGUAGE, Language.LOCALE_EN),
        Pair(USERNAME, ""),
        Pair(IP, ""),
        Pair(PORT, "")
) {
    companion object {
        const val LANGUAGE = "language"
        const val USERNAME = "username"
        const val IP = "ip"
        const val PORT = "port"
    }
}