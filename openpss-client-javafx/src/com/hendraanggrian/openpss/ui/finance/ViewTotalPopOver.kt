package com.hendraanggrian.openpss.ui.finance

import com.hendraanggrian.openpss.FxComponent
import com.hendraanggrian.openpss.R
import com.hendraanggrian.openpss.R2
import com.hendraanggrian.openpss.ui.BasePopOver
import javafx.geometry.HPos
import ktfx.invoke
import ktfx.layouts.columnConstraints
import ktfx.layouts.gridPane
import ktfx.layouts.label

class ViewTotalPopOver(
    component: FxComponent,
    private val cash: Double,
    private val nonCash: Double
) : BasePopOver(component, R2.string.view_total) {

    init {
        gridPane {
            columnConstraints {
                constraints()
                constraints { halignment = HPos.RIGHT }
            }
            vgap = 20.0
            hgap = 40.0
            label(getString(R2.string.cash)) {
                gridAt(0, 0)
            }
            label(currencyConverter(cash)) {
                gridAt(0, 1)
            }
            label(getString(R2.string.non_cash)) {
                gridAt(1, 0)
            }
            label(currencyConverter(nonCash)) {
                gridAt(1, 1)
            }
            label(getString(R2.string.total)) {
                gridAt(2, 0)
                styleClass += R.style.bold
            }
            label(currencyConverter(cash + nonCash)) {
                gridAt(2, 1)
                styleClass += R.style.bold
            }
        }
    }
}
