package com.hendraanggrian.openpss.ui.price

import com.hendraanggrian.openpss.App
import com.hendraanggrian.openpss.R
import com.hendraanggrian.openpss.content.FxComponent
import com.hendraanggrian.openpss.db.schemas.DigitalPrice
import javafx.beans.value.ObservableValue
import kotlinx.coroutines.CoroutineScope
import ktfx.beans.property.asReadOnlyProperty
import ktfx.coroutines.onEditCommit
import ktfx.listeners.textFieldCellFactory

@Suppress("UNCHECKED_CAST")
class EditDigitalPriceDialog(
    component: FxComponent
) : EditPriceDialog<DigitalPrice>(component, R.string.digital_print_price) {

    init {
        getString(R.string.one_side_price)<Double> {
            minWidth = 128.0
            style = "-fx-alignment: center-right;"
            setCellValueFactory { it.value.oneSidePrice.asReadOnlyProperty() as ObservableValue<Double> }
            textFieldCellFactory {
                fromString { it.toDoubleOrNull() ?: 0.0 }
            }
            onEditCommit { cell ->
                val digital = cell.rowValue
                if (App.API.editDigitalPrice(digital.name, cell.newValue, digital.twoSidePrice)) {
                    cell.rowValue.oneSidePrice = cell.newValue
                }
            }
        }
        getString(R.string.two_side_price)<Double> {
            minWidth = 128.0
            style = "-fx-alignment: center-right;"
            setCellValueFactory { it.value.twoSidePrice.asReadOnlyProperty() as ObservableValue<Double> }
            textFieldCellFactory {
                fromString { it.toDoubleOrNull() ?: 0.0 }
            }
            onEditCommit { cell ->
                val digital = cell.rowValue
                if (App.API.editDigitalPrice(digital.name, digital.oneSidePrice, cell.newValue)) {
                    cell.rowValue.twoSidePrice = cell.newValue
                }
            }
        }
    }

    override suspend fun CoroutineScope.refresh(): List<DigitalPrice> = App.API.getDigitalPrices()

    override suspend fun CoroutineScope.add(name: String): DigitalPrice? = App.API.addDigitalPrice(name)

    override suspend fun CoroutineScope.delete(selected: DigitalPrice): Boolean =
        App.API.deleteDigitalPrice(selected.name)
}