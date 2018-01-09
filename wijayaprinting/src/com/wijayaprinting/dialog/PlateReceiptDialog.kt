package com.wijayaprinting.dialog

import com.wijayaprinting.App.Companion.EMPLOYEE
import com.wijayaprinting.R
import com.wijayaprinting.Resourced
import com.wijayaprinting.dao.Customer
import com.wijayaprinting.dao.Plate
import com.wijayaprinting.dao.PlateReceipt
import com.wijayaprinting.data.ProductOrder
import com.wijayaprinting.scene.PATTERN_DATE
import com.wijayaprinting.utils.addConsumedEventFilter
import com.wijayaprinting.utils.expose
import com.wijayaprinting.utils.gap
import javafx.beans.property.ObjectProperty
import javafx.beans.property.SimpleObjectProperty
import javafx.event.ActionEvent.ACTION
import javafx.scene.control.ButtonBar.ButtonData.HELP
import javafx.scene.control.ButtonBar.ButtonData.HELP_2
import javafx.scene.control.ButtonType.CANCEL
import javafx.scene.control.ButtonType.OK
import javafx.scene.control.Dialog
import javafx.scene.control.Label
import javafx.scene.control.TableColumn
import javafx.scene.control.TableView
import javafx.scene.control.cell.TextFieldTableCell.forTableColumn
import javafx.scene.image.ImageView
import javafx.scene.text.Font.loadFont
import kotfx.*
import org.joda.time.DateTime.now
import java.math.BigDecimal
import java.math.BigDecimal.ZERO

class PlateReceiptDialog(val resourced: Resourced) : Dialog<PlateReceipt>(), Resourced by resourced {

    private lateinit var tableView: TableView<ProductOrder<Plate>>
    private lateinit var totalLabel: Label

    private var customerProperty: ObjectProperty<Customer> = SimpleObjectProperty()

    init {
        title = getString(R.string.add_plate_receipt)
        headerText = getString(R.string.add_plate_receipt)
        graphic = ImageView(R.png.ic_document)
        content = gridPane {
            gap(8)
            label(getString(R.string.date)) col 0 row 0
            label(now().toString(PATTERN_DATE)) { font = loadFont(latoBold, 13.0) } col 1 row 0
            label(getString(R.string.employee)) col 0 row 1
            label(EMPLOYEE) { font = loadFont(latoBold, 13.0) } col 1 row 1
            label(getString(R.string.customer)) col 0 row 2
            button {
                textProperty() bind stringBindingOf(customerProperty) { customerProperty.value?.toString() ?: getString(R.string.search_customer) }
                setOnAction { SearchCustomerDialog(resourced).showAndWait().ifPresent { customerProperty.set(it) } }
            } col 1 row 2
            label(getString(R.string.note)) col 0 row 3
            textArea { prefHeight = 120.0 } col 1 row 3
            label(getString(R.string.orders)) col 0 row 4
            tableView = tableView<ProductOrder<Plate>> {
                prefHeight = 240.0
                isEditable = true
                columns.addAll(
                        TableColumn<ProductOrder<Plate>, String>(getString(R.string.plate)).apply {
                            setCellValueFactory { expose { it.value.product.id.value }.asProperty() }
                            isEditable = false
                        },
                        TableColumn<ProductOrder<Plate>, Int>(getString(R.string.quantity)).apply {
                            setCellValueFactory { it.value.qty.asObservable() }
                            cellFactory = forTableColumn<ProductOrder<Plate>, Int>(stringConverter({ it.toIntOrNull() ?: 0 }))
                            setOnEditCommit { event -> event.rowValue.qty.set(event.newValue) }
                        },
                        TableColumn<ProductOrder<Plate>, BigDecimal>(getString(R.string.price)).apply {
                            setCellValueFactory { it.value.price.asObservable() }
                            cellFactory = forTableColumn<ProductOrder<Plate>, BigDecimal>(stringConverter({ it.toBigDecimalOrNull() ?: ZERO }))
                            setOnEditCommit { event -> expose { event.rowValue.price.set(event.newValue) } }
                        },
                        TableColumn<ProductOrder<Plate>, BigDecimal>(getString(R.string.total)).apply {
                            setCellValueFactory { it.value.total }
                            isEditable = false
                        }
                )
            } col 1 row 4
            label(getString(R.string.total)) col 0 row 5
            totalLabel = label(ZERO.toString()) { font = loadFont(latoBold, 13.0) } col 1 row 5
        }
        button(getString(R.string.add), HELP).addConsumedEventFilter(ACTION) {
            choiceDialog(expose { Plate.all().toList() }).showAndWait().ifPresent { plate ->
                tableView.items.add(ProductOrder(plate).apply { price.set(plate.price) })
                rebindTotalLabel()
            }
        }
        button(getString(R.string.delete), HELP_2).apply {
            disableProperty() bind tableView.selectionModel.selectedItemProperty().isNull
            addConsumedEventFilter(ACTION) {
                tableView.items.remove(tableView.selectionModel.selectedItem)
                rebindTotalLabel()
            }
        }
        button(CANCEL)
        button(OK)
        setResultConverter { null }
    }

    private fun rebindTotalLabel() = totalLabel.textProperty() rebind stringBindingOf(tableView.items, *tableView.items.map { it.total }.toTypedArray()) {
        tableView.items.map { it.total.value }.sumByDouble { it.toDouble() }.toString()
    }
}