package com.hendraanggrian.openpss.controls

import com.hendraanggrian.openpss.Language
import javafx.scene.control.ChoiceBox
import ktfx.collections.toObservableList
import ktfx.listeners.converter

class LanguageBox(prefill: Language) : ChoiceBox<Language>(Language.values().toObservableList()) {

    constructor(prefill: String) : this(Language.from(prefill))

    init {
        maxWidth = Double.MAX_VALUE
        selectionModel.select(prefill)
        converter {
            toString {
                it!!.toLocale().let { it.getDisplayLanguage(it) }
            }
        }
    }
}