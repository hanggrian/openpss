package com.hendraanggrian.openpss.ui.receipt

import com.hendraanggrian.openpss.R
import com.hendraanggrian.openpss.currencyConverter
import com.hendraanggrian.openpss.db.dbDateTime
import com.hendraanggrian.openpss.db.findById
import com.hendraanggrian.openpss.db.schema.Customer
import com.hendraanggrian.openpss.db.schema.Customers
import com.hendraanggrian.openpss.db.schema.Employee
import com.hendraanggrian.openpss.db.schema.Employees
import com.hendraanggrian.openpss.db.schema.Offset
import com.hendraanggrian.openpss.db.schema.Other
import com.hendraanggrian.openpss.db.schema.Plate
import com.hendraanggrian.openpss.db.schema.Receipt
import com.hendraanggrian.openpss.db.transaction
import com.hendraanggrian.openpss.time.PATTERN_DATE
import com.hendraanggrian.openpss.ui.Controller
import com.hendraanggrian.openpss.ui.Resourced
import com.hendraanggrian.openpss.utils.currencyCell
import com.hendraanggrian.openpss.utils.getFont
import com.hendraanggrian.openpss.utils.numberCell
import com.hendraanggrian.openpss.utils.stringCell
import javafx.beans.property.DoubleProperty
import javafx.beans.property.ObjectProperty
import javafx.beans.property.SimpleDoubleProperty
import javafx.beans.property.SimpleObjectProperty
import javafx.scene.Node
import javafx.scene.control.ButtonType.CANCEL
import javafx.scene.control.Dialog
import javafx.scene.control.TableView
import javafx.scene.control.TableView.CONSTRAINED_RESIZE_POLICY
import javafx.scene.control.TextArea
import javafx.scene.image.ImageView
import ktfx.application.later
import ktfx.beans.binding.doubleBindingOf
import ktfx.beans.binding.lessEq
import ktfx.beans.binding.or
import ktfx.beans.binding.stringBindingOf
import ktfx.collections.emptyBinding
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
import ktfx.scene.control.graphicIcon
import ktfx.scene.control.headerTitle
import ktfx.scene.control.okButton
import ktfx.scene.input.isDelete
import ktfx.scene.layout.gap
import org.joda.time.DateTime

