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
    private val totalCash: Double,
    private val totalTransfer: Double
) : Popup<Nothing>(resourced, R.string.view_total) {

    override val content: Node
        get() = gridPane {
            gap = 8.0
            label(getString(R.string.cash)) col 0 row 0
            label(currencyConverter.toString(totalCash)) col 1 row 0
            label(getString(R.string.cash)) col 0 row 1
            label(currencyConverter.toString(totalTransfer)) col 1 row 1
            label(getString(R.string.cash)) col 0 row 2
            label(currencyConverter.toString(totalCash + totalTransfer)) col 1 row 2
        }
}