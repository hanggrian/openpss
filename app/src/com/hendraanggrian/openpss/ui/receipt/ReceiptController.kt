package com.hendraanggrian.openpss.ui.receipt

import com.hendraanggrian.openpss.R
import com.hendraanggrian.openpss.converter.MoneyStringConverter
import com.hendraanggrian.openpss.db.schema.Customer
import com.hendraanggrian.openpss.db.schema.Customers
import com.hendraanggrian.openpss.db.schema.Employee
import com.hendraanggrian.openpss.db.schema.Employees
import com.hendraanggrian.openpss.db.schema.Receipt
import com.hendraanggrian.openpss.db.schema.Receipts
import com.hendraanggrian.openpss.db.transaction
import com.hendraanggrian.openpss.scene.control.CountBox
import com.hendraanggrian.openpss.time.PATTERN_DATETIME
import com.hendraanggrian.openpss.ui.Addable
import com.hendraanggrian.openpss.ui.Controller
import com.hendraanggrian.openpss.ui.Refreshable
import com.hendraanggrian.openpss.ui.controller
import com.hendraanggrian.openpss.ui.pane
import com.hendraanggrian.openpss.util.getResource
import javafx.fxml.FXML
import javafx.fxml.FXMLLoader
import javafx.geometry.Pos.CENTER_RIGHT
import javafx.scene.Node
import javafx.scene.Scene
import javafx.scene.control.ChoiceBox
import javafx.scene.control.Pagination
import javafx.scene.control.TableView
import javafx.scene.control.TableView.CONSTRAINED_RESIZE_POLICY
import javafx.scene.control.TextField
import javafx.stage.Modality.APPLICATION_MODAL
import javafx.util.Callback
import kotlinx.nosql.equal
import kotlinx.nosql.id
import ktfx.application.later
import ktfx.beans.binding.bindingOf
import ktfx.beans.property.toProperty
import ktfx.collections.toMutableObservableList
import ktfx.collections.toObservableList
import ktfx.layouts.columns
import ktfx.layouts.tableView
import ktfx.stage.stage
import ktfx.styles.labeledStyle
import java.net.URL
import java.util.ResourceBundle
import kotlin.math.ceil

class ReceiptController : Controller(), Refreshable, Addable {

    @FXML lateinit var customerField: TextField
    @FXML lateinit var countBox: CountBox
    @FXML lateinit var statusBox: ChoiceBox<String>
    @FXML lateinit var receiptPagination: Pagination

    private lateinit var receiptTable: TableView<Receipt>
    private val moneyConverter = MoneyStringConverter()

    override fun initialize(location: URL, resources: ResourceBundle) {
        super.initialize(location, resources)
        refresh()

        countBox.desc = getString(R.string.items)
        statusBox.items = listOf(R.string.any, R.string.unpaid, R.string.paid).map { getString(it) }.toObservableList()
        statusBox.selectionModel.selectFirst()
    }

    override fun refresh() = receiptPagination.pageFactoryProperty().bind(
        bindingOf(customerField.textProperty(), countBox.countProperty) {
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
                        column<String>(getString(R.string.total)) {
                            setCellValueFactory { moneyConverter.toString(it.value.total).toProperty() }
                            style = labeledStyle { alignment = CENTER_RIGHT }
                        }
                        column<Boolean>(getString(R.string.paid)) {
                            setCellValueFactory { it.value.isPaid().toProperty() }
                        }
                        column<Boolean>(getString(R.string.print)) {
                            setCellValueFactory { it.value.printed.toProperty() }
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
                receiptTable
            }
        })

    override fun add() = ReceiptDialog(this, _employee).showAndWait().ifPresent { receipt ->
        transaction {
            receipt.id = Receipts.insert(receipt)
            receiptTable.items.add(0, receipt)
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
}