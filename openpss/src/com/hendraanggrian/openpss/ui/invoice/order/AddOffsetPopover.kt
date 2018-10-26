package com.hendraanggrian.openpss.ui.invoice.order

import com.hendraanggrian.openpss.Context
import com.hendraanggrian.openpss.R
import com.hendraanggrian.openpss.control.DoubleField
import com.hendraanggrian.openpss.control.IntField
import com.hendraanggrian.openpss.control.doubleField
import com.hendraanggrian.openpss.control.intField
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

class AddOffsetPopover(context: Context) : AddOrderPopover<Invoice.Offset>(context, R.string.add_offset),
    Invoice.Order {

    private lateinit var machineChoice: ComboBox<OffsetPrice>
    private lateinit var techniqueChoice: ComboBox<Invoice.Offset.Technique>
    private lateinit var minQtyField: IntField
    private lateinit var minPriceField: DoubleField
    private lateinit var excessPriceField: DoubleField

    override fun _GridPane.onCreateContent() {
        label(getString(R.string.machine)) col 0 row currentRow
        machineChoice = jfxComboBox(transaction { OffsetPrices().toObservableList() }) {
            valueProperty().listener { _, _, offset ->
                minQtyField.value = offset.minQty
                minPriceField.value = offset.minPrice
                excessPriceField.value = offset.excessPrice
            }
        } col 1 colSpans 2 row currentRow
        currentRow++
        label(getString(R.string.technique)) col 0 row currentRow
        techniqueChoice = jfxComboBox(Invoice.Offset.Technique.values().toObservableList()) {
            converter { toString { it!!.toString(this@AddOffsetPopover) } }
            selectionModel.selectFirst()
        } col 1 colSpans 2 row currentRow
        currentRow++
        label(getString(R.string.min_qty)) col 0 row currentRow
        minQtyField = intField { promptText = getString(R.string.min_qty) } col 1 colSpans 2 row currentRow
        currentRow++
        label(getString(R.string.min_price)) col 0 row currentRow
        minPriceField = doubleField { promptText = getString(R.string.min_price) } col 1 colSpans 2 row currentRow
        currentRow++
        label(getString(R.string.excess_price)) col 0 row currentRow
        excessPriceField = doubleField { promptText = getString(R.string.excess_price) } col 1 colSpans 2 row currentRow
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
        get() = machineChoice.valueProperty().isNull or
            titleField.textProperty().isBlank() or
            qtyField.valueProperty().lessEq(0) or
            techniqueChoice.valueProperty().isNull or
            totalField.valueProperty().lessEq(0)

    override val nullableResult: Invoice.Offset?
        get() = Invoice.Offset.new(titleField.text, qty, total, machineChoice.value.name, techniqueChoice.value)

    override fun calculateTotal(): Double = when (techniqueChoice.value) {
        null -> 0.0
        Invoice.Offset.Technique.ONE_SIDE -> calculateSide(
            qty,
            minQtyField.value,
            minPriceField.value,
            excessPriceField.value
        )
        Invoice.Offset.Technique.TWO_SIDE_EQUAL -> calculateSide(
            qty * 2,
            minQtyField.value,
            minPriceField.value,
            excessPriceField.value
        )
        Invoice.Offset.Technique.TWO_SIDE_DISTINCT -> calculateSide(
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