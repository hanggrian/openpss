package com.hendraanggrian.openpss.ui.price

import com.hendraanggrian.openpss.FxComponent
import com.hendraanggrian.openpss.R2
import com.hendraanggrian.openpss.api.OpenPSSApi
import com.hendraanggrian.openpss.schema.PlatePrice
import javafx.beans.property.ReadOnlyDoubleWrapper
import javafx.beans.value.ObservableValue
import kotlinx.coroutines.CoroutineScope
import ktfx.cells.textFieldCellFactory
import ktfx.coroutines.onEditCommit
import ktfx.text.buildStringConverter

@Suppress("UNCHECKED_CAST")
class EditPlatePriceDialog(
    component: FxComponent
) : EditPriceDialog<PlatePrice>(component, R2.string.plate_price) {

    init {
        getString(R2.string.price)<Double> {
            minWidth = 128.0
            style = "-fx-alignment: center-right;"
            setCellValueFactory { ReadOnlyDoubleWrapper(it.value.price) as ObservableValue<Double> }
            textFieldCellFactory(buildStringConverter {
                fromString { it.toDoubleOrNull() ?: 0.0 }
            })
            onEditCommit { cell ->
                val plate = cell.rowValue
                OpenPSSApi.editPlatePrice(plate.apply { price = cell.newValue })
            }
        }
    }

    override suspend fun CoroutineScope.refresh(): List<PlatePrice> = OpenPSSApi.getPlatePrices()

    override suspend fun CoroutineScope.add(name: String): PlatePrice? =
        OpenPSSApi.addPlatePrice(PlatePrice.new(name))

    override suspend fun CoroutineScope.delete(selected: PlatePrice): Boolean =
        OpenPSSApi.deletePlatePrice(selected.id)
}
