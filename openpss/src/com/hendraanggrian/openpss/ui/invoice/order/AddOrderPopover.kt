package com.hendraanggrian.openpss.ui.invoice.order

import com.hendraanggrian.openpss.content.Context
import com.hendraanggrian.openpss.R
import com.hendraanggrian.openpss.control.DoubleField
import com.hendraanggrian.openpss.control.IntField
import com.hendraanggrian.openpss.control.doubleField
import com.hendraanggrian.openpss.control.intField
import com.hendraanggrian.openpss.db.Titled
import com.hendraanggrian.openpss.db.schemas.Invoice
import com.hendraanggrian.openpss.popup.popover.ResultablePopover
import javafx.beans.Observable
import javafx.beans.value.ObservableBooleanValue
import javafx.scene.control.CheckBox
import javafx.scene.control.TextField
import ktfx.beans.binding.buildStringBinding
import ktfx.coroutines.listener
import ktfx.jfoenix.jfxCheckBox
import ktfx.jfoenix.jfxTextField
import ktfx.layouts._GridPane
import ktfx.layouts.gridPane
import ktfx.layouts.label
import ktfx.layouts.tooltip
import ktfx.scene.layout.gap

abstract class AddOrderPopover<T : Titled>(context: Context, titleId: String) :
    ResultablePopover<T>(context, titleId), Invoice.Order {

    abstract fun _GridPane.onCreateContent()

    abstract val totalBindingDependencies: Array<Observable>

    abstract val defaultButtonDisableBinding: ObservableBooleanValue

    abstract fun calculateTotal(): Double

    protected var currentRow: Int = 0
    protected lateinit var titleField: TextField
    protected lateinit var qtyField: IntField
    protected lateinit var totalField: DoubleField
    protected lateinit var customizeCheck: CheckBox

    init {
        gridPane {
            gap = R.dimen.padding_medium.toDouble()
            label(getString(R.string.title)) col 0 row currentRow
            titleField = jfxTextField { promptText = getString(R.string.title) } col 1 colSpans 2 row currentRow
            currentRow++
            label(getString(R.string.qty)) col 0 row currentRow
            qtyField = intField { promptText = getString(R.string.qty) } col 1 colSpans 2 row currentRow
            currentRow++
            onCreateContent()
            currentRow++
            label(getString(R.string.total)) col 0 row currentRow
            totalField = doubleField { bindTotal() } col 1 row currentRow
            customizeCheck = jfxCheckBox(getString(R.string.customize)) {
                tooltip(getString(R.string.customize_total))
                selectedProperty().listener { _, _, selected ->
                    when {
                        selected -> {
                            val s = totalField.text
                            totalField.textProperty().unbind()
                            totalField.text = s
                        }
                        else -> totalField.bindTotal()
                    }
                }
            } col 2 row currentRow
        }
        defaultButton.run {
            text = getString(R.string.add)
            disableProperty().bind(defaultButtonDisableBinding)
        }
    }

    override val qty: Int get() = qtyField.value

    override val title: String get() = titleField.text

    override val total: Double get() = totalField.value

    private fun DoubleField.bindTotal() = textProperty().bind(buildStringBinding(*totalBindingDependencies) {
        calculateTotal().toString()
    })
}