package com.hendraanggrian.openpss.ui.price

import com.hendraanggrian.openpss.FxComponent
import com.hendraanggrian.openpss.R2
import com.hendraanggrian.openpss.api.OpenPSSApi
import com.hendraanggrian.openpss.schema.OffsetPrice
import javafx.beans.value.ObservableValue
import kotlinx.coroutines.CoroutineScope
import ktfx.buildStringConverter
import ktfx.cells.textFieldCellFactory
import ktfx.coroutines.onEditCommit
import ktfx.finalDoubleProperty
import ktfx.finalIntProperty

@Suppress("UNCHECKED_CAST")
class EditOffsetPriceDialog(
    component: FxComponent
) : EditPriceDialog<OffsetPrice>(component, R2.string.offset_print_price) {

    init {
        getString(R2.string.min_qty)<Int> {
            minWidth = 128.0
            style = "-fx-alignment: center-right;"
            setCellValueFactory {
                finalIntProperty(it.value.minQty) as ObservableValue<Int>
            }
            textFieldCellFactory(buildStringConverter {
                fromString { it.toIntOrNull() ?: 0 }
            })
            onEditCommit { cell ->
                val offset = cell.rowValue
                OpenPSSApi.editOffsetPrice(offset.apply { minQty = cell.newValue })
            }
        }

        getString(R2.string.min_price)<Double> {
            minWidth = 128.0
            style = "-fx-alignment: center-right;"
            setCellValueFactory {
                finalDoubleProperty(it.value.minPrice) as ObservableValue<Double>
            }
            textFieldCellFactory(buildStringConverter {
                fromString { it.toDoubleOrNull() ?: 0.0 }
            })
            onEditCommit { cell ->
                val offset = cell.rowValue
                OpenPSSApi.editOffsetPrice(offset.apply { minPrice = cell.newValue })
            }
        }

        getString(R2.string.excess_price)<Double> {
            minWidth = 128.0
            style = "-fx-alignment: center-right;"
            setCellValueFactory {
                finalDoubleProperty(it.value.excessPrice) as ObservableValue<Double>
            }
            textFieldCellFactory(buildStringConverter {
                fromString { it.toDoubleOrNull() ?: 0.0 }
            })
            onEditCommit { cell ->
                val offset = cell.rowValue
                OpenPSSApi.editOffsetPrice(offset.apply { excessPrice = cell.newValue })
            }
        }
    }

    override suspend fun CoroutineScope.refresh(): List<OffsetPrice> = OpenPSSApi.getOffsetPrices()

    override suspend fun CoroutineScope.add(name: String): OffsetPrice? =
        OpenPSSApi.addOffsetPrice(OffsetPrice.new(name))

    override suspend fun CoroutineScope.delete(selected: OffsetPrice): Boolean =
        OpenPSSApi.deleteOffsetPrice(selected.id)
}
