package com.hendraanggrian.openpss.ui.finance

import com.hendraanggrian.openpss.R
import com.hendraanggrian.openpss.control.popover.Popover
import com.hendraanggrian.openpss.i18n.Resourced
import com.hendraanggrian.openpss.util.bold
import com.hendraanggrian.openpss.util.currencyConverter
import javafx.geometry.HPos.RIGHT
import ktfx.layouts.columnConstraints
import ktfx.layouts.gridPane
import ktfx.layouts.label

class ViewTotalPopover(
    resourced: Resourced,
    private val cash: Double,
    private val credit: Double,
    private val debit: Double,
    private val cheque: Double,
    private val transfer: Double
) : Popover(resourced, R.string.view_total) {

    init {
        gridPane {
            columnConstraints {
                constraints()
                constraints { halignment = RIGHT }
            }
            vgap = 16.0
            hgap = 32.0
            label(getString(R.string.cash)) col 0 row 0
            label(currencyConverter.toString(cash)) col 1 row 0
            label(getString(R.string.credit_card)) col 0 row 1
            label(currencyConverter.toString(credit)) col 1 row 1
            label(getString(R.string.debit_card)) col 0 row 2
            label(currencyConverter.toString(debit)) col 1 row 2
            label(getString(R.string.cheque)) col 0 row 3
            label(currencyConverter.toString(cheque)) col 1 row 3
            label(getString(R.string.transfer)) col 0 row 4
            label(currencyConverter.toString(transfer)) col 1 row 4
            label(getString(R.string.total)) {
                font = bold()
            } col 0 row 5
            label(currencyConverter.toString(cash + credit + debit + cheque + transfer)) {
                font = bold()
            } col 1 row 5
        }
    }
}