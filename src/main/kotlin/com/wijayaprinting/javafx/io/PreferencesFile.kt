package com.wijayaprinting.javafx.io

import com.wijayaprinting.javafx.Language
import javafx.beans.property.SimpleStringProperty

/**
 * @author Hendra Anggrian (hendraanggrian@gmail.com)
 */
class PreferencesFile : PropertiesFile(".preferences") {

    companion object {
        private const val LANGUAGE = "language"
    }

    val language = SimpleStringProperty(getString(LANGUAGE, Language.LOCALE_EN))

    override fun save(comments: String?) {
        setString(LANGUAGE, language.value)
        store(comments)
    }
}