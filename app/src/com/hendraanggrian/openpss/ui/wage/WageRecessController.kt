package com.hendraanggrian.openpss.ui.wage

import com.hendraanggrian.openpss.db.schema.Recess
import com.hendraanggrian.openpss.db.schema.Recesses
import com.hendraanggrian.openpss.db.transaction
import com.hendraanggrian.openpss.time.PATTERN_TIME
import com.hendraanggrian.openpss.ui.SimpleTableController
import javafx.fxml.FXML
import javafx.scene.control.TableColumn
import kfx.beans.property.toProperty

class WageRecessController : SimpleTableController<Recess, Recesses>(Recesses) {

    @FXML lateinit var startColumn: TableColumn<Recess, String>
    @FXML lateinit var endColumn: TableColumn<Recess, String>

    override fun initialize() {
        super.initialize()
        startColumn.setCellValueFactory { it.value.start.toString(PATTERN_TIME).toProperty() }
        endColumn.setCellValueFactory { it.value.end.toString(PATTERN_TIME).toProperty() }
    }

    override fun add() = AddRecessDialog(this).showAndWait().ifPresent { (start, end) ->
        val recess = Recess(start, end)
        recess.id = transaction { Recesses.insert(recess) }!!
        table.items.add(recess)
    }
}