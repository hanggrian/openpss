package com.hendraanggrian.openpss.ui.receipt

import com.hendraanggrian.openpss.R
import com.hendraanggrian.openpss.collections.isNotEmpty
import com.hendraanggrian.openpss.db.NamedDocument
import com.hendraanggrian.openpss.db.NamedDocumentSchema
import com.hendraanggrian.openpss.db.transaction
import com.hendraanggrian.openpss.ui.SimpleTableController
import javafx.fxml.FXML
import javafx.scene.control.TableColumn
import kotlinx.nosql.equal
import ktfx.beans.property.toProperty
import ktfx.scene.control.errorAlert
import ktfx.scene.control.inputDialog
import java.net.URL
import java.util.ResourceBundle

abstract class PriceController<D : NamedDocument<S>, S : NamedDocumentSchema<D>>(schema: S)
    : SimpleTableController<D, S>(schema) {

    abstract fun newPrice(name: String): D

    @FXML lateinit var nameColumn: TableColumn<D, String>

    override fun initialize(location: URL, resources: ResourceBundle) {
        super.initialize(location, resources)
        nameColumn.setCellValueFactory { it.value.name.toProperty() }
    }

    override fun add() = inputDialog(title = getString(when {
        this is PlatePriceController -> R.string.add_plate
        else -> R.string.add_offset
    })) {
        editor.promptText = getString(R.string.name)
    }.showAndWait().ifPresent { name ->
        transaction @Suppress("IMPLICIT_CAST_TO_ANY") {
            when {
                schema.find { this.name.equal(name) }.isNotEmpty() ->
                    errorAlert(getString(R.string.name_taken)).showAndWait()
                else -> {
                    val price = newPrice(name)
                    price.id = schema.insert(price)
                    table.items.add(price)
                }
            }
        }
    }
}