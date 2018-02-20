package com.wijayaprinting.ui.wage

import com.wijayaprinting.R
import com.wijayaprinting.scene.layout.TimeBox
import com.wijayaprinting.scene.layout.timeBox
import com.wijayaprinting.ui.Resourced
import javafx.scene.control.ButtonType.CANCEL
import javafx.scene.control.ButtonType.OK
import javafx.scene.control.Dialog
import javafx.scene.image.ImageView
import kotfx.bindings.booleanBindingOf
import kotfx.coroutines.resultConverter
import kotfx.dialogs.addButton
import kotfx.dialogs.content
import kotfx.dialogs.graphicIcon
import kotfx.dialogs.headerTitle
import kotfx.gap
import kotfx.layout.gridPane
import kotfx.layout.label
import org.joda.time.LocalTime

class AddRecessDialog(resourced: Resourced) : Dialog<Pair<LocalTime, LocalTime>>(), Resourced by resourced {

    lateinit var startBox: TimeBox
    lateinit var endBox: TimeBox

    init {
        headerTitle = getString(R.string.add_reccess)
        graphicIcon = ImageView(R.image.ic_clock)

        content = gridPane {
            gap = 8.0
            label(getString(R.string.start)) col 0 row 0
            startBox = timeBox() col 1 row 0
            label(getString(R.string.end)) col 0 row 1
            endBox = timeBox() col 1 row 1
        }
        addButton(CANCEL)
        addButton(OK).disableProperty().bind(booleanBindingOf(startBox.timeProperty, endBox.timeProperty) { startBox.time >= endBox.time })
        resultConverter { if (it == OK) Pair(startBox.time, endBox.time) else null }
    }
}