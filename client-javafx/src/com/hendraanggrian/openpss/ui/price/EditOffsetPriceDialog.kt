package com.hendraanggrian.openpss.ui.price

import com.hendraanggrian.openpss.R2
import com.hendraanggrian.openpss.data.OffsetPrice
import com.hendraanggrian.openpss.FxComponent
import javafx.beans.value.ObservableValue
import kotlinx.coroutines.CoroutineScope
import ktfx.coroutines.onEditCommit
import ktfx.finalDouble
import ktfx.finalInt
import ktfx.listeners.textFieldCellFactory

@Suppress("UNCHECKED_CAST")
class EditOffsetPriceDialog(
    component: FxComponent
) : EditPriceDialog<OffsetPrice>(component, R2.string.offset_print_price) {

    init {
        getString(R2.string.min_qty)<Int> {
            minWidth = 128.0
            style = "-fx-alignment: center-right;"
            setCellValueFactory { finalInt(it.value.minQty) as ObservableValue<Int> }
            textFieldCellFactory {
                fromString { it.toIntOrNull() ?: 0 }
            }
            onEditCommit { cell ->
                val offset = cell.rowValue
                api.editOffsetPrice(offset.apply { minQty = cell.newValue })
            }
        }

        getString(R2.string.min_price)<Double> {
            minWidth = 128.0
            style = "-fx-alignment: center-right;"
            setCellValueFactory { finalDouble(it.value.minPrice) as ObservableValue<Double> }
            textFieldCellFactory {
                fromString { it.toDoubleOrNull() ?: 0.0 }
            }
            onEditCommit { cell ->
                val offset = cell.rowValue
                api.editOffsetPrice(offset.apply { minPrice = cell.newValue })
            }
        }

        getString(R2.string.excess_price)<Double> {
            minWidth = 128.0
            style = "-fx-alignment: center-right;"
            setCellValueFactory { finalDouble(it.value.excessPrice) as ObservableValue<Double> }
            textFieldCellFactory {
                fromString { it.toDoubleOrNull() ?: 0.0 }
            }
            onEditCommit { cell ->
                val offset = cell.rowValue
                api.editOffsetPrice(offset.apply { excessPrice = cell.newValue })
            }
        }
    }

    override suspend fun CoroutineScope.refresh(): List<OffsetPrice> = api.getOffsetPrices()

    override suspend fun CoroutineScope.add(name: String): OffsetPrice? =
        api.addOffsetPrice(OffsetPrice.new(name))

    override suspend fun CoroutineScope.delete(selected: OffsetPrice): Boolean =
        api.deleteOffsetPrice(selected.id)
}