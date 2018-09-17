package com.hendraanggrian.openpss.ui.invoice.order

import com.hendraanggrian.openpss.R
import com.hendraanggrian.openpss.control.IntField
import com.hendraanggrian.openpss.control.bold
import com.hendraanggrian.openpss.control.intField
import com.hendraanggrian.openpss.control.popover.ResultablePopover
import com.hendraanggrian.openpss.currencyConverter
import com.hendraanggrian.openpss.db.Order
import com.hendraanggrian.openpss.db.Titled
import com.hendraanggrian.openpss.i18n.Resourced
import com.hendraanggrian.openpss.util.getColor
import javafx.beans.Observable
import javafx.beans.value.ObservableBooleanValue
import javafx.scene.control.Label
import javafx.scene.control.TextField
import javafxx.beans.binding.bindingOf
import javafxx.beans.binding.stringBindingOf
import javafxx.layouts._GridPane
import javafxx.layouts.gridPane
import javafxx.layouts.label
import javafxx.layouts.textField
import javafxx.scene.layout.gap

abstract class AddOrderPopover<T : Titled>(
    resourced: Resourced,
    titleId: String
) : ResultablePopover<T>(resourced, titleId), Order {

    abstract fun _GridPane.onLayout()

    abstract val totalBindingDependencies: Array<Observable>

    abstract val defaultButtonDisableBinding: ObservableBooleanValue

    protected lateinit var titleField: TextField
    protected lateinit var qtyField: IntField
    protected lateinit var totalLabel: Label

    init {
        gridPane {
            gap = R.dimen.padding_small.toDouble()
            label(getString(R.string.title)) col 0 row 0
            titleField = textField { promptText = getString(R.string.title) } col 1 row 0
            label(getString(R.string.qty)) col 0 row 1
            qtyField = intField { promptText = getString(R.string.qty) } col 1 row 1
            onLayout()
            (children.size / 2).let { totalRow ->
                label(getString(R.string.total)) col 0 row totalRow
                totalLabel = label {
                    font = bold()
                    textProperty().bind(stringBindingOf(*totalBindingDependencies) {
                        currencyConverter.toString(total)
                    })
                    textFillProperty().bind(bindingOf(textProperty()) {
                        getColor(
                            when {
                                total > 0 -> R.color.green
                                else -> R.color.red
                            }
                        )
                    })
                } col 1 row totalRow
            }
        }
        defaultButton.run {
            text = getString(R.string.add)
            disableProperty().bind(defaultButtonDisableBinding)
        }
    }

    override val qty: Int get() = qtyField.value
}