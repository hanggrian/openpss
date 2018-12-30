package com.hendraanggrian.openpss.ui.price

import com.hendraanggrian.openpss.R
import com.hendraanggrian.openpss.data.DigitalPrice
import com.hendraanggrian.openpss.ui.FxComponent
import javafx.beans.value.ObservableValue
import kotlinx.coroutines.CoroutineScope
import ktfx.coroutines.onEditCommit
import ktfx.finalDouble
import ktfx.listeners.textFieldCellFactory

@Suppress("UNCHECKED_CAST")
class EditDigitalPriceDialog(
    component: FxComponent
) : EditPriceDialog<DigitalPrice>(component, R.string.digital_print_price) {

    init {
        getString(R.string.one_side_price)<Double> {
            minWidth = 128.0
            style = "-fx-alignment: center-right;"
            setCellValueFactory { finalDouble(it.value.oneSidePrice) as ObservableValue<Double> }
            textFieldCellFactory {
                fromString { it.toDoubleOrNull() ?: 0.0 }
            }
            onEditCommit { cell ->
                val digital = cell.rowValue
                api.editDigitalPrice(digital.apply { oneSidePrice = cell.newValue })
            }
        }
        getString(R.string.two_side_price)<Double> {
            minWidth = 128.0
            style = "-fx-alignment: center-right;"
            setCellValueFactory { finalDouble(it.value.twoSidePrice) as ObservableValue<Double> }
            textFieldCellFactory {
                fromString { it.toDoubleOrNull() ?: 0.0 }
            }
            onEditCommit { cell ->
                val digital = cell.rowValue
                api.editDigitalPrice(digital.apply { twoSidePrice = cell.newValue })
            }
        }
    }

    override suspend fun CoroutineScope.refresh(): List<DigitalPrice> = api.getDigitalPrices()

    override suspend fun CoroutineScope.add(name: String): DigitalPrice? = api.addDigitalPrice(DigitalPrice.new(name))

    override suspend fun CoroutineScope.delete(selected: DigitalPrice): Boolean = api.deleteDigitalPrice(selected.id)
}