package com.hendraanggrian.openpss.ui.invoice.job

import com.hendraanggrian.openpss.FxComponent
import com.hendraanggrian.openpss.R
import com.hendraanggrian.openpss.R2
import com.hendraanggrian.openpss.control.DoubleField
import com.hendraanggrian.openpss.control.IntField
import com.hendraanggrian.openpss.schema.Invoice
import com.hendraanggrian.openpss.ui.ResultablePopOver
import javafx.beans.Observable
import javafx.beans.value.ObservableBooleanValue
import javafx.scene.Node
import javafx.scene.control.CheckBox
import javafx.scene.control.TextField
import ktfx.controls.gap
import ktfx.coroutines.listener
import ktfx.jfoenix.layouts.jfxCheckBox
import ktfx.jfoenix.layouts.jfxTextField
import ktfx.layouts.KtfxGridPane
import ktfx.layouts.gridPane
import ktfx.layouts.label
import ktfx.stringBindingOf

abstract class AddJobPopOver<T : Invoice.Job>(component: FxComponent, titleId: String) :
    ResultablePopOver<T>(component, titleId), Invoice.Job {

    abstract fun KtfxGridPane.onCreateContent()

    abstract val totalBindingDependencies: Array<Observable>

    abstract val defaultButtonDisableBinding: ObservableBooleanValue

    abstract fun calculateTotal(): Double

    protected var currentRow: Int = 0
    protected val qtyField: IntField
    protected val titleField: TextField
    protected val totalField: DoubleField
    protected val customizeCheck: CheckBox

    override val focusedNode: Node? get() = qtyField

    init {
        gridPane {
            gap = getDouble(R.value.padding_medium)
            label(getString(R2.string.qty)) col 0 row currentRow
            qtyField = addChild(IntField().apply { promptText = getString(R2.string.qty) }) col (1 to 2) row currentRow
            currentRow++
            label(getString(R2.string.description)) col 0 row currentRow
            titleField = jfxTextField { promptText = getString(R2.string.description) } col (1 to 2) row currentRow
            currentRow++
            onCreateContent()
            currentRow++
            label(getString(R2.string.total)) col 0 row currentRow
            totalField = addChild(DoubleField().apply { bindTotal() }) col 1 row currentRow
            customizeCheck = jfxCheckBox(getString(R2.string.customize)) {
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
            text = getString(R2.string.add)
            disableProperty().bind(defaultButtonDisableBinding)
        }
    }

    override val qty: Int get() = qtyField.value

    override val desc: String get() = titleField.text

    override val total: Double get() = totalField.value

    private fun DoubleField.bindTotal() =
        textProperty().bind(stringBindingOf(*totalBindingDependencies) { calculateTotal().toString() })
}
