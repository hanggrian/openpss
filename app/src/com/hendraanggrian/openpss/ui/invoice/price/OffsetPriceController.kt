package com.hendraanggrian.openpss.ui.invoice.price

import com.hendraanggrian.openpss.db.schemas.OffsetPrice
import com.hendraanggrian.openpss.db.schemas.OffsetPrices
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

class OffsetPriceController : PriceController<OffsetPrice, OffsetPrices>(OffsetPrices) {

    @FXML lateinit var minQtyColumn: TableColumn<OffsetPrice, Int>
    @FXML lateinit var minPriceColumn: TableColumn<OffsetPrice, Double>
    @FXML lateinit var excessPriceColumn: TableColumn<OffsetPrice, Double>

    override fun initialize(location: URL, resources: ResourceBundle) {
        super.initialize(location, resources)
        minQtyColumn.setCellValueFactory { it.value.minQty.toProperty().asObservable() }
        minQtyColumn.textFieldCellFactory {
            fromString { it.toIntOrNull() ?: 0 }
        }
        minQtyColumn.onEditCommit { cell ->
            transaction {
                OffsetPrices.find { it.name.equal(cell.rowValue.name) }.projection { minQty }.update(cell.newValue)
            }
            cell.rowValue.minQty = cell.newValue
        }

        minPriceColumn.setCellValueFactory { it.value.minPrice.toProperty().asObservable() }
        minPriceColumn.textFieldCellFactory {
            fromString { it.toDoubleOrNull() ?: 0.0 }
        }
        minPriceColumn.onEditCommit { cell ->
            transaction {
                OffsetPrices.find { it.name.equal(cell.rowValue.name) }.projection { minPrice }.update(cell.newValue)
            }
            cell.rowValue.minPrice = cell.newValue
        }

        excessPriceColumn.setCellValueFactory { it.value.excessPrice.toProperty().asObservable() }
        excessPriceColumn.textFieldCellFactory {
            fromString { it.toDoubleOrNull() ?: 0.0 }
        }
        excessPriceColumn.onEditCommit { cell ->
            transaction {
                OffsetPrices.find { it.name.equal(cell.rowValue.name) }.projection { excessPrice }.update(cell.newValue)
            }
            cell.rowValue.excessPrice = cell.newValue
        }
    }

    override fun newPrice(name: String): OffsetPrice = OffsetPrice.new(name)
}