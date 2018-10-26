package com.hendraanggrian.openpss.ui.invoice

import com.hendraanggrian.openpss.App
import com.hendraanggrian.openpss.Context
import com.hendraanggrian.openpss.R
import com.hendraanggrian.openpss.control.DoubleField
import com.hendraanggrian.openpss.control.bold
import com.hendraanggrian.openpss.control.doubleField
import com.hendraanggrian.openpss.currencyConverter
import com.hendraanggrian.openpss.db.schemas.Invoice
import com.hendraanggrian.openpss.db.schemas.Payment
import com.hendraanggrian.openpss.db.transaction
import com.hendraanggrian.openpss.popup.popover.ResultablePopover
import com.hendraanggrian.openpss.util.getColor
import javafx.scene.Node
import javafx.scene.control.CheckBox
import javafx.scene.control.TextField
import javafx.scene.image.ImageView
import ktfx.beans.binding.bindingOf
import ktfx.beans.binding.booleanBindingOf
import ktfx.beans.binding.stringBindingOf
import ktfx.coroutines.onAction
import ktfx.jfoenix.jfxButton
import ktfx.layouts.checkBox
import ktfx.layouts.gridPane
import ktfx.layouts.label
import ktfx.layouts.textField
import ktfx.layouts.tooltip
import ktfx.scene.layout.gap
import ktfx.util.invoke

class AddPaymentPopover(
    context: Context,
    private val invoice: Invoice
) : ResultablePopover<Payment>(context, R.string.add_payment) {

    private lateinit var valueField: DoubleField
    private lateinit var cashBox: CheckBox
    private lateinit var referenceField: TextField
    private val receivable = transaction { invoice.calculateDue() }

    init {
        gridPane {
            gap = R.dimen.padding_medium.toDouble()
            label(getString(R.string.employee)) row 0 col 0
            label(login.name) {
                font = bold()
            } row 0 col 1 colSpans 2
            label(getString(R.string.receivable)) row 1 col 0
            label(currencyConverter(receivable)) {
                font = bold()
            } row 1 col 1 colSpans 2
            label(getString(R.string.payment)) row 2 col 0
            valueField = doubleField { promptText = getString(R.string.payment) } row 2 col 1
            jfxButton(graphic = ImageView(R.image.act_match_receivable)) {
                styleClass += App.STYLE_BUTTON_FLAT
                tooltip(getString(R.string.match_receivable))
                onAction {
                    valueField.value = receivable
                }
            } row 2 col 2
            label(getString(R.string.remaining)) row 3 col 0
            label {
                font = bold()
                textProperty().bind(stringBindingOf(valueField.valueProperty()) {
                    (receivable - valueField.value).let { remaining ->
                        when {
                            remaining <= 0.0 -> getString(R.string.paid)
                            else -> currencyConverter(remaining)
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
            invoice.id, login.id, valueField.value,
            when {
                cashBox.isSelected -> null
                else -> referenceField.text
            }
        )

    private fun Node.bindDisable() = disableProperty().bind(cashBox.selectedProperty())
}