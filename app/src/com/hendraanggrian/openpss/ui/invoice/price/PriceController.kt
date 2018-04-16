package com.hendraanggrian.openpss.ui.invoice.price

import com.hendraanggrian.openpss.R
import com.hendraanggrian.openpss.db.Document
import com.hendraanggrian.openpss.db.Named
import com.hendraanggrian.openpss.db.NamedSchema
import com.hendraanggrian.openpss.db.transaction
import com.hendraanggrian.openpss.ui.SimpleTableController
import com.hendraanggrian.openpss.utils.isNotEmpty
import com.hendraanggrian.openpss.utils.stringCell
import javafx.fxml.FXML
import javafx.scene.control.TableColumn
import kotlinx.nosql.equal
import kotlinx.nosql.mongodb.DocumentSchema
import ktfx.scene.control.errorAlert
import ktfx.scene.control.inputDialog
import java.net.URL
import java.util.ResourceBundle

abstract class PriceController<D, S>(schema: S) : SimpleTableController<D, S>(schema)
    where D : Document<S>, D : Named,
          S : DocumentSchema<D>, S : NamedSchema {

    abstract fun newPrice(name: String): D

    @FXML lateinit var nameColumn: TableColumn<D, String>

    override fun initialize(location: URL, resources: ResourceBundle) {
        super.initialize(location, resources)
        nameColumn.stringCell { name }
    }

    override fun add() = inputDialog(title = getString(when {
        this is PlatePriceController -> R.string.add_plate
        else -> R.string.add_offset
    })) {
        editor.promptText = getString(R.string.name)
    }.showAndWait().ifPresent { name ->
        transaction @Suppress("IMPLICIT_CAST_TO_ANY") {
            when {
                schema.find { this.name.equal(name) }.isNotEmpty() -> errorAlert(getString(R.string.name_taken)).show()
                else -> {
                    val price = newPrice(name)
                    price.id = schema.insert(price)
                    table.items.add(price)
                }
            }
        }
    }
}