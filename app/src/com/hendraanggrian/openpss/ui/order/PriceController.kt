package com.hendraanggrian.openpss.ui.order

import com.hendraanggrian.openpss.R
import com.hendraanggrian.openpss.collections.isNotEmpty
import com.hendraanggrian.openpss.db.Named
import com.hendraanggrian.openpss.db.NamedDocumentSchema
import com.hendraanggrian.openpss.db.transaction
import com.hendraanggrian.openpss.ui.SimpleTableController
import javafx.fxml.FXML
import javafx.scene.control.TableColumn
import kotlinfx.beans.property.toProperty
import kotlinfx.scene.control.errorAlert
import kotlinfx.scene.control.inputDialog
import kotlinx.nosql.equal

abstract class PriceController<D : Named<S>, S : NamedDocumentSchema<D>>(schema: S) : SimpleTableController<D, S>(schema) {

    abstract fun newPrice(name: String): D

    @FXML lateinit var nameColumn: TableColumn<D, String>

    override fun initialize() {
        super.initialize()
        nameColumn.setCellValueFactory { it.value.name.toProperty() }
    }

    override fun add() = inputDialog(getString(if (this is PlatePriceController) R.string.add_plate else R.string.add_offset), null) {
        contentText = getString(R.string.name)
        editor.promptText = getString(R.string.name)
    }.showAndWait().ifPresent { name ->
        transaction @Suppress("IMPLICIT_CAST_TO_ANY") {
            if (schema.find { this.name.equal(name) }.isNotEmpty()) errorAlert(getString(R.string.name_taken)).showAndWait() else {
                val price = newPrice(name)
                price.id = schema.insert(price)
                table.items.add(price)
            }
        }
    }
}