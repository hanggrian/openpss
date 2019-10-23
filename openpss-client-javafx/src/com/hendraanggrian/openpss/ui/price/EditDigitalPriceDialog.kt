package com.hendraanggrian.openpss.ui.price

import com.hendraanggrian.openpss.FxComponent
import com.hendraanggrian.openpss.R2
import com.hendraanggrian.openpss.api.OpenPSSApi
import com.hendraanggrian.openpss.schema.DigitalPrice
import javafx.beans.value.ObservableValue
import kotlinx.coroutines.CoroutineScope
import ktfx.asProperty
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
                it.value.oneSidePrice.asProperty(true) as ObservableValue<Double>
            }
            textFieldCellFactory {
                fromString { it.toDoubleOrNull() ?: 0.0 }
            }
            onEditCommit { cell ->
                val digital = cell.rowValue
                OpenPSSApi.editDigitalPrice(digital.apply { oneSidePrice = cell.newValue })
            }
        }
        getString(R2.string.two_side_price)<Double> {
            minWidth = 128.0
            style = "-fx-alignment: center-right;"
            setCellValueFactory {
                it.value.twoSidePrice.asProperty(true) as ObservableValue<Double>
            }
            textFieldCellFactory {
                fromString { it.toDoubleOrNull() ?: 0.0 }
            }
            onEditCommit { cell ->
                val digital = cell.rowValue
                OpenPSSApi.editDigitalPrice(digital.apply { twoSidePrice = cell.newValue })
            }
        }
    }

    override suspend fun CoroutineScope.refresh(): List<DigitalPrice> = OpenPSSApi.getDigitalPrices()

    override suspend fun CoroutineScope.add(name: String): DigitalPrice? =
        OpenPSSApi.addDigitalPrice(DigitalPrice.new(name))

    override suspend fun CoroutineScope.delete(selected: DigitalPrice): Boolean =
        OpenPSSApi.deleteDigitalPrice(selected.id)
}
