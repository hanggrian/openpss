package com.hendraanggrian.openpss.controls

import com.hendraanggrian.openpss.io.properties.LoginFile
import javafx.scene.control.ChoiceBox
import ktfx.collections.observableListOf
import ktfx.listeners.converter
import java.util.Locale

class LanguageBox : ChoiceBox<Locale>(observableListOf(Locale("en"), Locale("id"))) {

    init {
        maxWidth = Double.MAX_VALUE
        selectionModel.select(Locale(LoginFile.LANGUAGE))
        converter {
            toString { it!!.getDisplayLanguage(it) }
        }
    }
}