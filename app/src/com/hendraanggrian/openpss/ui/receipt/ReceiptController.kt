package com.hendraanggrian.openpss.ui.receipt

import com.hendraanggrian.openpss.R
import com.hendraanggrian.openpss.db.schema.Customer
import com.hendraanggrian.openpss.db.schema.Customers
import com.hendraanggrian.openpss.db.schema.Employee
import com.hendraanggrian.openpss.db.schema.Employees
import com.hendraanggrian.openpss.db.schema.Offset
import com.hendraanggrian.openpss.db.schema.Other
import com.hendraanggrian.openpss.db.schema.Payment
import com.hendraanggrian.openpss.db.schema.Plate
import com.hendraanggrian.openpss.db.schema.Receipt
import com.hendraanggrian.openpss.db.schema.Receipts
import com.hendraanggrian.openpss.db.transaction
import com.hendraanggrian.openpss.scene.control.CountBox
import com.hendraanggrian.openpss.scene.layout.DateBox
import com.hendraanggrian.openpss.time.PATTERN_DATETIME
import com.hendraanggrian.openpss.ui.Addable
import com.hendraanggrian.openpss.ui.Controller
import com.hendraanggrian.openpss.ui.Refreshable
import com.hendraanggrian.openpss.ui.controller
import com.hendraanggrian.openpss.ui.pane
import com.hendraanggrian.openpss.util.doneCell
import com.hendraanggrian.openpss.util.excessPriceCell
import com.hendraanggrian.openpss.util.getResource
import com.hendraanggrian.openpss.util.minPriceCell
import com.hendraanggrian.openpss.util.minQtyCell
import com.hendraanggrian.openpss.util.priceCell
import com.hendraanggrian.openpss.util.qtyCell
import com.hendraanggrian.openpss.util.titleCell
import com.hendraanggrian.openpss.util.totalCell
import com.hendraanggrian.openpss.util.typeCell
import javafx.fxml.FXML
import javafx.fxml.FXMLLoader
import javafx.scene.Node
import javafx.scene.Scene
import javafx.scene.control.ChoiceBox
import javafx.scene.control.Label
import javafx.scene.control.MenuItem
import javafx.scene.control.Pagination
import javafx.scene.control.RadioButton
import javafx.scene.control.Tab
import javafx.scene.control.TableColumn
import javafx.scene.control.TableView
import javafx.scene.control.TableView.CONSTRAINED_RESIZE_POLICY
import javafx.scene.control.TextField
import javafx.stage.Modality.APPLICATION_MODAL
import javafx.util.Callback
import kotlinx.nosql.equal
import kotlinx.nosql.id
import ktfx.application.later
import ktfx.beans.binding.bindingOf
import ktfx.beans.binding.or
import ktfx.beans.binding.stringBindingOf
import ktfx.beans.property.toProperty
import ktfx.collections.emptyBinding
import ktfx.collections.emptyObservableList
import ktfx.collections.toMutableObservableList
import ktfx.collections.toObservableList
import ktfx.coroutines.onAction
import ktfx.layouts.columns
import ktfx.layouts.contextMenu
import ktfx.layouts.menuItem
import ktfx.layouts.tableView
import ktfx.stage.stage
import java.net.URL
import java.util.ResourceBundle
import kotlin.math.ceil

class ReceiptController : Controller(), Refreshable, Addable {

    @FXML lateinit var customerField: TextField
    @FXML lateinit var countBox: CountBox
    @FXML lateinit var statusBox: ChoiceBox<String>
    @FXML lateinit var allDateRadio: RadioButton
    @FXML lateinit var pickDateRadio: RadioButton
    @FXML lateinit var dateBox: DateBox
    @FXML lateinit var receiptPagination: Pagination
    @FXML lateinit var plateTab: Tab
    @FXML lateinit var plateTable: TableView<Plate>
    @FXML lateinit var plateTypeColumn: TableColumn<Plate, String>
    @FXML lateinit var plateTitleColumn: TableColumn<Plate, String>
    @FXML lateinit var plateQtyColumn: TableColumn<Plate, String>
    @FXML lateinit var platePriceColumn: TableColumn<Plate, String>
    @FXML lateinit var plateTotalColumn: TableColumn<Plate, String>
    @FXML lateinit var offsetTab: Tab
    @FXML lateinit var offsetTable: TableView<Offset>
    @FXML lateinit var offsetTypeColumn: TableColumn<Offset, String>
    @FXML lateinit var offsetTitleColumn: TableColumn<Offset, String>
    @FXML lateinit var offsetQtyColumn: TableColumn<Offset, String>
    @FXML lateinit var offsetMinQtyColumn: TableColumn<Offset, String>
    @FXML lateinit var offsetMinPriceColumn: TableColumn<Offset, String>
    @FXML lateinit var offsetExcessPriceColumn: TableColumn<Offset, String>
    @FXML lateinit var offsetTotalColumn: TableColumn<Offset, String>
    @FXML lateinit var otherTab: Tab
    @FXML lateinit var otherTable: TableView<Other>
    @FXML lateinit var otherTitleColumn: TableColumn<Other, String>
    @FXML lateinit var otherQtyColumn: TableColumn<Other, String>
    @FXML lateinit var otherPriceColumn: TableColumn<Other, String>
    @FXML lateinit var otherTotalColumn: TableColumn<Other, String>
    @FXML lateinit var paymentTab: Tab
    @FXML lateinit var paymentTable: TableView<Payment>
    @FXML lateinit var paymentEmployeeColumn: TableColumn<Payment, String>
    @FXML lateinit var paymentDateTimeColumn: TableColumn<Payment, String>
    @FXML lateinit var paymentValueColumn: TableColumn<Payment, String>
    @FXML lateinit var noteTab: Tab
    @FXML lateinit var noteLabel: Label
    @FXML lateinit var coverLabel: Label

