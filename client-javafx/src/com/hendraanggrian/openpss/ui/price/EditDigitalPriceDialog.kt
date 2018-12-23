package com.hendraanggrian.openpss.ui.price

import com.hendraanggrian.openpss.R
import com.hendraanggrian.openpss.content.FxComponent
import com.hendraanggrian.openpss.data.DigitalPrice
import javafx.beans.value.ObservableValue
import kotlinx.coroutines.CoroutineScope
import ktfx.coroutines.onEditCommit
import ktfx.listeners.textFieldCellFactory
import ktfx.readOnlyDoublePropertyOf

@Suppress("UNCHECKED_CAST")
class EditDigitalPriceDialog(
    component: FxComponent
) : EditPriceDialog<DigitalPrice>(component, R.string.digital_print_price) {

    init {
        getString(R.string.one_side_price)<Double> {
            minWidth = 128.0
            style = "-fx-alignment: center-right;"
            setCellValueFactory { readOnlyDoublePropertyOf(it.value.oneSidePrice) as ObservableValue<Double> }
            textFieldCellFactory {
                fromString { it.toDoubleOrNull() ?: 0.0 }
            }
            onEditCommit { cell ->
                val digital = cell.rowValue
                if (api.editDigitalPrice(digital.id, cell.newValue, digital.twoSidePrice)) {
                    cell.rowValue.oneSidePrice = cell.newValue
                }
            }
        }
        getString(R.string.two_side_price)<Double> {
            minWidth = 128.0
            style = "-fx-alignment: center-right;"
            setCellValueFactory { readOnlyDoublePropertyOf(it.value.twoSidePrice) as ObservableValue<Double> }
            textFieldCellFactory {
                fromString { it.toDoubleOrNull() ?: 0.0 }
            }
            onEditCommit { cell ->
                val digital = cell.rowValue
                if (api.editDigitalPrice(digital.id, digital.oneSidePrice, cell.newValue)) {
                    cell.rowValue.twoSidePrice = cell.newValue
                }
            }
        }
    }

    override suspend fun CoroutineScope.refresh(): List<DigitalPrice> = api.getDigitalPrices()

    override suspend fun CoroutineScope.add(name: String): DigitalPrice? = api.addDigitalPrice(name)

    override suspend fun CoroutineScope.delete(selected: DigitalPrice): Boolean = api.deleteDigitalPrice(selected.id)
}