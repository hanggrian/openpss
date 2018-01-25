package com.wijayaprinting.ui.order.price

import com.wijayaprinting.R
import com.wijayaprinting.collections.isNotEmpty
import com.wijayaprinting.db.Named
import com.wijayaprinting.db.NamedDocumentSchema
import com.wijayaprinting.db.transaction
import com.wijayaprinting.ui.Controller
import com.wijayaprinting.ui.Refreshable
import javafx.fxml.FXML
import javafx.scene.control.Button
import javafx.scene.control.ButtonType.NO
import javafx.scene.control.ButtonType.YES
import javafx.scene.control.TableColumn
import javafx.scene.control.TableView
import kotfx.*
import kotlinx.nosql.equal
import kotlinx.nosql.id

abstract class PriceController<D : Named<S>, S : NamedDocumentSchema<D>>(
        private val schema: S,
        private val addDialogHeaderId: String
) : Controller(), Refreshable {

    abstract fun newPrice(name: String): D

    @FXML lateinit var deleteButton: Button
    @FXML lateinit var priceTable: TableView<D>
    @FXML lateinit var nameColumn: TableColumn<D, String>

    override fun initialize() {
        refresh()
        runLater { deleteButton.disableProperty() bind (priceTable.selectionModel.selectedItemProperty().isNull or !isFullAccess.asProperty()) }
        nameColumn.setCellValueFactory { it.value.name.asProperty() }
    }

    override fun refresh() {
        priceTable.items = transaction { schema.find().toMutableObservableList() }
    }

    @FXML
    fun add() = inputDialog {
        title = getString(addDialogHeaderId)
        headerText = getString(addDialogHeaderId)
        contentText = getString(R.string.name)
        editor.promptText = getString(R.string.name)
    }.showAndWait().ifPresent { name ->
        transaction @Suppress("IMPLICIT_CAST_TO_ANY") {
            if (schema.find { this.name.equal(name) }.isNotEmpty) errorAlert(getString(R.string.name_taken)).showAndWait() else {
                val price = newPrice(name)
                price.id = schema.insert(price)
                priceTable.items.add(price)
            }
        }
    }

    @FXML
    fun delete() = confirmAlert(getString(R.string.are_you_sure), YES, NO)
            .showAndWait()
            .filter { it == YES }
            .ifPresent {
                priceTable.selectionModel.selectedItem.let { price ->
                    transaction { schema.find { id.equal(price.id.value) }.remove() }
                    priceTable.items.remove(price)
                }
            }
}