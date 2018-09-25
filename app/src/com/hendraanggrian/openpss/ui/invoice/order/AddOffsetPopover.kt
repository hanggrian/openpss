package com.hendraanggrian.openpss.ui.invoice.order

import com.hendraanggrian.openpss.R
import com.hendraanggrian.openpss.control.DoubleField
import com.hendraanggrian.openpss.control.IntField
import com.hendraanggrian.openpss.control.doubleField
import com.hendraanggrian.openpss.control.intField
import com.hendraanggrian.openpss.db.schemas.Invoice
import com.hendraanggrian.openpss.db.schemas.OffsetPrice
import com.hendraanggrian.openpss.db.schemas.OffsetPrices
import com.hendraanggrian.openpss.db.transaction
import com.hendraanggrian.openpss.i18n.Resourced
import javafx.beans.Observable
import javafx.beans.value.ObservableBooleanValue
import javafx.scene.control.ChoiceBox
import javafxx.beans.value.isBlank
import javafxx.beans.value.lessEq
import javafxx.beans.value.or
import javafxx.collections.toObservableList
import javafxx.coroutines.listener
import javafxx.layouts._GridPane
import javafxx.layouts.choiceBox
import javafxx.layouts.label
import javafxx.listeners.converter

class AddOffsetPopover(resourced: Resourced) : AddOrderPopover<Invoice.Offset>(resourced, R.string.add_offset),
    Invoice.Order {

    private lateinit var machineChoice: ChoiceBox<OffsetPrice>
    private lateinit var techniqueChoice: ChoiceBox<Invoice.Offset.Technique>
    private lateinit var minQtyField: IntField
    private lateinit var minPriceField: DoubleField
    private lateinit var excessPriceField: DoubleField

    override fun _GridPane.onCreateContent() {
        label(getString(R.string.machine)) col 0 row currentRow
        machineChoice = choiceBox(transaction { OffsetPrices().toObservableList() }) {
            valueProperty().listener { _, _, offset ->
                minQtyField.value = offset.minQty
                minPriceField.value = offset.minPrice
                excessPriceField.value = offset.excessPrice
            }
        } col 1 colSpans 2 row currentRow
        currentRow++
        label(getString(R.string.technique)) col 0 row currentRow
        techniqueChoice = choiceBox(Invoice.Offset.Technique.values().toObservableList()) {
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