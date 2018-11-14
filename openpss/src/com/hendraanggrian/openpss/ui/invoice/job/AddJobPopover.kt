package com.hendraanggrian.openpss.ui.invoice.job

import com.hendraanggrian.openpss.R
import com.hendraanggrian.openpss.content.Context
import com.hendraanggrian.openpss.control.JFXDoubleField
import com.hendraanggrian.openpss.control.JFXIntField
import com.hendraanggrian.openpss.control.base.DoubleFieldBase
import com.hendraanggrian.openpss.control.jfxDoubleField
import com.hendraanggrian.openpss.control.jfxIntField
import com.hendraanggrian.openpss.control.popover.ResultablePopover
import com.hendraanggrian.openpss.db.schemas.Invoice
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
import ktfx.scene.layout.gap

abstract class AddJobPopover<T : Invoice.Job>(context: Context, titleId: String) :
    ResultablePopover<T>(context, titleId), Invoice.Job {

    abstract fun _GridPane.onCreateContent()

    abstract val totalBindingDependencies: Array<Observable>

    abstract val defaultButtonDisableBinding: ObservableBooleanValue

    abstract fun calculateTotal(): Double

    protected var currentRow: Int = 0
    protected lateinit var qtyField: JFXIntField
    protected lateinit var titleField: TextField
    protected lateinit var totalField: JFXDoubleField
    protected lateinit var customizeCheck: CheckBox

    init {
        gridPane {
            gap = getDouble(R.dimen.padding_medium)
            label(getString(R.string.qty)) col 0 row currentRow
            qtyField = jfxIntField { promptText = getString(R.string.qty) } col 1 colSpans 2 row currentRow
            currentRow++
            label(getString(R.string.title)) col 0 row currentRow
            titleField = jfxTextField { promptText = getString(R.string.title) } col 1 colSpans 2 row currentRow
            currentRow++
            onCreateContent()
            currentRow++
            label(getString(R.string.total)) col 0 row currentRow
            totalField = jfxDoubleField { bindTotal() } col 1 row currentRow
            customizeCheck = jfxCheckBox(getString(R.string.customize)) {
                totalField.disableProperty().bind(!selectedProperty())
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

    private fun DoubleFieldBase.bindTotal() = actual.textProperty().bind(buildStringBinding(*totalBindingDependencies) {
        calculateTotal().toString()
    })
}