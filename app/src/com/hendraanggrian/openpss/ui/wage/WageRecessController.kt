package com.hendraanggrian.openpss.ui.wage

import com.hendraanggrian.openpss.R
import com.hendraanggrian.openpss.db.schemas.Recess
import com.hendraanggrian.openpss.db.schemas.Recesses
import com.hendraanggrian.openpss.db.transaction
import com.hendraanggrian.openpss.layouts.TimeBox
import com.hendraanggrian.openpss.layouts.timeBox
import com.hendraanggrian.openpss.time.PATTERN_TIME
import com.hendraanggrian.openpss.ui.SimpleTableController
import com.hendraanggrian.openpss.utils.stringCell
import com.hendraanggrian.openpss.utils.style
import javafx.fxml.FXML
import javafx.scene.control.ButtonType.OK
import javafx.scene.control.TableColumn
import javafx.scene.image.ImageView
import ktfx.beans.binding.booleanBindingOf
import ktfx.layouts.gridPane
import ktfx.layouts.label
import ktfx.scene.control.cancelButton
import ktfx.scene.control.dialog
import ktfx.scene.control.okButton
import ktfx.scene.layout.gap
import org.joda.time.LocalTime
import java.net.URL
import java.util.ResourceBundle

class WageRecessController : SimpleTableController<Recess, Recesses>(Recesses) {

    @FXML lateinit var startColumn: TableColumn<Recess, String>
    @FXML lateinit var endColumn: TableColumn<Recess, String>

    override fun initialize(location: URL, resources: ResourceBundle) {
        super.initialize(location, resources)
        startColumn.stringCell { start.toString(PATTERN_TIME) }
        endColumn.stringCell { end.toString(PATTERN_TIME) }
    }

    override fun add() = dialog<Pair<LocalTime, LocalTime>>(getString(R.string.add_reccess),
        ImageView(R.image.ic_time)) {
        lateinit var startBox: TimeBox
        lateinit var endBox: TimeBox
        style()
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
        setResultConverter { if (it == OK) startBox.value to endBox.value else null }
    }.showAndWait().ifPresent { (start, end) ->
        val recess = Recess(start, end)
        recess.id = transaction { Recesses.insert(recess) }!!
        table.items.add(recess)
    }
}