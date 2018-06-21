package com.hendraanggrian.openpss.ui.invoice

import com.hendraanggrian.openpss.R
import com.hendraanggrian.openpss.control.dialog.ResultableDialog
import com.hendraanggrian.openpss.control.popover.ResultablePopover
import com.hendraanggrian.openpss.db.dbDateTime
import com.hendraanggrian.openpss.db.schemas.Customer
import com.hendraanggrian.openpss.db.schemas.Customers
import com.hendraanggrian.openpss.db.schemas.Employee
import com.hendraanggrian.openpss.db.schemas.Employees
import com.hendraanggrian.openpss.db.schemas.Invoice
import com.hendraanggrian.openpss.db.transaction
import com.hendraanggrian.openpss.i18n.Resourced
import com.hendraanggrian.openpss.io.properties.PreferencesFile.INVOICE_QUICK_SELECT_CUSTOMER
import com.hendraanggrian.openpss.ui.invoice.order.AddOffsetPopover
import com.hendraanggrian.openpss.ui.invoice.order.AddOtherPopover
import com.hendraanggrian.openpss.ui.invoice.order.AddPlatePopover
import com.hendraanggrian.openpss.util.PATTERN_DATE
import com.hendraanggrian.openpss.util.bold
import com.hendraanggrian.openpss.util.currencyCell
import com.hendraanggrian.openpss.util.currencyConverter
import com.hendraanggrian.openpss.util.getColor
import com.hendraanggrian.openpss.util.numberCell
import com.hendraanggrian.openpss.util.stringCell
import javafx.beans.property.DoubleProperty
import javafx.beans.property.ObjectProperty
import javafx.beans.property.SimpleDoubleProperty
import javafx.beans.property.SimpleObjectProperty
import javafx.geometry.HPos.RIGHT
import javafx.scene.Node
import javafx.scene.control.TableColumn
import javafx.scene.control.TableView
import javafx.scene.control.TextArea
import javafx.scene.image.ImageView
import javafx.scene.layout.Priority.ALWAYS
import ktfx.application.later
import ktfx.beans.binding.`when`
import ktfx.beans.binding.doubleBindingOf
import ktfx.beans.binding.otherwise
import ktfx.beans.binding.stringBindingOf
import ktfx.beans.binding.then
import ktfx.beans.value.greater
import ktfx.beans.value.lessEq
import ktfx.beans.value.or
import ktfx.collections.isEmpty
import ktfx.coroutines.onAction
import ktfx.coroutines.onKeyPressed
import ktfx.layouts.LayoutManager
import ktfx.layouts.TableColumnsBuilder
import ktfx.layouts.button
import ktfx.layouts.columns
import ktfx.layouts.contextMenu
import ktfx.layouts.gridPane
import ktfx.layouts.label
import ktfx.layouts.separatorMenuItem
import ktfx.layouts.tableView
import ktfx.layouts.textArea
import ktfx.scene.control.cancelButton
import ktfx.scene.control.okButton
import ktfx.scene.input.isDelete
import ktfx.scene.layout.gap
import org.joda.time.DateTime

