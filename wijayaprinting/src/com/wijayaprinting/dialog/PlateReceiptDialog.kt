package com.wijayaprinting.dialog

import com.wijayaprinting.App.Companion.EMPLOYEE
import com.wijayaprinting.R
import com.wijayaprinting.Resourced
import com.wijayaprinting.dao.Plate
import com.wijayaprinting.dao.PlateOrder
import com.wijayaprinting.dao.PlateReceipt
import com.wijayaprinting.scene.PATTERN_DATE
import com.wijayaprinting.utils.addConsumedEventFilter
import com.wijayaprinting.utils.expose
import com.wijayaprinting.utils.gap
import javafx.event.ActionEvent.ACTION
import javafx.scene.control.ButtonBar.ButtonData.HELP
import javafx.scene.control.ButtonBar.ButtonData.HELP_2
import javafx.scene.control.ButtonType.CANCEL
import javafx.scene.control.ButtonType.OK
import javafx.scene.control.Dialog
import javafx.scene.control.TableColumn
import javafx.scene.control.TableView
import javafx.scene.control.cell.TextFieldTableCell.forTableColumn
import javafx.scene.image.ImageView
import javafx.scene.text.Font.loadFont
import kotfx.*
import org.joda.time.DateTime.now
import java.math.BigDecimal
import java.math.BigDecimal.ZERO

class PlateReceiptDialog(val resourced: Resourced, val prefill: PlateReceipt) : Dialog<PlateReceipt>(), Resourced by resourced {

    private lateinit var tableView: TableView<PlateOrder>

    init {
        title = getString(R.string.add_plate_receipt)
        headerText = getString(R.string.add_plate_receipt)
        graphic = ImageView(R.png.ic_document)
        content = gridPane {
            gap(8)
            label(getString(R.string.id)) col 0 row 0
            label(prefill.id.value.toString()) { font = loadFont(latoBold, 13.0) } col 1 row 0
            label(getString(R.string.date)) col 0 row 1
            label(now().toString(PATTERN_DATE)) { font = loadFont(latoBold, 13.0) } col 1 row 1
            label(getString(R.string.employee)) col 0 row 2
            label(EMPLOYEE) { font = loadFont(latoBold, 13.0) } col 1 row 2
            label(getString(R.string.customer)) col 0 row 3
            label(expose { prefill.customer.toString() }) { font = loadFont(latoBold, 13.0) } col 1 row 3
            label(getString(R.string.note)) col 0 row 4
            textArea { prefHeight = 120.0 } col 1 row 4
            label(getString(R.string.orders)) col 0 row 5
            tableView = tableView<PlateOrder> {
                prefHeight = 240.0
                isEditable = true
                columns.add(TableColumn<PlateOrder, String>(getString(R.string.plate)).apply {
                    setCellValueFactory { expose { it.value.plate.id.value }.asProperty() }
                    isEditable = false
                })
                columns.add(TableColumn<PlateOrder, Int>(getString(R.string.quantity)).apply {
                    // setCellValueFactory { it.value.qty.asProperty().asObservable() }
                    cellFactory = forTableColumn<PlateOrder, Int>(stringConverter({ it.toIntOrNull() ?: 0 }))
                    setOnEditCommit { event -> expose { event.rowValue.qty = event.newValue } }
                })
                columns.add(TableColumn<PlateOrder, BigDecimal>(getString(R.string.price)).apply {
                    // setCellValueFactory { it.value.price.asProperty().asObservable() }
                    cellFactory = forTableColumn<PlateOrder, BigDecimal>(stringConverter({ it.toBigDecimalOrNull() ?: ZERO }))
                    setOnEditCommit { event -> expose { event.rowValue.price = event.newValue } }
                })
                columns.add(TableColumn<PlateOrder, BigDecimal>(getString(R.string.total)).apply {
                    setCellValueFactory { it.value.total.asProperty() }
                    isEditable = false
                })
            } col 1 row 5
            label(getString(R.string.total)) col 0 row 6
            label { font = loadFont(latoBold, 13.0) } col 1 row 6
        }
        button(getString(R.string.add), HELP).addConsumedEventFilter(ACTION) {
            choiceDialog(expose { Plate.all().toList() }) { }.showAndWait().ifPresent { _plate ->
                tableView.items.add(expose {
                    PlateOrder.new {
                        receipt = prefill
                        plate = _plate
                        qty = 0
                        price = _plate.price
                    }
                })
            }
        }
        button(getString(R.string.delete), HELP_2).apply {
            disableProperty() bind tableView.selectionModel.selectedItemProperty().isNull
            addConsumedEventFilter(ACTION) {

            }
        }
        button(CANCEL)
        button(OK)
        setResultConverter { null }
    }
}