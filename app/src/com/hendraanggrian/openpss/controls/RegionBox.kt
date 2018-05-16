package com.hendraanggrian.openpss.controls

import com.hendraanggrian.openpss.internationalization.Region
import javafx.scene.control.ChoiceBox
import ktfx.collections.toObservableList

class RegionBox(prefill: Region) : ChoiceBox<Region>(Region.values().toObservableList()) {

    init {
        maxWidth = Double.MAX_VALUE
        selectionModel.select(prefill)
    }
}