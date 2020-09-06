package com.hendraanggrian.openpss.ui.finance

import com.hendraanggrian.openpss.R
import com.hendraanggrian.openpss.content.Context
import com.hendraanggrian.openpss.popup.popover.Popover
import ktfx.controls.H_RIGHT
import ktfx.controls.columnConstraints
import ktfx.layouts.gridPane
import ktfx.layouts.label
import ktfx.layouts.styledLabel
import ktfx.text.invoke

class ViewTotalPopover(
    context: Context,
    private val cash: Double,
    private val nonCash: Double
) : Popover(context, R.string.view_total) {

    init {
        gridPane {
            columnConstraints {
                append()
                append { halignment = H_RIGHT }
            }
            vgap = 20.0
            hgap = 40.0
            label(getString(R.string.cash)).grid(0, 0)
            label(currencyConverter(cash)).grid(0, 1)
            label(getString(R.string.non_cash)).grid(1, 0)
            label(currencyConverter(nonCash)).grid(1, 1)
            styledLabel(getString(R.string.total), null, R.style.bold).grid(2, 0)
            styledLabel(currencyConverter(cash + nonCash), null, R.style.bold).grid(2, 1)
        }
    }
}
