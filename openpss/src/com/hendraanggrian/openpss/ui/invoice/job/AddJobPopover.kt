package com.hendraanggrian.openpss.ui.invoice.job

import com.hendraanggrian.openpss.R
import com.hendraanggrian.openpss.content.Context
import com.hendraanggrian.openpss.control.DoubleField
import com.hendraanggrian.openpss.control.IntField
import com.hendraanggrian.openpss.db.schemas.Invoice
import com.hendraanggrian.openpss.popup.popover.ResultablePopover
import javafx.beans.Observable
import javafx.beans.value.ObservableBooleanValue
import javafx.scene.Node
import javafx.scene.control.CheckBox
import javafx.scene.control.TextField
import ktfx.bindings.stringBindingOf
import ktfx.coroutines.listener
import ktfx.jfoenix.layouts.jfxCheckBox
import ktfx.jfoenix.layouts.jfxTextField
import ktfx.layouts.KtfxGridPane
import ktfx.layouts.gridPane
import ktfx.layouts.label

abstract class AddJobPopover<T : Invoice.Job>(context: Context, titleId: String) :
    ResultablePopover<T>(context, titleId), Invoice.Job {

    abstract fun KtfxGridPane.onCreateContent()

    abstract val totalBindingDependencies: Array<Observable>

    abstract val defaultButtonDisableBinding: ObservableBooleanValue

    abstract fun calculateTotal(): Double

    protected var currentRow: Int = 0
    protected var qtyField: IntField
    protected var titleField: TextField
    protected var totalField: DoubleField
    protected var customizeCheck: CheckBox

    override val focusedNode: Node? get() = qtyField

    init {
        gridPane {
            hgap = getDouble(R.dimen.padding_medium)
            vgap = getDouble(R.dimen.padding_medium)
            label(getString(R.string.qty)).grid(currentRow, 0)
            qtyField = addChild(IntField().apply { promptText = getString(R.string.qty) }).grid(currentRow, 1 to 2)
            currentRow++
            label(getString(R.string.description)).grid(currentRow, 0)
            titleField = jfxTextField { promptText = getString(R.string.description) }.grid(currentRow, 1 to 2)
            currentRow++
            onCreateContent()
            currentRow++
            label(getString(R.string.total)).grid(currentRow, 0)
            totalField = addChild(DoubleField().apply { bindTotal() }).grid(currentRow, 1)
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
            }.grid(currentRow, 2)
        }
        defaultButton.run {
            text = getString(R.string.add)
            disableProperty().bind(defaultButtonDisableBinding)
        }
    }

    override val qty: Int get() = qtyField.value

    override val desc: String get() = titleField.text

    override val total: Double get() = totalField.value

    private fun DoubleField.bindTotal() = textProperty().bind(
        stringBindingOf(*totalBindingDependencies) {
            calculateTotal().toString()
        }
    )
}
