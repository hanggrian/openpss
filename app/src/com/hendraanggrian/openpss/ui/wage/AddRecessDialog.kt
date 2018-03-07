package com.hendraanggrian.openpss.ui.wage

import com.hendraanggrian.openpss.R
import com.hendraanggrian.openpss.scene.layout.TimeBox
import com.hendraanggrian.openpss.scene.layout.timeBox
import com.hendraanggrian.openpss.ui.Resourced
import javafx.scene.control.ButtonType.OK
import javafx.scene.control.Dialog
import javafx.scene.image.ImageView
import kotlinfx.beans.binding.booleanBindingOf
import kotlinfx.layouts.gridPane
import kotlinfx.layouts.label
import kotlinfx.scene.control.cancelButton
import kotlinfx.scene.control.graphicIcon
import kotlinfx.scene.control.headerTitle
import kotlinfx.scene.control.okButton
import kotlinfx.scene.layout.gaps
import org.joda.time.LocalTime

class AddRecessDialog(resourced: Resourced) : Dialog<Pair<LocalTime, LocalTime>>(), Resourced by resourced {

    lateinit var startBox: TimeBox
    lateinit var endBox: TimeBox

    init {
        headerTitle = getString(R.string.add_reccess)
        graphicIcon = ImageView(R.image.ic_clock)

        dialogPane.content = gridPane {
            gaps = 8
            label(getString(R.string.start)) col 0 row 0
            startBox = timeBox() col 1 row 0
            label(getString(R.string.end)) col 0 row 1
            endBox = timeBox() col 1 row 1
        }
        cancelButton()
        okButton {
            disableProperty().bind(booleanBindingOf(startBox.timeProperty, endBox.timeProperty) { startBox.time >= endBox.time })
        }
        setResultConverter { if (it == OK) Pair(startBox.time, endBox.time) else null }
    }
}