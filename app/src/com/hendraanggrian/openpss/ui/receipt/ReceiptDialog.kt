package com.hendraanggrian.openpss.ui.receipt

import com.hendraanggrian.openpss.R
import com.hendraanggrian.openpss.converters.MoneyStringConverter
import com.hendraanggrian.openpss.db.dbDateTime
import com.hendraanggrian.openpss.db.schema.Customer
import com.hendraanggrian.openpss.db.schema.Employee
import com.hendraanggrian.openpss.db.schema.Offset
import com.hendraanggrian.openpss.db.schema.Other
import com.hendraanggrian.openpss.db.schema.Plate
import com.hendraanggrian.openpss.db.schema.Receipt
import com.hendraanggrian.openpss.time.PATTERN_DATE
import com.hendraanggrian.openpss.ui.Resourced
import com.hendraanggrian.openpss.util.excessPriceCellValueFactory
import com.hendraanggrian.openpss.util.getResourceString
import com.hendraanggrian.openpss.util.minPriceCellValueFactory
import com.hendraanggrian.openpss.util.minQtyCellValueFactory
import com.hendraanggrian.openpss.util.priceCellValueFactory
import com.hendraanggrian.openpss.util.qtyCellValueFactory
import com.hendraanggrian.openpss.util.titleCellValueFactory
import com.hendraanggrian.openpss.util.totalCellValueFactory
import com.hendraanggrian.openpss.util.typeCellValueFactory
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
import javafx.scene.text.Font.loadFont
import javafx.util.converter.NumberStringConverter
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
import ktfx.layouts.menuItem
import ktfx.layouts.separatorMenuItem
import ktfx.layouts.tableView
import ktfx.layouts.textArea
import ktfx.scene.control.cancelButton
import ktfx.scene.control.graphicIcon
import ktfx.scene.control.headerTitle
import ktfx.scene.control.okButton
import ktfx.scene.input.isDelete
import ktfx.scene.layout.gaps
import ktfx.scene.layout.heightPref
import org.joda.time.DateTime

class ReceiptDialog(
    resourced: Resourced,
    employee: Employee,
    prefill: Receipt? = null
) : Dialog<Receipt>(), Resourced by resourced {

    private lateinit var plateTable: TableView<Plate>
    private lateinit var offsetTable: TableView<Offset>
    private lateinit var otherTable: TableView<Other>
    private lateinit var noteArea: TextArea

    private val dateTime: DateTime = dbDateTime
    private val customerProperty: ObjectProperty<Customer> = SimpleObjectProperty()
    private val totalProperty: DoubleProperty = SimpleDoubleProperty()

    private val numberConverter = NumberStringConverter()
    private val moneyConverter = MoneyStringConverter()

    init {
        headerTitle = getString(R.string.add_receipt)
        graphicIcon = ImageView(R.image.ic_receipt)
        dialogPane.content = gridPane {
            gaps = 8
            label(getString(R.string.employee)) col 0 row 0
            label(employee.name) {
                font = loadFont(getResourceString(R.font.opensans_bold), 13.0)
            } col 1 row 0
            label(getString(R.string.date)) col 0 row 1
            label(dateTime.toString(PATTERN_DATE)) {
                font = loadFont(getResourceString(R.font.opensans_bold), 13.0)
            } col 1 row 1
            label(getString(R.string.customer)) col 0 row 2
            button {
                textProperty().bind(stringBindingOf(customerProperty) {
                    customerProperty.value?.toString() ?: getString(R.string.search_customer)
                })
                setOnAction { SearchCustomerDialog(resourced).showAndWait().ifPresent { customerProperty.set(it) } }
            } col 1 row 2
            label(getString(R.string.plate)) col 0 row 3
            plateTable = receiptTableView({ AddPlateDialog(this@ReceiptDialog) }) {
                column<String>(getString(R.string.type)) { typeCellValueFactory() }
                column<String>(getString(R.string.title)) { titleCellValueFactory() }
                column<String>(getString(R.string.qty)) { qtyCellValueFactory(numberConverter) }
                column<String>(getString(R.string.price)) { priceCellValueFactory(moneyConverter) }
                column<String>(getString(R.string.total)) { totalCellValueFactory(moneyConverter) }
            } col 1 row 3
            label(getString(R.string.offset)) col 0 row 4
            offsetTable = receiptTableView({ AddOffsetDialog(this@ReceiptDialog) }) {
                column<String>(getString(R.string.type)) { typeCellValueFactory() }
                column<String>(getString(R.string.title)) { titleCellValueFactory() }
                column<String>(getString(R.string.qty)) { qtyCellValueFactory(numberConverter) }
                column<String>(getString(R.string.min_qty)) { minQtyCellValueFactory(numberConverter) }
                column<String>(getString(R.string.min_price)) { minPriceCellValueFactory(moneyConverter) }
                column<String>(getString(R.string.excess_price)) { excessPriceCellValueFactory(moneyConverter) }
                column<String>(getString(R.string.total)) { totalCellValueFactory(moneyConverter) }
            } col 1 row 4
            label(getString(R.string.others)) col 0 row 5
            otherTable = receiptTableView({ AddOtherDialog(this@ReceiptDialog) }) {
                column<String>(getString(R.string.title)) { titleCellValueFactory() }
                column<String>(getString(R.string.qty)) { qtyCellValueFactory(numberConverter) }
                column<String>(getString(R.string.price)) { priceCellValueFactory(moneyConverter) }
                column<String>(getString(R.string.total)) { totalCellValueFactory(moneyConverter) }
            } col 1 row 5
            totalProperty.bind(doubleBindingOf(plateTable.items, offsetTable.items, otherTable.items) {
                plateTable.items.sumByDouble { it.total } +
                    offsetTable.items.sumByDouble { it.total } +
                    otherTable.items.sumByDouble { it.total }
            })
            label(getString(R.string.note)) col 0 row 6
            noteArea = textArea { heightPref = 48 } col 1 row 6
            label(getString(R.string.total)) col 0 row 7
            label {
                font = loadFont(getResourceString(R.font.opensans_bold), 13.0)
                textProperty().bind(stringBindingOf(totalProperty) { moneyConverter.toString(totalProperty.value) })
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
                noteArea.text,
                totalProperty.value
            )
        }
    }

    private fun <S> LayoutManager<Node>.receiptTableView(
        newAddDialog: () -> Dialog<S>,
        columnsBuilder: TableColumnsBuilder<S>.() -> Unit
    ): TableView<S> = tableView {
        heightPref = 96
        columnResizePolicy = CONSTRAINED_RESIZE_POLICY
        columns(columnsBuilder)
        columns.forEachIndexed { i, column ->
            val minWidth = 384.0 / columns.size
            column.minWidth = when (i) {
                1 -> 256.0
                else -> minWidth
            }
        }
        contextMenu {
            menuItem(getString(R.string.add)) {
                onAction { newAddDialog().showAndWait().ifPresent { this@tableView.items.add(it) } }
            }
            separatorMenuItem()
            menuItem(getString(R.string.delete)) {
                later { disableProperty().bind(this@tableView.selectionModel.selectedItemProperty().isNull) }
                onAction { this@tableView.items.remove(this@tableView.selectionModel.selectedItem) }
            }
            menuItem(getString(R.string.clear)) {
                later { disableProperty().bind(this@tableView.items.emptyBinding()) }
                onAction { this@tableView.items.clear() }
            }
        }
        onKeyPressed {
            if (it.code.isDelete() && selectionModel.selectedItem != null) items.remove(selectionModel.selectedItem)
        }
    }
}