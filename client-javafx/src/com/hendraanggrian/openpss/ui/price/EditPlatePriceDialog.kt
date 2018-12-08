package com.hendraanggrian.openpss.ui.price

import com.hendraanggrian.openpss.App
import com.hendraanggrian.openpss.R
import com.hendraanggrian.openpss.content.FxComponent
import com.hendraanggrian.openpss.db.schemas.PlatePrice
import javafx.beans.value.ObservableValue
import kotlinx.coroutines.CoroutineScope
import ktfx.beans.property.asReadOnlyProperty
import ktfx.coroutines.onEditCommit
import ktfx.listeners.textFieldCellFactory

@Suppress("UNCHECKED_CAST")
class EditPlatePriceDialog(
    component: FxComponent
) : EditPriceDialog<PlatePrice>(component, R.string.plate_price) {

    init {
        getString(R.string.price)<Double> {
            minWidth = 128.0
            style = "-fx-alignment: center-right;"
            setCellValueFactory { it.value.price.asReadOnlyProperty() as ObservableValue<Double> }
            textFieldCellFactory {
                fromString { it.toDoubleOrNull() ?: 0.0 }
            }
            onEditCommit { cell ->
                val plate = cell.rowValue
                if (App.API.editPlatePrice(plate.name, cell.newValue)) {
                    cell.rowValue.price = cell.newValue
                }
            }
        }
    }

    override suspend fun CoroutineScope.refresh(): List<PlatePrice> = App.API.getPlatePrices()

    override suspend fun CoroutineScope.add(name: String): PlatePrice? = App.API.addPlatePrice(name)

    override suspend fun CoroutineScope.delete(selected: PlatePrice): Boolean = App.API.deletePlatePrice(selected.name)
}