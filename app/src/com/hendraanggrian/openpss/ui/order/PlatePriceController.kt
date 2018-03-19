package com.hendraanggrian.openpss.ui.order

import com.hendraanggrian.openpss.db.schema.Plate
import com.hendraanggrian.openpss.db.schema.Plates
import com.hendraanggrian.openpss.db.transaction
import javafx.fxml.FXML
import javafx.scene.control.TableColumn
import kotlinx.nosql.equal
import kotlinx.nosql.update
import ktfx.beans.property.asObservable
import ktfx.beans.property.toProperty
import ktfx.coroutines.onEditCommit
import ktfx.listeners.textFieldCellFactory
import java.net.URL
import java.util.ResourceBundle

class PlatePriceController : PriceController<Plate, Plates>(Plates) {

    @FXML lateinit var priceColumn: TableColumn<Plate, Double>

    override fun initialize(location: URL, resources: ResourceBundle) {
        super.initialize(location, resources)
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