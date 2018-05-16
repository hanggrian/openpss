package com.hendraanggrian.openpss.ui.invoice.order

import com.hendraanggrian.openpss.R
import com.hendraanggrian.openpss.controls.DefaultPopOver
import com.hendraanggrian.openpss.controls.IntField
import com.hendraanggrian.openpss.controls.intField
import com.hendraanggrian.openpss.db.Order
import com.hendraanggrian.openpss.db.Titled
import com.hendraanggrian.openpss.internationalization.Resourced
import com.hendraanggrian.openpss.util.currencyConverter
import com.hendraanggrian.openpss.util.getColor
import com.hendraanggrian.openpss.util.getFont
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

abstract class AddOrderPopOver<out T : Titled>(
    resourced: Resourced,
    titleId: String
) : DefaultPopOver<T>(resourced, titleId), Order {

    abstract fun _GridPane.onLayout()

    abstract val totalBindingDependencies: Array<Observable>

    abstract val disableBinding: ObservableBooleanValue

    abstract fun newInstance(): T

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
                    font = getFont(R.font.sf_pro_text_bold)
                    textProperty().bind(stringBindingOf(*totalBindingDependencies) {
                        currencyConverter.toString(total)
                    })
                    textFillProperty().bind(bindingOf(textProperty()) {
                        getColor(when {
                            total > 0 -> R.color.teal
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

    override fun getResult(): T = newInstance()
}