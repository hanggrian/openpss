package com.hendraanggrian.openpss.ui.finance

import com.hendraanggrian.openpss.R
import com.hendraanggrian.openpss.controls.Popup
import com.hendraanggrian.openpss.resources.Resourced
import com.hendraanggrian.openpss.util.currencyConverter
import javafx.scene.Node
import ktfx.layouts.gridPane
import ktfx.layouts.label
import ktfx.scene.layout.gap

class ViewTotalPopup(
    resourced: Resourced,
    private val cash: Double,
    private val others: Double
) : Popup<Nothing>(resourced, R.string.view_total) {

    override val content: Node
        get() = gridPane {
            gap = 8.0
            label(getString(R.string.cash)) col 0 row 0
            label(currencyConverter.toString(cash)) col 1 row 0
            label(getString(R.string.others)) col 0 row 1
            label(currencyConverter.toString(others)) col 1 row 1
            label(getString(R.string.total)) col 0 row 2
            label(currencyConverter.toString(cash + others)) col 1 row 2
        }
}