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
import ktfx.bindings.buildStringBinding
import ktfx.controls.gap
import ktfx.coroutines.listener
import ktfx.jfoenix.jfxCheckBox
import ktfx.jfoenix.jfxTextField
import ktfx.layouts.KtfxGridPane
import ktfx.layouts.gridPane
import ktfx.layouts.label

abstract class AddJobPopOver<T : Invoice.Job>(component: FxComponent, titleId: String) :
    ResultablePopOver<T>(component, titleId), Invoice.Job {

    abstract fun KtfxGridPane.onCreateContent()

    abstract val totalBindingDependencies: Array<Observable>

    abstract val defaultButtonDisableBinding: ObservableBooleanValue

    abstract fun calculateTotal(): Double

    protected var currentRow: Int = 0
    protected lateinit var qtyField: IntField
    protected lateinit var titleField: TextField
    protected lateinit var totalField: DoubleField
    protected lateinit var customizeCheck: CheckBox

    override val focusedNode: Node? get() = qtyField

    init {
        gridPane {
            gap = getDouble(R.value.padding_medium)
            label(getString(R2.string.qty)) col 0 row currentRow
            qtyField = addNode(IntField().apply {
                promptText = getString(R2.string.qty)
            }) col 1 colSpans 2 row currentRow
            currentRow++
            label(getString(R2.string.description)) col 0 row currentRow
            titleField = jfxTextField {
                promptText = getString(R2.string.description)
            } col 1 colSpans 2 row currentRow
            currentRow++
            onCreateContent()
            currentRow++
            label(getString(R2.string.total)) col 0 row currentRow
            totalField = addNode(DoubleField().apply { bindTotal() }) col 1 row currentRow
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
        textProperty().bind(buildStringBinding(*totalBindingDependencies) {
            calculateTotal().toString()
        })
}
