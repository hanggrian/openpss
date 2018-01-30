package com.wijayaprinting.ui.wage

import com.wijayaprinting.R
import com.wijayaprinting.ui.Resourced
import com.wijayaprinting.ui.gap
import com.wijayaprinting.ui.scene.layout.TimeBox
import com.wijayaprinting.ui.scene.layout.timeBox
import javafx.scene.control.ButtonType.CANCEL
import javafx.scene.control.ButtonType.OK
import javafx.scene.control.Dialog
import javafx.scene.image.Image
import javafx.scene.image.ImageView
import kotfx.*
import org.joda.time.LocalTime

class AddRecessDialog(resourced: Resourced) : Dialog<Pair<LocalTime, LocalTime>>(), Resourced by resourced {

    lateinit var startBox: TimeBox
    lateinit var endBox: TimeBox

    init {
        icon = Image(R.image.ic_launcher)
        title = getString(R.string.add_reccess)
        headerText = getString(R.string.add_reccess)
        graphic = ImageView(R.image.ic_clock)

        content = gridPane {
            gap(8)
            label(getString(R.string.start)) col 0 row 0
            startBox = timeBox() col 1 row 0
            label(getString(R.string.end)) col 0 row 1
            endBox = timeBox() col 1 row 1
        }
        button(CANCEL)
        button(OK).disableProperty().bind(booleanBindingOf(startBox.timeProperty, endBox.timeProperty) { startBox.time >= endBox.time })
        setResultConverter { if (it == OK) Pair(startBox.time, endBox.time) else null }
    }
}