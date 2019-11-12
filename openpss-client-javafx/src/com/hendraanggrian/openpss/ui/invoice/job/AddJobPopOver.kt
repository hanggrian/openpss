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
import ktfx.bindings.stringBindingOf
import ktfx.coroutines.listener
import ktfx.jfoenix.layouts.jfxCheckBox
import ktfx.jfoenix.layouts.jfxTextField
import ktfx.layouts.KtfxGridPane
import ktfx.layouts.addNode
import ktfx.layouts.gap
import ktfx.layouts.gridPane
import ktfx.layouts.label

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
            label(getString(R2.string.qty)) {
                gridAt(currentRow, 0)
            }
            qtyField = addNode(IntField()) {
                gridAt(currentRow, 1, colSpans = 2)
                promptText = getString(R2.string.qty)
            }
            currentRow++
            label(getString(R2.string.description)) {
                gridAt(currentRow, 0)
            }
            titleField = jfxTextField {
                gridAt(currentRow, 1, colSpans = 2)
                promptText = getString(R2.string.description)
            }
            currentRow++
            onCreateContent()
            currentRow++
            label(getString(R2.string.total)) {
                gridAt(currentRow, 0)
            }
            totalField = addNode(DoubleField()) {
                gridAt(currentRow, 1)
                bindTotal()
            }
            customizeCheck = jfxCheckBox(getString(R2.string.customize)) {
                gridAt(currentRow, 2)
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
            }
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
        textProperty().bind(stringBindingOf(*totalBindingDependencies) {
            calculateTotal().toString()
        })
}