class InvoiceDialog(
    resourced: Resourced,
    private val prefill: Invoice? = null,
    private val employee: Employee? = prefill?.let { transaction { Employees[prefill.employeeId].single() } }
) : ResultableDialog<Invoice>(resourced, when (prefill) {
    null -> R.string.add_invoice
    else -> R.string.edit_invoice
}, R.image.header_invoice) {

    private lateinit var plateTable: TableView<Invoice.Plate>
    private lateinit var offsetTable: TableView<Invoice.Offset>
    private lateinit var otherTable: TableView<Invoice.Other>
    private lateinit var noteArea: TextArea

    private val dateTime: DateTime = prefill?.dateTime ?: dbDateTime
    private val customerProperty: ObjectProperty<Customer> = SimpleObjectProperty(when {
        isEdit() -> transaction { Customers[prefill!!.customerId].single() }
        else -> null
    })
    private val totalProperty: DoubleProperty = SimpleDoubleProperty()

    init {
        gridPane {
            gap = R.dimen.padding_small.toDouble()
            label(getString(R.string.employee)) col 0 row 0
            label(employee!!.name) { font = bold() } col 1 row 0
            label(getString(R.string.date)) col 2 row 0 hpriority ALWAYS halign RIGHT
            label(dateTime.toString(PATTERN_DATE)) { font = bold() } col 3 row 0
            label(getString(R.string.customer)) col 0 row 1
            button {
                isDisable = isEdit()
                textProperty().bind(stringBindingOf(customerProperty) {
                    customerProperty.value?.toString() ?: getString(R.string.search_customer)
                })
                onAction {
                    SearchCustomerPopover(this@InvoiceDialog).showAt(this@button) { customerProperty.set(it) }
                }
                if (INVOICE_QUICK_SELECT_CUSTOMER && !isEdit()) fire()
            } col 1 row 1
            label(getString(R.string.plate)) col 0 row 2
            plateTable = invoiceTableView({ AddPlatePopover(this@InvoiceDialog) }) {
                columns {
                    column<Invoice.Plate, String>(R.string.machine, 64) { stringCell { machine } }
                    column<Invoice.Plate, String>(R.string.title, 256) { stringCell { title } }
                    column<Invoice.Plate, String>(R.string.qty, 64) { numberCell { qty } }
                    column<Invoice.Plate, String>(R.string.price, 416) { currencyCell { price } }
                    column<Invoice.Plate, String>(R.string.total, 128) { currencyCell { total } }
                }
                if (isEdit()) items.addAll(prefill!!.plates)
            } col 1 row 2 colSpans 3
            label(getString(R.string.offset)) col 0 row 3
            offsetTable = invoiceTableView({ AddOffsetPopover(this@InvoiceDialog) }) {
                columns {
                    column<Invoice.Offset, String>(R.string.machine, 64) { stringCell { machine } }
                    column<Invoice.Offset, String>(R.string.title, 256) { stringCell { title } }
                    column<Invoice.Offset, String>(R.string.qty, 64) { numberCell { qty } }
                    column<Invoice.Offset, String>(R.string.technique, 128) {
                        stringCell { typedTechnique.toString(this@InvoiceDialog) }
                    }
                    column<Invoice.Offset, String>(R.string.min_qty, 64) { numberCell { minQty } }
                    column<Invoice.Offset, String>(R.string.min_price, 128) { currencyCell { minPrice } }
                    column<Invoice.Offset, String>(R.string.excess_price, 64) { currencyCell { excessPrice } }
                    column<Invoice.Offset, String>(R.string.total, 128) { currencyCell { total } }
                }
                if (isEdit()) items.addAll(prefill!!.offsets)
            } col 1 row 3 colSpans 3
            label(getString(R.string.others)) col 0 row 4
            otherTable = invoiceTableView({ AddOtherPopover(this@InvoiceDialog) }) {
                columns {
                    column<Invoice.Other, String>(R.string.title, 336) { stringCell { title } }
                    column<Invoice.Other, String>(R.string.qty, 64) { numberCell { qty } }
                    column<Invoice.Other, String>(R.string.price, 416) { currencyCell { price } }
                    column<Invoice.Other, String>(R.string.total, 128) { currencyCell { total } }
                }
                if (isEdit()) items.addAll(prefill!!.others)
            } col 1 row 4 colSpans 3
            totalProperty.bind(doubleBindingOf(plateTable.items, offsetTable.items, otherTable.items) {
                plateTable.items.sumByDouble { it.total } +
                    offsetTable.items.sumByDouble { it.total } +
                    otherTable.items.sumByDouble { it.total }
            })
            label(getString(R.string.note)) col 0 row 5
            noteArea = textArea {
                prefHeight = 48.0
                if (isEdit()) text = prefill!!.note
            } col 1 row 5 colSpans 3
            label(getString(R.string.total)) col 0 row 6
            label {
                font = bold()
                textProperty().bind(stringBindingOf(totalProperty) {
                    currencyConverter.toString(totalProperty.value)
                })
                textFillProperty().bind(`when`(totalProperty greater 0)
                    then getColor(R.color.green)
                    otherwise getColor(R.color.red))
            } col 1 row 6
        }
        cancelButton()
        okButton().disableProperty().bind(customerProperty.isNull or totalProperty.lessEq(0))
    }

    override val optionalResult: Invoice?
        get() = when {
            isEdit() -> prefill!!.apply {
                plates = plateTable.items
                offsets = offsetTable.items
                others = otherTable.items
                note = noteArea.text
            }
            else -> Invoice.new(
                employee!!.id,
                customerProperty.value.id,
                dateTime,
                plateTable.items,
                offsetTable.items,
                otherTable.items,
                noteArea.text
            )
        }

    private fun isEdit(): Boolean = prefill != null

    private fun <S> LayoutManager<Node>.invoiceTableView(
        newAddOrderPopOver: () -> ResultablePopover<S>,
        init: TableView<S>.() -> Unit
    ): TableView<S> = tableView {
        prefHeight = 96.0
        init()
        prefWidth = columns.sumByDouble { it.minWidth } + 34 // just enough for vertical scrollbar
        contextMenu {
            getString(R.string.add)(ImageView(R.image.menu_add)) {
                onAction { newAddOrderPopOver().showAt(this@tableView) { this@tableView.items.add(it) } }
            }
            separatorMenuItem()
            getString(R.string.delete)(ImageView(R.image.menu_delete)) {
                later { disableProperty().bind(this@tableView.selectionModel.selectedItemProperty().isNull) }
                onAction { this@tableView.items.remove(this@tableView.selectionModel.selectedItem) }
            }
            getString(R.string.clear)(ImageView(R.image.menu_clear)) {
                later { disableProperty().bind(this@tableView.items.isEmpty) }
                onAction { this@tableView.items.clear() }
            }
        }
        onKeyPressed {
            if (it.code.isDelete() && selectionModel.selectedItem != null) items.remove(selectionModel.selectedItem)
        }
    }

    private fun <S, T> TableColumnsBuilder<S>.column(
        textId: String,
        minWidth: Int,
        init: TableColumn<S, T>.() -> Unit
    ): TableColumn<S, T> = column(getString(textId)) {
        this.minWidth = minWidth.toDouble()
        init()
    }
}