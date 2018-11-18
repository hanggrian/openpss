package com.hendraanggrian.openpss.ui.finance

import com.hendraanggrian.openpss.R
import com.hendraanggrian.openpss.content.Context
import com.hendraanggrian.openpss.content.currencyConverter
import com.hendraanggrian.openpss.popup.popover.Popover
import javafx.geometry.HPos
import ktfx.layouts.columnConstraints
import ktfx.layouts.gridPane
import ktfx.layouts.label
import ktfx.util.invoke

class ViewTotalPopover(
    context: Context,
    private val cash: Double,
    private val nonCash: Double
) : Popover(context, R.string.view_total) {

    init {
        gridPane {
            columnConstraints {
                constraints()
                constraints { halignment = HPos.RIGHT }
            }
            vgap = 20.0
            hgap = 40.0
            label(getString(R.string.cash)) col 0 row 0
            label(currencyConverter(cash)) col 1 row 0
            label(getString(R.string.non_cash)) col 0 row 1
            label(currencyConverter(nonCash)) col 1 row 1
            label(getString(R.string.total)) {
                styleClass += R.style.bold
            } col 0 row 2
            label(currencyConverter(cash + nonCash)) {
                styleClass += R.style.bold
            } col 1 row 2
        }
    }
}