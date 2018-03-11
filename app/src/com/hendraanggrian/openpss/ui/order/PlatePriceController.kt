package com.hendraanggrian.openpss.ui.order

import com.hendraanggrian.openpss.db.schema.Plate
import com.hendraanggrian.openpss.db.schema.Plates
import com.hendraanggrian.openpss.db.transaction
import javafx.fxml.FXML
import javafx.scene.control.TableColumn
import kfx.beans.property.asObservable
import kfx.beans.property.toProperty
import kfx.coroutines.onEditCommit
import kfx.listeners.textFieldCellFactory
import kotlinx.nosql.equal
import kotlinx.nosql.update

class PlatePriceController : PriceController<Plate, Plates>(Plates) {

    @FXML lateinit var priceColumn: TableColumn<Plate, Double>

    override fun initialize() {
        super.initialize()
        priceColumn.setCellValueFactory { it.value.price.toProperty().asObservable() }
        priceColumn.textFieldCellFactory {
            fromString { it.toDoubleOrNull() ?: 0.0 }
        }
        priceColumn.onEditCommit { event ->
            transaction { Plates.find { name.equal(event.rowValue.name) }.projection { price }.update(event.newValue) }
            event.rowValue.price = event.newValue
        }
    }

    override fun newPrice(name: String): Plate = Plate(name)
}