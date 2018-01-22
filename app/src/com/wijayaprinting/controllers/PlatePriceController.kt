package com.wijayaprinting.controllers

import com.wijayaprinting.R
import com.wijayaprinting.base.Refreshable
import com.wijayaprinting.db.Plate
import com.wijayaprinting.db.Plates
import com.wijayaprinting.db.transaction
import javafx.fxml.FXML
import javafx.scene.control.Button
import javafx.scene.control.ButtonType.NO
import javafx.scene.control.ButtonType.YES
import javafx.scene.control.TableColumn
import javafx.scene.control.TableView
import javafx.scene.control.cell.TextFieldTableCell.forTableColumn
import kotfx.*
import kotlinx.nosql.equal
import kotlinx.nosql.id
import kotlinx.nosql.update

class PlatePriceController : Controller(), Refreshable {

    @FXML lateinit var deleteButton: Button

    @FXML lateinit var plateTable: TableView<Plate>
    @FXML lateinit var nameColumn: TableColumn<Plate, String>
    @FXML lateinit var priceColumn: TableColumn<Plate, Double>

    @FXML
    override fun initialize() {
        deleteButton.isDisable = !isFullAccess

        nameColumn.setCellValueFactory { it.value.name.asProperty() }
        priceColumn.setCellValueFactory { it.value.price.asProperty().asObservable() }
        priceColumn.cellFactory = forTableColumn<Plate, Double>(stringConverter({ it.toDoubleOrNull() ?: 0.0 }))
        priceColumn.setOnEditCommit { event ->
            transaction { Plates.find { name.equal(event.rowValue.name) }.projection { price }.update(event.newValue) }
            event.rowValue.price = event.newValue
        }
        refresh()
    }

    @FXML
    override fun refresh() {
        plateTable.items = transaction { Plates.find().toMutableObservableList() }
    }

    @FXML
    fun addOnAction() = inputDialog {
        title = getString(R.string.add_plate)
        headerText = getString(R.string.add_plate)
        contentText = getString(R.string.name)
        editor.promptText = getString(R.string.plate)
    }.showAndWait().ifPresent { name ->
        val plate = Plate(name)
        plate.id = transaction { Plates.insert(plate) }!!
        plateTable.items.add(plate)
    }

    @FXML
    fun deleteOnAction() = confirmAlert(getString(R.string.are_you_sure), YES, NO)
            .showAndWait()
            .filter { it == YES }
            .ifPresent {
                plateTable.selectionModel.selectedItem.let { plate ->
                    transaction { Plates.find { id.equal(plate.id.value) }.remove() }
                    plateTable.items.remove(plate)
                }
            }
}