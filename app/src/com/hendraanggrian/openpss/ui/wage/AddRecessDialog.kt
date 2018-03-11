package com.hendraanggrian.openpss.ui.wage

import com.hendraanggrian.openpss.R
import com.hendraanggrian.openpss.scene.layout.TimeBox
import com.hendraanggrian.openpss.scene.layout.timeBox
import com.hendraanggrian.openpss.ui.Resourced
import javafx.scene.control.ButtonType.OK
import javafx.scene.control.Dialog
import javafx.scene.image.ImageView
import kfx.beans.binding.booleanBindingOf
import kfx.layouts.gridPane
import kfx.layouts.label
import kfx.scene.control.cancelButton
import kfx.scene.control.graphicIcon
import kfx.scene.control.headerTitle
import kfx.scene.control.okButton
import kfx.scene.layout.gaps
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