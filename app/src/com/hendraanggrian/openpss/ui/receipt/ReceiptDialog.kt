package com.hendraanggrian.openpss.ui.receipt

import com.hendraanggrian.openpss.R
import com.hendraanggrian.openpss.db.schema.Customer
import com.hendraanggrian.openpss.db.schema.Employee
import com.hendraanggrian.openpss.db.schema.Offset
import com.hendraanggrian.openpss.db.schema.Plate
import com.hendraanggrian.openpss.db.schema.Receipt
import com.hendraanggrian.openpss.db.transaction
import com.hendraanggrian.openpss.time.PATTERN_DATE
import com.hendraanggrian.openpss.ui.Resourced
import com.hendraanggrian.openpss.util.getResourceString
import javafx.beans.property.ObjectProperty
import javafx.beans.property.SimpleObjectProperty
import javafx.scene.control.ButtonType.CANCEL
import javafx.scene.control.Dialog
import javafx.scene.control.TableView
import javafx.scene.image.ImageView
import javafx.scene.text.Font.loadFont
import ktfx.application.later
import ktfx.beans.binding.stringBindingOf
import ktfx.beans.property.asObservable
import ktfx.beans.property.toProperty
import ktfx.collections.emptyBinding
import ktfx.coroutines.onAction
import ktfx.coroutines.onKeyPressed
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
import org.joda.time.LocalDate.now

class ReceiptDialog(
    resourced: Resourced,
    employee: Employee,
    prefill: Receipt? = null
) : Dialog<Receipt>(), Resourced by resourced {

    private var customerProperty: ObjectProperty<Customer> = SimpleObjectProperty()

    private lateinit var plateTable: TableView<Plate>
    private lateinit var offsetTable: TableView<Offset>

    init {
        headerTitle = getString(R.string.add_receipt)
        graphicIcon = ImageView(R.image.ic_receipt)
        dialogPane.content = gridPane {
            gaps = 8
            label(getString(R.string.date)) col 0 row 0
            label(now().toString(PATTERN_DATE)) {
                font = loadFont(getResourceString(R.font.opensans_bold), 13.0)
            } col 1 row 0
            label(getString(R.string.employee)) col 0 row 1
            label(employee.name) { font = loadFont(getResourceString(R.font.opensans_bold), 13.0) } col 1 row 1
            label(getString(R.string.customer)) col 0 row 2
            button {
                textProperty().bind(stringBindingOf(customerProperty) {
                    customerProperty.value?.toString() ?: getString(R.string.search_customer)
                })
                setOnAction { SearchCustomerDialog(resourced).showAndWait().ifPresent { customerProperty.set(it) } }
            } col 1 row 2
            label(getString(R.string.note)) col 0 row 3
            textArea { heightPref = 64 } col 1 row 3
            label(getString(R.string.plate)) col 0 row 4
            plateTable = tableView<Plate> {
                heightPref = 128
                isEditable = true
                columns {
                    column<String>(getString(R.string.plate)) {
                        setCellValueFactory { transaction { it.value.plate }.toProperty() }
                    }
                    column<Int>(getString(R.string.qty)) {
                        setCellValueFactory { it.value.qty.toProperty().asObservable() }
                    }
                    column<Double>(getString(R.string.price)) {
                        setCellValueFactory { it.value.price.toProperty().asObservable() }
                    }
                    column<Double>(getString(R.string.total)) {
                        setCellValueFactory { it.value.total.toProperty().asObservable() }
                    }
                }
                contextMenu {
                    menuItem(getString(R.string.add_plate)) {
                        onAction {
                            AddPlateDialog(this@ReceiptDialog).showAndWait().ifPresent { plateTable.items.add(it) }
                        }
                    }
                    separatorMenuItem()
                    menuItem(getString(R.string.delete)) {
                        later { disableProperty().bind(plateTable.selectionModel.selectedItemProperty().isNull) }
                        onAction { plateTable.items.remove(plateTable.selectionModel.selectedItem) }
                    }
                    menuItem(getString(R.string.clear)) {
                        later { disableProperty().bind(plateTable.items.emptyBinding()) }
                        onAction { plateTable.items.clear() }
                    }
                }
                onKeyPressed {
                    if (it.code.isDelete() && selectionModel.selectedItem != null)
                        items.remove(selectionModel.selectedItem)
                }
            } col 1 row 4
            label(getString(R.string.offset)) col 0 row 5
            offsetTable = tableView<Offset> {
                heightPref = 128
                isEditable = true
                columns {
                    column<String>(getString(R.string.offset)) {
                        setCellValueFactory { transaction { it.value.offset }.toProperty() }
                    }
                    column<Int>(getString(R.string.qty)) {
                        setCellValueFactory { it.value.qty.toProperty().asObservable() }
                    }
                    column<Int>(getString(R.string.min_qty)) {
                        setCellValueFactory { it.value.minQty.toProperty().asObservable() }
                    }
                    column<Double>(getString(R.string.min_price)) {
                        setCellValueFactory { it.value.minPrice.toProperty().asObservable() }
                    }
                    column<Double>(getString(R.string.excess_price)) {
                        setCellValueFactory { it.value.excessPrice.toProperty().asObservable() }
                    }
                    column<Double>(getString(R.string.total)) {
                        setCellValueFactory { it.value.total.toProperty().asObservable() }
                    }
                }
                contextMenu {
                    menuItem(getString(R.string.add_offset)) {
                        onAction {
                            AddOffsetDialog(this@ReceiptDialog).showAndWait().ifPresent { offsetTable.items.add(it) }
                        }
                    }
                    separatorMenuItem()
                    menuItem(getString(R.string.delete)) {
                        later { disableProperty().bind(offsetTable.selectionModel.selectedItemProperty().isNull) }
                        onAction { offsetTable.items.remove(offsetTable.selectionModel.selectedItem) }
                    }
                    menuItem(getString(R.string.clear)) {
                        later { disableProperty().bind(offsetTable.items.emptyBinding()) }
                        onAction { offsetTable.items.clear() }
                    }
                }
                onKeyPressed {
                    if (it.code.isDelete() && selectionModel.selectedItem != null)
                        items.remove(selectionModel.selectedItem)
                }
            } col 1 row 5
        }
        cancelButton()
        okButton()
        setResultConverter { if (it == CANCEL) null else null }
    }
}