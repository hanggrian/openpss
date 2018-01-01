package com.wijayaprinting.manager.controller

import com.hendraanggrian.rxexposed.SQLSingles
import com.wijayaprinting.data.Plate
import io.reactivex.rxkotlin.subscribeBy
import javafx.fxml.FXML
import javafx.scene.control.Button
import javafx.scene.control.TableColumn
import javafx.scene.control.TableView
import kotfx.asObservable
import kotfx.asProperty
import java.math.BigDecimal

class PlatePriceController : Controller() {

    @FXML lateinit var addButton: Button

    @FXML lateinit var tableView: TableView<Plate>
    @FXML lateinit var idColumn: TableColumn<Plate, String>
    @FXML lateinit var priceColumn: TableColumn<Plate, BigDecimal>
    @FXML lateinit var qtyColumn: TableColumn<Plate, Int>

    @FXML
    fun initialize() {
        SQLSingles.transaction { Plate.all() }
                .subscribeBy({}) { plates ->
                    // tableView.items =
                }

        idColumn.setCellValueFactory { it.value.id.value.asProperty() }
        priceColumn.setCellValueFactory { it.value.price.asProperty() }
        qtyColumn.setCellValueFactory { it.value.qty.asProperty().asObservable() }
    }

    @FXML
    fun addOnAction() {

    }
}