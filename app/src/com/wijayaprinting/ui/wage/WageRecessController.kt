package com.wijayaprinting.ui.wage

import com.wijayaprinting.db.dao.Recess
import com.wijayaprinting.db.schema.Recesses
import com.wijayaprinting.db.transaction
import com.wijayaprinting.scene.PATTERN_TIME
import com.wijayaprinting.ui.SimpleTableController
import javafx.fxml.FXML
import javafx.scene.control.TableColumn
import kotfx.properties.toProperty

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