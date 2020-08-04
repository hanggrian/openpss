package com.hendraanggrian.openpss.ui.invoice.job

import com.hendraanggrian.openpss.R
import com.hendraanggrian.openpss.content.Context
import com.hendraanggrian.openpss.control.DoubleField
import com.hendraanggrian.openpss.control.IntField
import com.hendraanggrian.openpss.db.schemas.Invoice
import com.hendraanggrian.openpss.db.schemas.OffsetPrice
import com.hendraanggrian.openpss.db.schemas.OffsetPrices
import com.hendraanggrian.openpss.db.transaction
import javafx.beans.Observable
import javafx.beans.value.ObservableBooleanValue
import javafx.scene.control.ComboBox
import ktfx.beans.value.isBlank
import ktfx.beans.value.lessEq
import ktfx.beans.value.or
import ktfx.collections.toObservableList
import ktfx.coroutines.listener
import ktfx.jfoenix.jfxComboBox
import ktfx.layouts._GridPane
import ktfx.layouts.label
import ktfx.listeners.converter

class AddOffsetJobPopover(context: Context) :
    AddJobPopover<Invoice.OffsetJob>(context, R.string.add_offset_job), Invoice.Job {

    private lateinit var typeChoice: ComboBox<OffsetPrice>
    private lateinit var techniqueChoice: ComboBox<Invoice.OffsetJob.Technique>
    private lateinit var minQtyField: IntField
    private lateinit var minPriceField: DoubleField
    private lateinit var excessPriceField: DoubleField

    override fun _GridPane.onCreateContent() {
        label(getString(R.string.type)) col 0 row currentRow
        typeChoice = jfxComboBox(transaction { OffsetPrices().toObservableList() }) {
            valueProperty().listener { _, _, job ->
                minQtyField.value = job.minQty
                minPriceField.value = job.minPrice
                excessPriceField.value = job.excessPrice
            }
        } col 1 colSpans 2 row currentRow
        currentRow++
        label(getString(R.string.technique)) col 0 row currentRow
        techniqueChoice = jfxComboBox(Invoice.OffsetJob.Technique.values().toObservableList()) {
            converter { toString { it!!.toString(this@AddOffsetJobPopover) } }
            selectionModel.selectFirst()
        } col 1 colSpans 2 row currentRow
        currentRow++
        label(getString(R.string.min_qty)) col 0 row currentRow
        minQtyField = IntField().apply { promptText = getString(R.string.min_qty) }() col 1 colSpans 2 row currentRow
        currentRow++
        label(getString(R.string.min_price)) col 0 row currentRow
        minPriceField = DoubleField().apply { promptText = getString(R.string.min_price) }() col 1 colSpans 2 row
            currentRow
        currentRow++
        label(getString(R.string.excess_price)) col 0 row currentRow
        excessPriceField = DoubleField().apply {
            promptText = getString(R.string.excess_price)
        }() col 1 colSpans 2 row currentRow
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
        get() = Invoice.OffsetJob.new(qty, desc, total, typeChoice.value.name, techniqueChoice.value)

    override fun calculateTotal(): Double = when (techniqueChoice.value) {
        null -> 0.0
        Invoice.OffsetJob.Technique.ONE_SIDE -> calculateSide(
            qty,
            minQtyField.value,
            minPriceField.value,
            excessPriceField.value
        )
        Invoice.OffsetJob.Technique.TWO_SIDE_EQUAL -> calculateSide(
            qty * 2,
            minQtyField.value,
            minPriceField.value,
            excessPriceField.value
        )
        Invoice.OffsetJob.Technique.TWO_SIDE_DISTINCT -> calculateSide(
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
