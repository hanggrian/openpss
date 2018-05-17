package com.hendraanggrian.openpss.controls

import com.hendraanggrian.openpss.internationalization.Language
import javafx.scene.control.ChoiceBox
import ktfx.collections.toObservableList

class LanguageBox(prefill: Language) : ChoiceBox<Language>(Language.values().toObservableList()) {

    constructor(prefill: String) : this(Language.of(prefill))

    init {
        maxWidth = Double.MAX_VALUE
        selectionModel.select(prefill)
    }
}