package com.hendraanggrian.openpss.ui.price

import com.hendraanggrian.openpss.FxComponent
import com.hendraanggrian.openpss.R2
import com.hendraanggrian.openpss.schema.PlatePrice
import javafx.beans.value.ObservableValue
import kotlinx.coroutines.CoroutineScope
import ktfx.asFinalProperty
import ktfx.coroutines.onEditCommit
import ktfx.listeners.textFieldCellFactory

@Suppress("UNCHECKED_CAST")
class EditPlatePriceDialog(
    component: FxComponent
) : EditPriceDialog<PlatePrice>(component, R2.string.plate_price) {

    init {
        getString(R2.string.price)<Double> {
            minWidth = 128.0
            style = "-fx-alignment: center-right;"
            setCellValueFactory {
                it.value.price.asFinalProperty() as ObservableValue<Double>
            }
            textFieldCellFactory {
                fromString { it.toDoubleOrNull() ?: 0.0 }
            }
            onEditCommit { cell ->
                val plate = cell.rowValue
                api.editPlatePrice(plate.apply { price = cell.newValue })
            }
        }
    }

    override suspend fun CoroutineScope.refresh(): List<PlatePrice> = api.getPlatePrices()

    override suspend fun CoroutineScope.add(name: String): PlatePrice? =
        api.addPlatePrice(PlatePrice.new(name))

    override suspend fun CoroutineScope.delete(selected: PlatePrice): Boolean =
        api.deletePlatePrice(selected.id)
}
