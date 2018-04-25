package com.hendraanggrian.openpss.ui.wage

import com.hendraanggrian.openpss.R
import com.hendraanggrian.openpss.layouts.TimeBox
import com.hendraanggrian.openpss.layouts.timeBox
import com.hendraanggrian.openpss.resources.Resourced
import com.hendraanggrian.openpss.util.style
import javafx.scene.control.ButtonType
import javafx.scene.control.Dialog
import javafx.scene.image.ImageView
import ktfx.beans.binding.booleanBindingOf
import ktfx.layouts.gridPane
import ktfx.layouts.label
import ktfx.scene.control.cancelButton
import ktfx.scene.control.graphicIcon
import ktfx.scene.control.headerTitle
import ktfx.scene.control.okButton
import ktfx.scene.layout.gap
import org.joda.time.LocalTime

class AddRecessDialog(resourced: Resourced) : Dialog<Pair<LocalTime, LocalTime>>(), Resourced by resourced {

    private lateinit var startBox: TimeBox
    private lateinit var endBox: TimeBox

    init {
        style()
        headerTitle = getString(R.string.add_reccess)
        graphicIcon = ImageView(R.image.header_time)
        dialogPane.content = gridPane {
            gap = 8.0
            label(getString(R.string.start)) col 0 row 0
            startBox = timeBox() col 1 row 0
            label(getString(R.string.end)) col 0 row 1
            endBox = timeBox() col 1 row 1
        }
        cancelButton()
        okButton().disableProperty().bind(booleanBindingOf(startBox.valueProperty, endBox.valueProperty) {
            startBox.value >= endBox.value
        })
        setResultConverter { if (it == ButtonType.OK) startBox.value to endBox.value else null }
    }
}