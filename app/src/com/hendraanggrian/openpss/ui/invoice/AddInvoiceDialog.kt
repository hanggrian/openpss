package com.hendraanggrian.openpss.ui.invoice

import com.hendraanggrian.openpss.PATTERN_DATE
import com.hendraanggrian.openpss.R
import com.hendraanggrian.openpss.control.bold
import com.hendraanggrian.openpss.control.currencyCell
import com.hendraanggrian.openpss.control.dialog.ResultableDialog
import com.hendraanggrian.openpss.control.numberCell
import com.hendraanggrian.openpss.control.popover.ResultablePopover
import com.hendraanggrian.openpss.control.stringCell
import com.hendraanggrian.openpss.currencyConverter
import com.hendraanggrian.openpss.db.dbDateTime
import com.hendraanggrian.openpss.db.schemas.Customer
import com.hendraanggrian.openpss.db.schemas.Employee
import com.hendraanggrian.openpss.db.schemas.Invoice
import com.hendraanggrian.openpss.i18n.Resourced
import com.hendraanggrian.openpss.io.properties.PreferencesFile.INVOICE_QUICK_SELECT_CUSTOMER
import com.hendraanggrian.openpss.ui.invoice.order.AddOffsetPopover
import com.hendraanggrian.openpss.ui.invoice.order.AddOtherPopover
import com.hendraanggrian.openpss.ui.invoice.order.AddPlatePopover
import com.hendraanggrian.openpss.util.getColor
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

class AddInvoiceDialog(
    resourced: Resourced,
    private val employee: Employee
) : ResultableDialog<Invoice>(resourced, R.string.add_invoice, R.image.header_invoice) {

    private lateinit var plateTable: TableView<Invoice.Plate>
    private lateinit var offsetTable: TableView<Invoice.Offset>
    private lateinit var otherTable: TableView<Invoice.Other>
    private lateinit var noteArea: TextArea

    private val dateTime: DateTime = dbDateTime
    private val customerProperty: ObjectProperty<Customer> = SimpleObjectProperty(null)
    private val totalProperty: DoubleProperty = SimpleDoubleProperty()

    init {
        gridPane {
            gap = R.dimen.padding_medium.toDouble()
            label(getString(R.string.employee)) col 0 row 0
            label(employee.name) { font = bold() } col 1 row 0
            label(getString(R.string.date)) col 2 row 0 hpriority ALWAYS halign RIGHT
            label(dateTime.toString(PATTERN_DATE)) { font = bold() } col 3 row 0
            label(getString(R.string.customer)) col 0 row 1
            button {
                textProperty().bind(stringBindingOf(customerProperty) {
                    customerProperty.value?.toString() ?: getString(R.string.search_customer)
                })
                onAction { _ ->
                    SearchCustomerPopover(this@AddInvoiceDialog).showAt(this@button) { customerProperty.set(it) }
                }
                if (INVOICE_QUICK_SELECT_CUSTOMER) fire()
            } col 1 row 1
            label(getString(R.string.plate)) col 0 row 2
            plateTable = invoiceTableView({ AddPlatePopover(this@AddInvoiceDialog) }) {
                columns {
                    column<Invoice.Plate, String>(R.string.qty, 72) { numberCell { qty } }
                    column<Invoice.Plate, String>(R.string.machine, 72) { stringCell { machine } }
                    column<Invoice.Plate, String>(R.string.title, 264) { stringCell { title } }
                    column<Invoice.Plate, String>(R.string.total, 156) { currencyCell { total } }
                }
            } col 1 row 2 colSpans 3
            label(getString(R.string.offset)) col 0 row 3
            offsetTable = invoiceTableView({ AddOffsetPopover(this@AddInvoiceDialog) }) {
                columns {
                    column<Invoice.Offset, String>(R.string.qty, 72) { numberCell { qty } }
                    column<Invoice.Offset, String>(R.string.machine, 72) { stringCell { machine } }
                    column<Invoice.Offset, String>(R.string.technique, 72) {
                        stringCell { typedTechnique.toString(this@AddInvoiceDialog) }
                    }
                    column<Invoice.Offset, String>(R.string.title, 192) { stringCell { title } }
                    column<Invoice.Offset, String>(R.string.total, 156) { currencyCell { total } }
                }
            } col 1 row 3 colSpans 3
            label(getString(R.string.others)) col 0 row 4
            otherTable = invoiceTableView({ AddOtherPopover(this@AddInvoiceDialog) }) {
                columns {
                    column<Invoice.Other, String>(R.string.qty, 72) { numberCell { qty } }
                    column<Invoice.Other, String>(R.string.title, 336) { stringCell { title } }
                    column<Invoice.Other, String>(R.string.total, 156) { currencyCell { total } }
                }
            } col 1 row 4 colSpans 3
            totalProperty.bind(doubleBindingOf(plateTable.items, offsetTable.items, otherTable.items) {
                plateTable.items.sumByDouble { it.total } +
                    offsetTable.items.sumByDouble { it.total } +
                    otherTable.items.sumByDouble { it.total }
            })
            label(getString(R.string.note)) col 0 row 5
            noteArea = textArea {
                prefHeight = 48.0
            } col 1 row 5 colSpans 3
            label(getString(R.string.total)) col 0 row 6
            label {
                font = bold()
                textProperty().bind(stringBindingOf(totalProperty) {
                    currencyConverter.toString(totalProperty.value)
                })
                textFillProperty().bind(
                    `when`(totalProperty greater 0)
                        then getColor(R.color.green)
                        otherwise getColor(R.color.red)
                )
            } col 1 row 6
        }
        cancelButton()
        okButton().disableProperty().bind(customerProperty.isNull or totalProperty.lessEq(0))
    }

    override val nullableResult: Invoice?
        get() = Invoice.new(
            employee.id,
            customerProperty.value.id,
            dateTime,
            plateTable.items,
            offsetTable.items,
            otherTable.items,
            noteArea.text
        )

    private fun <S> LayoutManager<Node>.invoiceTableView(
        newAddOrderPopOver: () -> ResultablePopover<S>,
        init: TableView<S>.() -> Unit
    ): TableView<S> = tableView {
        prefHeight = 96.0
        init()
        contextMenu {
            getString(R.string.add)(ImageView(R.image.menu_add)) {
                onAction { _ -> newAddOrderPopOver().showAt(this@tableView) { this@tableView.items.add(it) } }
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
        width: Int,
        init: TableColumn<S, T>.() -> Unit
    ): TableColumn<S, T> = column(getString(textId)) {
        width.toDouble().let {
            minWidth = it
            prefWidth = it
            maxWidth = it
        }
        init()
    }
}