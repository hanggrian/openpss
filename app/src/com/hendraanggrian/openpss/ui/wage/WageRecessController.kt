package com.hendraanggrian.openpss.ui.wage

import com.hendraanggrian.openpss.R
import com.hendraanggrian.openpss.db.schema.Recess
import com.hendraanggrian.openpss.db.schema.Recesses
import com.hendraanggrian.openpss.db.transaction
import com.hendraanggrian.openpss.scene.layout.TimeBox
import com.hendraanggrian.openpss.scene.layout.timeBox
import com.hendraanggrian.openpss.time.PATTERN_TIME
import com.hendraanggrian.openpss.ui.SimpleTableController
import javafx.fxml.FXML
import javafx.scene.control.ButtonType.OK
import javafx.scene.control.TableColumn
import javafx.scene.image.ImageView
import kfx.beans.binding.booleanBindingOf
import kfx.beans.property.toProperty
import kfx.layouts.gridPane
import kfx.layouts.label
import kfx.scene.control.cancelButton
import kfx.scene.control.dialog
import kfx.scene.control.okButton
import kfx.scene.layout.gaps
import org.joda.time.LocalTime

class WageRecessController : SimpleTableController<Recess, Recesses>(Recesses) {

    @FXML lateinit var startColumn: TableColumn<Recess, String>
    @FXML lateinit var endColumn: TableColumn<Recess, String>

    override fun initialize() {
        super.initialize()
        startColumn.setCellValueFactory { it.value.start.toString(PATTERN_TIME).toProperty() }
        endColumn.setCellValueFactory { it.value.end.toString(PATTERN_TIME).toProperty() }
    }

    override fun add() = dialog<Pair<LocalTime, LocalTime>>(getString(R.string.add_reccess),
        ImageView(R.image.ic_clock)) {
        lateinit var startBox: TimeBox
        lateinit var endBox: TimeBox
        dialogPane.content = gridPane {
            gaps = 8
            label(getString(R.string.start)) col 0 row 0
            startBox = timeBox() col 1 row 0
            label(getString(R.string.end)) col 0 row 1
            endBox = timeBox() col 1 row 1
        }
        cancelButton()
        okButton {
            disableProperty().bind(booleanBindingOf(startBox.timeProperty, endBox.timeProperty) {
                startBox.time >= endBox.time
            })
        }
        setResultConverter { if (it == OK) Pair(startBox.time, endBox.time) else null }
    }.showAndWait().ifPresent { (start, end) ->
        val recess = Recess(start, end)
        recess.id = transaction { Recesses.insert(recess) }!!
        table.items.add(recess)
    }
}