class ReceiptDialog(
    controller: Controller,
    private val prefill: Receipt? = null
) : Dialog<Receipt>(), Resourced by controller {

    private lateinit var plateTable: TableView<Plate>
    private lateinit var offsetTable: TableView<Offset>
    private lateinit var otherTable: TableView<Other>
    private lateinit var noteArea: TextArea

    private val employee: Employee = transaction {
        findById(Employees, prefill?.employeeId ?: controller.employeeId).single()
    }!!
    private val dateTime: DateTime = prefill?.dateTime ?: dbDateTime
    private val customerProperty: ObjectProperty<Customer> = SimpleObjectProperty(when {
        isEdit() -> transaction { findById(Customers, prefill!!.customerId).single() }
        else -> null
    })
    private val totalProperty: DoubleProperty = SimpleDoubleProperty(prefill?.total ?: 0.0)

    init {
        headerTitle = getString(if (!isEdit()) R.string.add_receipt else R.string.edit_receipt)
        graphicIcon = ImageView(R.image.ic_receipt)
        dialogPane.content = gridPane {
            gap = 8.0
            label(getString(R.string.employee)) col 0 row 0
            label(employee.name) { font = getFont(R.font.opensans_bold) } col 1 row 0
            label(getString(R.string.date)) col 0 row 1
            label(dateTime.toString(PATTERN_DATE)) { font = getFont(R.font.opensans_bold) } col 1 row 1
            label(getString(R.string.customer)) col 0 row 2
            button {
                isDisable = isEdit()
                textProperty().bind(stringBindingOf(customerProperty) {
                    customerProperty.value?.toString() ?: getString(R.string.search_customer)
                })
                setOnAction { SearchCustomerDialog(this@ReceiptDialog).showAndWait().ifPresent { customerProperty.set(it) } }
            } col 1 row 2
            label(getString(R.string.plate)) col 0 row 3
            plateTable = receiptTableView({ AddPlateDialog(this@ReceiptDialog) }) {
                column<String>(getString(R.string.type)) { stringCell { type } }
                column<String>(getString(R.string.title)) { stringCell { title } }
                column<String>(getString(R.string.qty)) { numberCell { qty } }
                column<String>(getString(R.string.price)) { currencyCell { price } }
                column<String>(getString(R.string.total)) { currencyCell { total } }
            } col 1 row 3
            label(getString(R.string.offset)) col 0 row 4
            offsetTable = receiptTableView({ AddOffsetDialog(this@ReceiptDialog) }) {
                column<String>(getString(R.string.type)) { stringCell { type } }
                column<String>(getString(R.string.title)) { stringCell { title } }
                column<String>(getString(R.string.qty)) { numberCell { qty } }
                column<String>(getString(R.string.min_qty)) { numberCell { minQty } }
                column<String>(getString(R.string.min_price)) { currencyCell { minPrice } }
                column<String>(getString(R.string.excess_price)) { currencyCell { excessPrice } }
                column<String>(getString(R.string.total)) { currencyCell { total } }
            } col 1 row 4
            label(getString(R.string.others)) col 0 row 5
            otherTable = receiptTableView({ AddOtherDialog(this@ReceiptDialog) }) {
                column<String>(getString(R.string.title)) { stringCell { title } }
                column<String>(getString(R.string.qty)) { numberCell { qty } }
                column<String>(getString(R.string.price)) { currencyCell { price } }
                column<String>(getString(R.string.total)) { currencyCell { total } }
            } col 1 row 5
            totalProperty.bind(doubleBindingOf(plateTable.items, offsetTable.items, otherTable.items) {
                plateTable.items.sumByDouble { it.total } +
                    offsetTable.items.sumByDouble { it.total } +
                    otherTable.items.sumByDouble { it.total }
            })
            label(getString(R.string.note)) col 0 row 6
            noteArea = textArea { prefHeight = 48.0 } col 1 row 6
            label(getString(R.string.total)) col 0 row 7
            label {
                font = getFont(R.font.opensans_bold)
                textProperty().bind(stringBindingOf(totalProperty) { currencyConverter.toString(totalProperty.value) })
            } col 1 row 7
        }
        cancelButton()
        okButton { disableProperty().bind(customerProperty.isNull or totalProperty.lessEq(0)) }
        setResultConverter {
            if (it == CANCEL) null else Receipt.new(
                employee.id,
                customerProperty.value.id,
                dateTime,
                plateTable.items,
                offsetTable.items,
                otherTable.items,
                noteArea.text
            )
        }
    }

    private fun isEdit(): Boolean = prefill != null

    private fun <S> LayoutManager<Node>.receiptTableView(
        newAddDialog: () -> Dialog<S>,
        columnsBuilder: TableColumnsBuilder<S>.() -> Unit
    ): TableView<S> = tableView {
        prefHeight = 96.0
        columnResizePolicy = CONSTRAINED_RESIZE_POLICY
        columns(columnsBuilder)
        columns.forEach {
            val minWidth = 768.0 / columns.size
            it.minWidth = when (it.text) {
                getString(R.string.title) -> 256.0
                else -> minWidth
            }
        }
        contextMenu {
            (getString(R.string.add)) {
                onAction { newAddDialog().showAndWait().ifPresent { this@tableView.items.add(it) } }
            }
            separatorMenuItem()
            (getString(R.string.delete)) {
                later { disableProperty().bind(this@tableView.selectionModel.selectedItemProperty().isNull) }
                onAction { this@tableView.items.remove(this@tableView.selectionModel.selectedItem) }
            }
            (getString(R.string.clear)) {
                later { disableProperty().bind(this@tableView.items.emptyBinding()) }
                onAction { this@tableView.items.clear() }
            }
        }
        onKeyPressed {
            if (it.code.isDelete() && selectionModel.selectedItem != null) items.remove(selectionModel.selectedItem)
        }
    }
}