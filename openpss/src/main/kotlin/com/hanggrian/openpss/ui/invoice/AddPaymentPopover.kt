package com.hanggrian.openpss.ui.invoice

import com.hanggrian.openpss.Context
import com.hanggrian.openpss.R
import com.hanggrian.openpss.control.DoubleField
import com.hanggrian.openpss.db.schemas.Invoice
import com.hanggrian.openpss.db.schemas.Payment
import com.hanggrian.openpss.db.transaction
import com.hanggrian.openpss.popup.popover.ResultablePopover
import javafx.scene.Node
import javafx.scene.control.CheckBox
import javafx.scene.control.TextField
import javafx.scene.image.ImageView
import ktfx.bindings.bindingOf
import ktfx.bindings.booleanBindingOf
import ktfx.bindings.stringBindingBy
import ktfx.coroutines.onAction
import ktfx.jfoenix.layouts.jfxCheckBox
import ktfx.jfoenix.layouts.jfxTextField
import ktfx.jfoenix.layouts.styledJfxButton
import ktfx.layouts.gridPane
import ktfx.layouts.label
import ktfx.layouts.styledLabel
import ktfx.layouts.tooltip
import ktfx.text.invoke

class AddPaymentPopover(context: Context, private val invoice: Invoice) :
    ResultablePopover<Payment>(context, R.string_add_payment) {
    private var valueField: DoubleField
    private var cashBox: CheckBox
    private var referenceField: TextField
    private val receivable = transaction { invoice.calculateDue() }

    init {
        gridPane {
            hgap = getDouble(R.dimen_padding_medium)
            vgap = getDouble(R.dimen_padding_medium)
            label(getString(R.string_employee))
                .grid(0, 0)
            styledLabel(login.name, null, R.style_bold)
                .grid(0, 1 to 2)
            label(getString(R.string_receivable))
                .grid(1, 0)
            styledLabel(currencyConverter(receivable), null, R.style_bold)
                .grid(1, 1 to 2)
            label(getString(R.string_payment))
                .grid(2, 0)
            valueField =
                addChild(DoubleField().apply { promptText = getString(R.string_payment) })
                    .grid(2, 1)
            styledJfxButton(null, ImageView(R.image_btn_match_receivable), R.style_flat) {
                tooltip(getString(R.string_match_receivable))
                onAction { valueField.value = receivable }
            }.grid(2, 2)
            label(getString(R.string_remaining))
                .grid(3, 0)
            styledLabel(styleClass = arrayOf(R.style_bold)) {
                textProperty().bind(
                    valueField.valueProperty.stringBindingBy {
                        (receivable - it).let { remaining ->
                            when {
                                remaining <= 0.0 -> getString(R.string_paid)
                                else -> currencyConverter(remaining)
                            }
                        }
                    },
                )
                textFillProperty().bind(
                    bindingOf(textProperty()) {
                        getColor(
                            when {
                                receivable - valueField.value <= 0.0 -> R.color_green
                                else -> R.color_red
                            },
                        )
                    },
                )
            }.grid(3, 1 to 2)
            label(getString(R.string_cash))
                .grid(6, 0)
            cashBox =
                jfxCheckBox { isSelected = true }
                    .grid(6, 1 to 2)
            label(getString(R.string_reference)) { bindDisable() }
                .grid(7, 0)
            referenceField =
                jfxTextField { bindDisable() }
                    .grid(7, 1 to 2)
        }
        defaultButton.disableProperty().bind(
            booleanBindingOf(
                valueField.valueProperty,
                cashBox.selectedProperty(),
                referenceField.textProperty(),
            ) {
                (!valueField.isValid || valueField.value <= 0 || valueField.value > receivable)
                    .let {
                        when {
                            cashBox.isSelected -> it
                            else -> it || referenceField.text.isBlank()
                        }
                    }
            },
        )
    }

    override val nullableResult: Payment
        get() =
            Payment.new(
                invoice.id,
                login.id,
                valueField.value,
                when {
                    cashBox.isSelected -> null
                    else -> referenceField.text
                },
            )

    private fun Node.bindDisable() = disableProperty().bind(cashBox.selectedProperty())
}