    private lateinit var receiptTable: TableView<Receipt>

    override fun initialize(location: URL, resources: ResourceBundle) {
        super.initialize(location, resources)
        refresh()

        countBox.desc = getString(R.string.items)
        statusBox.items = listOf(R.string.any, R.string.unpaid, R.string.paid).map { getString(it) }.toObservableList()
        statusBox.selectionModel.selectFirst()
        pickDateRadio.graphic.disableProperty().bind(!pickDateRadio.selectedProperty())

        plateTypeColumn.typeCell()
        plateTitleColumn.titleCell()
        plateQtyColumn.qtyCell()
        platePriceColumn.priceCell()
        plateTotalColumn.totalCell()
        offsetTypeColumn.typeCell()
        offsetTitleColumn.titleCell()
        offsetQtyColumn.qtyCell()
        offsetMinQtyColumn.minQtyCell()
        offsetMinPriceColumn.minPriceCell()
        offsetExcessPriceColumn.excessPriceCell()
        offsetTotalColumn.totalCell()
        otherTitleColumn.titleCell()
        otherQtyColumn.qtyCell()
        otherPriceColumn.priceCell()
        otherTotalColumn.totalCell()
    }

    override fun refresh() = receiptPagination.pageFactoryProperty()
        .bind(bindingOf(customerField.textProperty(), countBox.countProperty) {
            Callback<Int, Node> { page ->
                receiptTable = tableView {
                    columnResizePolicy = CONSTRAINED_RESIZE_POLICY
                    columns {
                        column<String>(getString(R.string.date)) {
                            setCellValueFactory { it.value.dateTime.toString(PATTERN_DATETIME).toProperty() }
                        }
                        column<Employee>(getString(R.string.employee)) {
                            setCellValueFactory {
                                transaction { Employees.find { id.equal(it.value.employeeId) }.single().toProperty() }
                            }
                        }
                        column<Customer>(getString(R.string.customer)) {
                            setCellValueFactory {
                                transaction { Customers.find { id.equal(it.value.customerId) }.single().toProperty() }
                            }
                        }
                        column<String>(getString(R.string.total)) { totalCell() }
                        column<Boolean>(getString(R.string.paid)) { doneCell { isPaid() } }
                        column<Boolean>(getString(R.string.print)) { doneCell { printed } }
                    }
                    contextMenu {
                        menuItem(getString(R.string.edit)) {
                            bindDisable()
                            onAction {
                                ReceiptDialog(this@ReceiptController, receiptTable.selectionModel.selectedItem)
                                    .showAndWait()
                            }
                        }
                        menuItem(getString(R.string.delete)) {
                            bindDisable()
                            onAction {
                            }
                        }
                    }
                    later {
                        transaction {
                            val receipts = Receipts.find()/*when {
                                customerField.text.isBlank() -> Customers.find()
                                else -> Receipts.find {
                                    name.matches(customerField.text.toRegex(IGNORE_CASE).toPattern())
                                }
                            }*/
                            receiptPagination.pageCount = ceil(receipts.count() / countBox.count.toDouble()).toInt()
                            items = receipts.skip(countBox.count * page).take(countBox.count).toMutableObservableList()
                        }
                    }
                }
                plateTable.bindTable(plateTab) { plates }
                offsetTable.bindTable(offsetTab) { offsets }
                otherTable.bindTable(otherTab) { others }
                paymentTable.bindTable(paymentTab) { payments }
                noteLabel.textProperty().bind(stringBindingOf(receiptTable.selectionModel.selectedItemProperty()) {
                    receipt?.note ?: ""
                })
                coverLabel.visibleProperty().bind(receiptTable.selectionModel.selectedItemProperty().isNull)
                receiptTable
            }
        })

    override fun add() = ReceiptDialog(this).showAndWait().ifPresent {
        transaction {
            it.id = Receipts.insert(it)
            receiptTable.items.add(it)
            receiptTable.selectionModel.selectFirst()
        }
    }

    @FXML fun platePrice() = stage(getString(R.string.plate_price)) {
        initModality(APPLICATION_MODAL)
        val loader = FXMLLoader(getResource(R.layout.controller_price_plate), resources)
        scene = Scene(loader.pane)
        isResizable = false
        loader.controller._employee = _employee
    }.showAndWait()

    @FXML fun offsetPrice() = stage(getString(R.string.offset_price)) {
        initModality(APPLICATION_MODAL)
        val loader = FXMLLoader(getResource(R.layout.controller_price_offset), resources)
        scene = Scene(loader.pane)
        isResizable = false
        loader.controller._employee = _employee
    }.showAndWait()

    private inline val receipt: Receipt? get() = receiptTable.selectionModel.selectedItem

    private fun <S> TableView<S>.bindTable(tab: Tab, target: Receipt.() -> List<S>) {
        itemsProperty().bind(bindingOf(receiptTable.selectionModel.selectedItemProperty()) {
            receipt?.target()?.toObservableList() ?: emptyObservableList()
        })
        tab.graphicProperty().bind(bindingOf(itemsProperty()) {
            when {
                items.isEmpty() -> null
                else -> Label(items.size.toString())
            }
        })
    }

    private fun MenuItem.bindDisable() = later {
        disableProperty().bind(receiptTable.selectionModel.selectedItems.emptyBinding() or
            !isFullAccess.toProperty())
    }
}