package com.hendraanggrian.openpss.ui.invoice

import com.hendraanggrian.openpss.App
import com.hendraanggrian.openpss.R
import com.hendraanggrian.openpss.content.Context
import com.hendraanggrian.openpss.content.currencyConverter
import com.hendraanggrian.openpss.control.JFXDoubleField
import com.hendraanggrian.openpss.control.dialog.ResultableDialog
import com.hendraanggrian.openpss.control.jfxDoubleField
import com.hendraanggrian.openpss.db.schemas.Invoice
import com.hendraanggrian.openpss.db.schemas.Payment
import com.hendraanggrian.openpss.db.transaction
import com.hendraanggrian.openpss.util.bold
import com.hendraanggrian.openpss.util.getColor
import javafx.scene.Node
import javafx.scene.control.CheckBox
import javafx.scene.control.TextField
import javafx.scene.image.ImageView
import ktfx.beans.binding.buildBinding
import ktfx.beans.binding.buildBooleanBinding
import ktfx.beans.binding.buildStringBinding
import ktfx.coroutines.onAction
import ktfx.jfoenix.jfxButton
import ktfx.jfoenix.jfxCheckBox
import ktfx.jfoenix.jfxTextField
import ktfx.layouts.gridPane
import ktfx.layouts.label
import ktfx.layouts.tooltip
import ktfx.scene.layout.gap
import ktfx.util.invoke

class AddPaymentDialog(
    context: Context,
    private val invoice: Invoice
) : ResultableDialog<Payment>(context, R.string.add_payment) {

    private lateinit var valueField: JFXDoubleField
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
            valueField = jfxDoubleField { promptText = getString(R.string.payment) } row 2 col 1
            jfxButton(graphic = ImageView(R.image.btn_match_receivable)) {
                styleClass += App.STYLE_BUTTON_FLAT
                tooltip(getString(R.string.match_receivable))
                onAction {
                    valueField.value = receivable
                }
            } row 2 col 2
            label(getString(R.string.remaining)) row 3 col 0
            label {
                font = bold()
                textProperty().bind(buildStringBinding(valueField.valueProperty()) {
                    (receivable - valueField.value).let { remaining ->
                        when {
                            remaining <= 0.0 -> getString(R.string.paid)
                            else -> currencyConverter(remaining)
                        }
                    }
                })
                textFillProperty().bind(buildBinding(textProperty()) {
                    getColor(
                        when {
                            receivable - valueField.value <= 0.0 -> R.color.green
                            else -> R.color.red
                        }
                    )
                })
            } row 3 col 1 colSpans 2
            label(getString(R.string.cash)) row 6 col 0
            cashBox = jfxCheckBox { isSelected = true } row 6 col 1 colSpans 2
            label(getString(R.string.reference)) { bindDisable() } row 7 col 0
            referenceField = jfxTextField { bindDisable() } row 7 col 1 colSpans 2
        }
        defaultButton.disableProperty().bind(buildBooleanBinding(
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