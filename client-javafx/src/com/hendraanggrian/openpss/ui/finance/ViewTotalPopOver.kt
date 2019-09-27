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
            label(getString(R2.string.cash)) col 0 row 0
            label(currencyConverter(cash)) col 1 row 0
            label(getString(R2.string.non_cash)) col 0 row 1
            label(currencyConverter(nonCash)) col 1 row 1
            label(getString(R2.string.total)) {
                styleClass += R.style.bold
            } col 0 row 2
            label(currencyConverter(cash + nonCash)) {
                styleClass += R.style.bold
            } col 1 row 2
        }
    }
}
