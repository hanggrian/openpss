package com.hanggrian.openpss.ui.finance

import com.hanggrian.openpss.Context
import com.hanggrian.openpss.R
import com.hanggrian.openpss.popup.popover.Popover
import ktfx.controls.H_RIGHT
import ktfx.controls.columnConstraints
import ktfx.layouts.gridPane
import ktfx.layouts.label
import ktfx.layouts.styledLabel
import ktfx.text.invoke

class ViewTotalPopover(context: Context, private val cash: Double, private val nonCash: Double) :
    Popover(context, R.string_view_total) {
    init {
        gridPane {
            columnConstraints {
                append()
                append { halignment = H_RIGHT }
            }
            vgap = 20.0
            hgap = 40.0
            label(getString(R.string_cash))
                .grid(0, 0)
            label(currencyConverter(cash))
                .grid(0, 1)
            label(getString(R.string_non_cash))
                .grid(1, 0)
            label(currencyConverter(nonCash))
                .grid(1, 1)
            styledLabel(getString(R.string_total), null, R.style_bold)
                .grid(2, 0)
            styledLabel(currencyConverter(cash + nonCash), null, R.style_bold)
                .grid(2, 1)
        }
    }
}
