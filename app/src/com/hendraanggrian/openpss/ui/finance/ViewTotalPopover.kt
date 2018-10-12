package com.hendraanggrian.openpss.ui.finance

import com.hendraanggrian.openpss.R
import com.hendraanggrian.openpss.control.bold
import com.hendraanggrian.openpss.control.popover.Popover
import com.hendraanggrian.openpss.currencyConverter
import com.hendraanggrian.openpss.i18n.Resourced
import javafx.geometry.HPos.RIGHT
import ktfx.layouts.columnConstraints
import ktfx.layouts.gridPane
import ktfx.layouts.label
import ktfx.util.invoke

class ViewTotalPopover(
    resourced: Resourced,
    cash: Double,
    nonCash: Double
) : Popover(resourced, R.string.view_total) {

    init {
        gridPane {
            columnConstraints {
                constraints()
                constraints { halignment = RIGHT }
            }
            vgap = 20.0
            hgap = 40.0
            label(getString(R.string.cash)) col 0 row 0
            label(currencyConverter(cash)) col 1 row 0
            label(getString(R.string.non_cash)) col 0 row 1
            label(currencyConverter(nonCash)) col 1 row 1
            label(getString(R.string.total)) {
                font = bold()
            } col 0 row 2
            label(currencyConverter(cash + nonCash)) {
                font = bold()
            } col 1 row 2
        }
    }
}