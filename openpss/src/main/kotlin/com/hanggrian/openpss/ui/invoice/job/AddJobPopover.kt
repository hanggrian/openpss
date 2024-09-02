package com.hanggrian.openpss.ui.invoice.job

import com.hanggrian.openpss.Context
import com.hanggrian.openpss.R
import com.hanggrian.openpss.control.DoubleField
import com.hanggrian.openpss.control.IntField
import com.hanggrian.openpss.db.schemas.Invoice
import com.hanggrian.openpss.popup.popover.ResultablePopover
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
    ResultablePopover<T>(context, titleId),
    Invoice.Job {
    abstract val totalBindingDependencies: Array<Observable>

    abstract val defaultButtonDisableBinding: ObservableBooleanValue

    protected var currentRow: Int = 0
    protected val qtyField: IntField
    protected val titleField: TextField
    protected val totalField: DoubleField
    protected val customizeCheck: CheckBox

    init {
        gridPane {
            hgap = getDouble(R.dimen_padding_medium)
            vgap = getDouble(R.dimen_padding_medium)
            label(getString(R.string_qty))
                .grid(currentRow, 0)
            qtyField =
                addChild(IntField().apply { promptText = getString(R.string_qty) })
                    .grid(currentRow, 1 to 2)
            currentRow++
            label(getString(R.string_description))
                .grid(currentRow, 0)
            titleField =
                jfxTextField { promptText = getString(R.string_description) }
                    .grid(currentRow, 1 to 2)
            currentRow++
            onCreateContent()
            currentRow++
            label(getString(R.string_total))
                .grid(currentRow, 0)
            totalField =
                addChild(DoubleField().apply { bindTotal() })
                    .grid(currentRow, 1)
            customizeCheck =
                jfxCheckBox(getString(R.string_customize)) {
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
            text = getString(R.string_add)
            disableProperty().bind(defaultButtonDisableBinding)
        }
    }

    abstract fun KtfxGridPane.onCreateContent()

    abstract fun calculateTotal(): Double

    override val focusedNode: Node get() = qtyField

    override val qty: Int get() = qtyField.value

    override val desc: String get() = titleField.text

    override val total: Double get() = totalField.value

    private fun DoubleField.bindTotal() =
        textProperty().bind(
            stringBindingOf(*totalBindingDependencies) {
                calculateTotal().toString()
            },
        )
}
