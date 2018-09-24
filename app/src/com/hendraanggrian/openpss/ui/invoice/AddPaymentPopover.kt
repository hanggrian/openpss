package com.hendraanggrian.openpss.ui.invoice

import com.hendraanggrian.openpss.R
import com.hendraanggrian.openpss.control.DoubleField
import com.hendraanggrian.openpss.control.bold
import com.hendraanggrian.openpss.control.doubleField
import com.hendraanggrian.openpss.control.popover.ResultablePopover
import com.hendraanggrian.openpss.currencyConverter
import com.hendraanggrian.openpss.db.schemas.Employee
import com.hendraanggrian.openpss.db.schemas.Invoice
import com.hendraanggrian.openpss.db.schemas.Payment
import com.hendraanggrian.openpss.db.transaction
import com.hendraanggrian.openpss.i18n.Resourced
import com.hendraanggrian.openpss.util.getColor
import javafx.scene.Node
import javafx.scene.control.CheckBox
import javafx.scene.control.TextField
import javafx.scene.image.ImageView
import javafxx.beans.binding.bindingOf
import javafxx.beans.binding.booleanBindingOf
import javafxx.beans.binding.stringBindingOf
import javafxx.coroutines.onAction
import javafxx.layouts.button
import javafxx.layouts.checkBox
import javafxx.layouts.gridPane
import javafxx.layouts.label
import javafxx.layouts.textField
import javafxx.layouts.tooltip
import javafxx.scene.layout.gap

class AddPaymentPopover(
    resourced: Resourced,
    private val employee: Employee,
    private val invoice: Invoice
) : ResultablePopover<Payment>(resourced, R.string.add_payment) {

    private lateinit var valueField: DoubleField
    private lateinit var cashBox: CheckBox
    private lateinit var referenceField: TextField
    private val receivable = transaction { invoice.calculateDue() }

    init {
        gridPane {
            gap = R.dimen.padding_small.toDouble()
            label(getString(R.string.employee)) row 0 col 0
            label(employee.name) {
                font = bold()
            } row 0 col 1 colSpans 2
            label(getString(R.string.receivable)) row 1 col 0
            label(currencyConverter.toString(receivable)) {
                font = bold()
            } row 1 col 1 colSpans 2
            label(getString(R.string.payment)) row 2 col 0
            valueField = doubleField { promptText = getString(R.string.payment) } row 2 col 1
            button(graphic = ImageView(R.image.btn_match_receivable_light)) {
                tooltip(getString(R.string.match_receivable))
                onAction { valueField.value = receivable }
            } row 2 col 2
            label(getString(R.string.remaining)) row 3 col 0
            label {
                font = bold()
                textProperty().bind(stringBindingOf(valueField.valueProperty()) {
                    (receivable - valueField.value).let { remaining ->
                        when {
                            remaining <= 0.0 -> getString(R.string.paid)
                            else -> currencyConverter.toString(remaining)
                        }
                    }
                })
                textFillProperty().bind(bindingOf(textProperty()) {
                    getColor(
                        when {
                            receivable - valueField.value <= 0.0 -> R.color.green
                            else -> R.color.red
                        }
                    )
                })
            } row 3 col 1 colSpans 2
            label(getString(R.string.cash)) row 6 col 0
            cashBox = checkBox { isSelected = true } row 6 col 1 colSpans 2
            label(getString(R.string.reference)) { bindDisable() } row 7 col 0
            referenceField = textField { bindDisable() } row 7 col 1 colSpans 2
        }
        defaultButton.disableProperty().bind(booleanBindingOf(
            valueField.valueProperty(), cashBox.selectedProperty(),
            referenceField.textProperty()
        ) {
            (!valueField.isValid || valueField.value <= 0 || valueField.value > receivable).let {
                when {
                    cashBox.isSelected -> it
                    else -> it || referenceField.text.isBlank()
                }
            }
        })
    }

    override val nullableResult: Payment?
        get() = Payment.new(
            invoice.id, employee.id, valueField.value,
            when {
                cashBox.isSelected -> null
                else -> referenceField.text
            }
        )

    private fun Node.bindDisable() = disableProperty().bind(cashBox.selectedProperty())
}