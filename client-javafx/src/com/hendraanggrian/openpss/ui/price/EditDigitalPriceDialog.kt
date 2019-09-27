package com.hendraanggrian.openpss.ui.price

import com.hendraanggrian.openpss.FxComponent
import com.hendraanggrian.openpss.R2
import com.hendraanggrian.openpss.data.DigitalPrice
import javafx.beans.value.ObservableValue
import kotlinx.coroutines.CoroutineScope
import ktfx.asFinalProperty
import ktfx.coroutines.onEditCommit
import ktfx.listeners.textFieldCellFactory

@Suppress("UNCHECKED_CAST")
class EditDigitalPriceDialog(
    component: FxComponent
) : EditPriceDialog<DigitalPrice>(component, R2.string.digital_print_price) {

    init {
        getString(R2.string.one_side_price)<Double> {
            minWidth = 128.0
            style = "-fx-alignment: center-right;"
            setCellValueFactory {
                it.value.oneSidePrice.asFinalProperty() as ObservableValue<Double>
            }
            textFieldCellFactory {
                fromString { it.toDoubleOrNull() ?: 0.0 }
            }
            onEditCommit { cell ->
                val digital = cell.rowValue
                api.editDigitalPrice(digital.apply { oneSidePrice = cell.newValue })
            }
        }
        getString(R2.string.two_side_price)<Double> {
            minWidth = 128.0
            style = "-fx-alignment: center-right;"
            setCellValueFactory {
                it.value.twoSidePrice.asFinalProperty() as ObservableValue<Double>
            }
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

    override suspend fun CoroutineScope.add(name: String): DigitalPrice? =
        api.addDigitalPrice(DigitalPrice.new(name))

    override suspend fun CoroutineScope.delete(selected: DigitalPrice): Boolean =
        api.deleteDigitalPrice(selected.id)
}
