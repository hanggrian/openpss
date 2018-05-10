package com.hendraanggrian.openpss.ui.invoice

import com.hendraanggrian.openpss.R
import com.hendraanggrian.openpss.controls.DoubleField
import com.hendraanggrian.openpss.controls.Popup
import com.hendraanggrian.openpss.controls.doubleField
import com.hendraanggrian.openpss.db.schemas.Employee
import com.hendraanggrian.openpss.db.schemas.Invoice
import com.hendraanggrian.openpss.db.schemas.Payment
import com.hendraanggrian.openpss.db.schemas.Payment.Method
import com.hendraanggrian.openpss.db.schemas.Payment.Method.CASH
import com.hendraanggrian.openpss.db.schemas.Payment.Method.values
import com.hendraanggrian.openpss.db.transaction
import com.hendraanggrian.openpss.resources.Resourced
import com.hendraanggrian.openpss.util.currencyConverter
import com.hendraanggrian.openpss.util.getColor
import com.hendraanggrian.openpss.util.getFont
import javafx.scene.Node
import javafx.scene.control.ChoiceBox
import javafx.scene.control.TextField
import javafx.scene.image.ImageView
import ktfx.beans.binding.bindingOf
import ktfx.beans.binding.stringBindingOf
import ktfx.beans.value.eq
import ktfx.beans.value.greater
import ktfx.beans.value.isBlank
import ktfx.beans.value.lessEq
import ktfx.beans.value.or
import ktfx.collections.toObservableList
import ktfx.coroutines.listener
import ktfx.coroutines.onAction
import ktfx.layouts.LayoutManager
import ktfx.layouts.button
import ktfx.layouts.choiceBox
import ktfx.layouts.gridPane
import ktfx.layouts.label
import ktfx.layouts.textField
import ktfx.layouts.tooltip
import ktfx.listeners.converter
import ktfx.scene.layout.gap

class AddPaymentPopup(
    resourced: Resourced,
    private val employee: Employee,
    private val invoice: Invoice
) : Popup<Payment>(resourced, R.string.add_payment) {

    private lateinit var valueField: DoubleField
    private lateinit var methodChoice: ChoiceBox<Method>
    private lateinit var referenceField: TextField
    private val receivable = transaction { invoice.calculateDue() }

    override val content: Node = gridPane {
        gap = 8.0
        label(getString(R.string.employee)) row 0 col 0
        label(employee.name) {
            font = getFont(R.font.sf_pro_text_bold)
        } row 0 col 1 colSpans 2
        label(getString(R.string.receivable)) row 1 col 0
        label(currencyConverter.toString(receivable)) {
            font = getFont(R.font.sf_pro_text_bold)
        } row 1 col 1 colSpans 2
        label(getString(R.string.payment)) row 2 col 0
        valueField = doubleField { promptText = getString(R.string.payment) } row 2 col 1
        button(graphic = ImageView(R.image.btn_match_receivable_light)) {
            tooltip(getString(R.string.match_receivable))
            onAction { valueField.value = receivable }
        } row 2 col 2
        label(getString(R.string.remaining)) row 3 col 0
        label {
            font = getFont(R.font.sf_pro_text_bold)
            textProperty().bind(stringBindingOf(valueField.valueProperty()) {
                (receivable - valueField.value).let { remaining ->
                    when (remaining) {
                        0.0 -> getString(R.string.paid)
                        else -> currencyConverter.toString(remaining)
                    }
                }
            })
            textFillProperty().bind(bindingOf(textProperty()) {
                getColor(when {
                    receivable - valueField.value == 0.0 -> R.color.teal
                    else -> R.color.red
                })
            })
        } row 3 col 1 colSpans 2
        label(getString(R.string.payment_method)) row 6 col 0
        methodChoice = choiceBox(values().toObservableList()) {
            converter { toString { it!!.toString(this@AddPaymentPopup) } }
            selectionModel.selectFirst()
        } row 6 col 1 colSpans 2
        label(getString(R.string.reference)) { bindDisable() } row 7 col 0
        referenceField = textField { bindDisable() } row 7 col 1 colSpans 2
    }

    override fun LayoutManager<Node>.buttons() {
        defaultButton(R.string.ok) {
            val binding = !valueField.validProperty() or
                valueField.valueProperty().lessEq(0) or
                valueField.valueProperty().greater(receivable)
            methodChoice.selectionModel.selectedItemProperty().listener { _, _, method ->
                disableProperty().bind(when (method) {
                    CASH -> binding
                    else -> binding or referenceField.textProperty().isBlank()
                })
            }
        }
    }

    override fun getResult(): Payment = Payment.new(invoice.id, employee.id, methodChoice.value, valueField.value,
        when (methodChoice.selectionModel.selectedItem) {
            CASH -> null
            else -> referenceField.text
        })

    private fun Node.bindDisable() = disableProperty().bind(methodChoice.selectionModel.selectedItemProperty() eq CASH)
}