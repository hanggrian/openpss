package com.hendraanggrian.openpss.controls

import com.hendraanggrian.openpss.io.properties.LoginFile
import javafx.scene.control.ChoiceBox
import ktfx.collections.observableListOf
import ktfx.listeners.converter
import java.util.Locale

class LanguageBox : ChoiceBox<String>(observableListOf("en", "id")) {

    init {
        maxWidth = Double.MAX_VALUE
        selectionModel.select(LoginFile.LANGUAGE)
        converter {
            toString { Locale(it!!).let { it.getDisplayLanguage(it) } }
        }
    }
}