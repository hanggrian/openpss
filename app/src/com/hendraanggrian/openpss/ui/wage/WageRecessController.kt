package com.hendraanggrian.openpss.ui.wage

import com.hendraanggrian.openpss.db.schemas.Recess
import com.hendraanggrian.openpss.db.schemas.Recesses
import com.hendraanggrian.openpss.db.transaction
import com.hendraanggrian.openpss.util.PATTERN_TIME
import com.hendraanggrian.openpss.ui.SimpleTableController
import com.hendraanggrian.openpss.util.stringCell
import javafx.fxml.FXML
import javafx.scene.control.TableColumn
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

    override fun add() = AddRecessDialog(this).showAndWait().ifPresent { (start, end) ->
        val recess = Recess(start, end)
        recess.id = transaction { Recesses.insert(recess) }!!
        table.items.add(recess)
    }
}