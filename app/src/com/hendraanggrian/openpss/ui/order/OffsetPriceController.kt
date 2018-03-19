package com.hendraanggrian.openpss.ui.order

import com.hendraanggrian.openpss.db.schema.Offset
import com.hendraanggrian.openpss.db.schema.Offsets
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

class OffsetPriceController : PriceController<Offset, Offsets>(Offsets) {

    @FXML lateinit var minAmountColumn: TableColumn<Offset, Int>
    @FXML lateinit var minPriceColumn: TableColumn<Offset, Double>
    @FXML lateinit var excessPriceColumn: TableColumn<Offset, Double>

    override fun initialize(location: URL, resources: ResourceBundle) {
        super.initialize(location, resources)
        minAmountColumn.setCellValueFactory { it.value.minAmount.toProperty().asObservable() }
        minAmountColumn.textFieldCellFactory {
            fromString { it.toIntOrNull() ?: 0 }
        }
        minAmountColumn.onEditCommit { event ->
            transaction {
                Offsets.find { name.equal(event.rowValue.name) }.projection { minAmount }.update(event.newValue)
            }
            event.rowValue.minAmount = event.newValue
        }

        minPriceColumn.setCellValueFactory { it.value.minPrice.toProperty().asObservable() }
        minPriceColumn.textFieldCellFactory {
            fromString { it.toDoubleOrNull() ?: 0.0 }
        }
        minPriceColumn.onEditCommit { event ->
            transaction {
                Offsets.find { name.equal(event.rowValue.name) }.projection { minPrice }.update(event.newValue)
            }
            event.rowValue.minPrice = event.newValue
        }

        excessPriceColumn.setCellValueFactory { it.value.excessPrice.toProperty().asObservable() }
        excessPriceColumn.textFieldCellFactory {
            fromString { it.toDoubleOrNull() ?: 0.0 }
        }
        excessPriceColumn.onEditCommit { event ->
            transaction {
                Offsets.find { name.equal(event.rowValue.name) }.projection { excessPrice }.update(event.newValue)
            }
            event.rowValue.excessPrice = event.newValue
        }
    }

    override fun newPrice(name: String): Offset = Offset(name)
}