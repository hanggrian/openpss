package com.hendraanggrian.openpss.ui.price

import com.hendraanggrian.openpss.R
import com.hendraanggrian.openpss.content.FxComponent
import com.hendraanggrian.openpss.db.schemas.OffsetPrice
import javafx.beans.value.ObservableValue
import kotlinx.coroutines.CoroutineScope
import ktfx.beans.property.asReadOnlyProperty
import ktfx.coroutines.onEditCommit
import ktfx.listeners.textFieldCellFactory

@Suppress("UNCHECKED_CAST")
class EditOffsetPriceDialog(
    component: FxComponent
) : EditPriceDialog<OffsetPrice>(component, R.string.offset_print_price) {

    init {
        getString(R.string.min_qty)<Int> {
            minWidth = 128.0
            style = "-fx-alignment: center-right;"
            setCellValueFactory { it.value.minQty.asReadOnlyProperty() as ObservableValue<Int> }
            textFieldCellFactory {
                fromString { it.toIntOrNull() ?: 0 }
            }
            onEditCommit { cell ->
                val offset = cell.rowValue
                if (api.editOffsetPrice(offset.name, cell.newValue, offset.minPrice, offset.excessPrice)) {
                    cell.rowValue.minQty = cell.newValue
                }
            }
        }

        getString(R.string.min_price)<Double> {
            minWidth = 128.0
            style = "-fx-alignment: center-right;"
            setCellValueFactory { it.value.minPrice.asReadOnlyProperty() as ObservableValue<Double> }
            textFieldCellFactory {
                fromString { it.toDoubleOrNull() ?: 0.0 }
            }
            onEditCommit { cell ->
                val offset = cell.rowValue
                if (api.editOffsetPrice(offset.name, offset.minQty, cell.newValue, offset.excessPrice)) {
                    cell.rowValue.minPrice = cell.newValue
                }
            }
        }

        getString(R.string.excess_price)<Double> {
            minWidth = 128.0
            style = "-fx-alignment: center-right;"
            setCellValueFactory { it.value.excessPrice.asReadOnlyProperty() as ObservableValue<Double> }
            textFieldCellFactory {
                fromString { it.toDoubleOrNull() ?: 0.0 }
            }
            onEditCommit { cell ->
                val offset = cell.rowValue
                if (api.editOffsetPrice(offset.name, offset.minQty, offset.minPrice, cell.newValue)) {
                    cell.rowValue.excessPrice = cell.newValue
                }
            }
        }
    }

    override suspend fun CoroutineScope.refresh(): List<OffsetPrice> = api.getOffsetPrices()

    override suspend fun CoroutineScope.add(name: String): OffsetPrice? = api.addOffsetPrice(name)

    override suspend fun CoroutineScope.delete(selected: OffsetPrice): Boolean =
        api.deleteOffsetPrice(selected.name)
}