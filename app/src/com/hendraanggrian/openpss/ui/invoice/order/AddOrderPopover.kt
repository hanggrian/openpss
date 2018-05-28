package com.hendraanggrian.openpss.ui.invoice.order

import com.hendraanggrian.openpss.R
import com.hendraanggrian.openpss.control.IntField
import com.hendraanggrian.openpss.control.intField
import com.hendraanggrian.openpss.control.popover.ResultablePopover
import com.hendraanggrian.openpss.db.Order
import com.hendraanggrian.openpss.db.Titled
import com.hendraanggrian.openpss.i18n.Resourced
import com.hendraanggrian.openpss.util.bold
import com.hendraanggrian.openpss.util.currencyConverter
import com.hendraanggrian.openpss.util.getColor
import javafx.beans.Observable
import javafx.beans.value.ObservableBooleanValue
import javafx.scene.control.TextField
import ktfx.beans.binding.bindingOf
import ktfx.beans.binding.stringBindingOf
import ktfx.layouts._GridPane
import ktfx.layouts.gridPane
import ktfx.layouts.label
import ktfx.layouts.textField
import ktfx.scene.layout.gap

abstract class AddOrderPopover<T : Titled>(
    resourced: Resourced,
    titleId: String
) : ResultablePopover<T>(resourced, titleId), Order {

    abstract fun _GridPane.onLayout()

    abstract val totalBindingDependencies: Array<Observable>

    abstract val disableBinding: ObservableBooleanValue

    protected lateinit var titleField: TextField
    protected lateinit var qtyField: IntField

    init {
        gridPane {
            gap = 8.0
            label(getString(R.string.title)) col 0 row 0
            titleField = textField { promptText = getString(R.string.title) } col 1 row 0
            label(getString(R.string.qty)) col 0 row 1
            qtyField = intField { promptText = getString(R.string.qty) } col 1 row 1
            onLayout()
            (children.size / 2).let { totalRow ->
                label(getString(R.string.total)) col 0 row totalRow
                label {
                    font = bold()
                    textProperty().bind(stringBindingOf(*totalBindingDependencies) {
                        currencyConverter.toString(total)
                    })
                    textFillProperty().bind(bindingOf(textProperty()) {
                        getColor(when {
                            total > 0 -> R.color.green
                            else -> R.color.red
                        })
                    })
                } col 1 row totalRow
            }
        }
        defaultButton.run {
            text = getString(R.string.add)
            disableProperty().bind(disableBinding)
        }
    }
}