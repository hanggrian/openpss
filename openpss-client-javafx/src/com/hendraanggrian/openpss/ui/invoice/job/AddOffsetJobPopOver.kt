package com.hendraanggrian.openpss.ui.invoice.job

import com.hendraanggrian.openpss.FxComponent
import com.hendraanggrian.openpss.R2
import com.hendraanggrian.openpss.api.OpenPSSApi
import com.hendraanggrian.openpss.control.DoubleField
import com.hendraanggrian.openpss.control.IntField
import com.hendraanggrian.openpss.schema.Invoice
import com.hendraanggrian.openpss.schema.OffsetPrice
import com.hendraanggrian.openpss.schema.Technique
import com.hendraanggrian.openpss.schema.new
import javafx.beans.Observable
import javafx.beans.value.ObservableBooleanValue
import javafx.scene.control.ComboBox
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import ktfx.bindings.isBlank
import ktfx.bindings.lessEq
import ktfx.bindings.or
import ktfx.buildStringConverter
import ktfx.collections.toObservableList
import ktfx.coroutines.listener
import ktfx.jfoenix.layouts.jfxComboBox
import ktfx.layouts.KtfxGridPane
import ktfx.layouts.addNode
import ktfx.layouts.label

class AddOffsetJobPopOver(component: FxComponent) :
    AddJobPopOver<Invoice.OffsetJob>(component, R2.string.add_offset_job), Invoice.Job {

    private lateinit var typeChoice: ComboBox<OffsetPrice>
    private lateinit var techniqueChoice: ComboBox<Technique>
    private lateinit var minQtyField: IntField
    private lateinit var minPriceField: DoubleField
    private lateinit var excessPriceField: DoubleField

    override fun KtfxGridPane.onCreateContent() {
        label(getString(R2.string.type)) {
            gridAt(currentRow, 0)
        }
        typeChoice = jfxComboBox(runBlocking(Dispatchers.IO) { OpenPSSApi.getOffsetPrices() }.toObservableList()) {
            gridAt(currentRow, 1)
            colSpans = 2
            valueProperty().listener { _, _, job ->
                minQtyField.value = job.minQty
                minPriceField.value = job.minPrice
                excessPriceField.value = job.excessPrice
            }
        }
        currentRow++
        label(getString(R2.string.technique)) {
            gridAt(currentRow, 0)
        }
        techniqueChoice = jfxComboBox(Technique.values().toObservableList()) {
            gridAt(currentRow, 1)
            colSpans = 2
            converter = buildStringConverter { toString { it!!.toString(this@AddOffsetJobPopOver) } }
            selectionModel.selectFirst()
        }
        currentRow++
        label(getString(R2.string.min_qty)) {
            gridAt(currentRow, 0)
        }
        minQtyField = addNode(IntField()) {
            gridAt(currentRow, 1)
            colSpans = 2
            promptText = getString(R2.string.min_qty)
        }
        currentRow++
        label(getString(R2.string.min_price)) {
            gridAt(currentRow, 0)
        }
        minPriceField = addNode(DoubleField()) {
            gridAt(currentRow, 1)
            colSpans = 2
            promptText = getString(R2.string.min_price)
        }
        currentRow++
        label(getString(R2.string.excess_price)) {
            gridAt(currentRow, 0)
        }
        excessPriceField = addNode(DoubleField()) {
            gridAt(currentRow, 1)
            colSpans = 2
            promptText = getString(R2.string.excess_price)
        }
    }

    override val totalBindingDependencies: Array<Observable>
        get() = arrayOf(
            qtyField.valueProperty(),
            techniqueChoice.valueProperty(),
            minQtyField.valueProperty(),
            minPriceField.valueProperty(),
            excessPriceField.valueProperty()
        )

    override val defaultButtonDisableBinding: ObservableBooleanValue
        get() = typeChoice.valueProperty().isNull or
            titleField.textProperty().isBlank() or
            qtyField.valueProperty().lessEq(0) or
            techniqueChoice.valueProperty().isNull or
            totalField.valueProperty().lessEq(0)

    override val nullableResult: Invoice.OffsetJob?
        get() = Invoice.OffsetJob.new(
            qty,
            desc,
            total,
            typeChoice.value.name,
            techniqueChoice.value
        )

    override fun calculateTotal(): Double = when (techniqueChoice.value) {
        null -> 0.0
        Technique.ONE_SIDE -> calculateSide(
            qty,
            minQtyField.value,
            minPriceField.value,
            excessPriceField.value
        )
        Technique.TWO_SIDE_EQUAL -> calculateSide(
            qty * 2,
            minQtyField.value,
            minPriceField.value,
            excessPriceField.value
        )
        Technique.TWO_SIDE_DISTINCT -> calculateSide(
            qty,
            minQtyField.value,
            minPriceField.value,
            excessPriceField.value
        ) * 2
    }

    private fun calculateSide(qty: Int, minQty: Int, minPrice: Double, excessPrice: Double) = when {
        qty <= minQty -> minPrice
        else -> minPrice + ((qty - minQty) * excessPrice)
    }
}
