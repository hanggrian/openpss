package com.wijayaprinting.ui.order

import com.wijayaprinting.R
import com.wijayaprinting.collections.isNotEmpty
import com.wijayaprinting.db.Named
import com.wijayaprinting.db.NamedDocumentSchema
import com.wijayaprinting.db.transaction
import com.wijayaprinting.ui.SimpleTableController
import javafx.fxml.FXML
import javafx.scene.control.TableColumn
import kotfx.errorAlert
import kotfx.inputDialog
import kotfx.toProperty
import kotlinx.nosql.equal

abstract class PriceController<D : Named<S>, S : NamedDocumentSchema<D>>(schema: S) : SimpleTableController<D, S>(schema) {

    abstract fun newPrice(name: String): D

    @FXML lateinit var nameColumn: TableColumn<D, String>

    override fun initialize() {
        super.initialize()
        nameColumn.setCellValueFactory { it.value.name.toProperty() }
    }

    override fun add() = getString(if (this is PlatePriceController) R.string.add_plate else R.string.add_offset).let { header ->
        inputDialog {
            title = header
            headerText = header
            contentText = getString(R.string.name)
            editor.promptText = getString(R.string.name)
        }.showAndWait().ifPresent { name ->
            transaction @Suppress("IMPLICIT_CAST_TO_ANY") {
                if (schema.find { this.name.equal(name) }.isNotEmpty) errorAlert(getString(R.string.name_taken)).showAndWait() else {
                    val price = newPrice(name)
                    price.id = schema.insert(price)
                    table.items.add(price)
                }
            }
        }
    }
}