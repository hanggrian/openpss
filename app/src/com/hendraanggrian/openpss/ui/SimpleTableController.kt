package com.hendraanggrian.openpss.ui

import com.hendraanggrian.openpss.R
import com.hendraanggrian.openpss.db.Document
import com.hendraanggrian.openpss.db.findByDoc
import com.hendraanggrian.openpss.db.transaction
import com.hendraanggrian.openpss.utils.yesNoAlert
import javafx.fxml.FXML
import javafx.scene.control.Button
import javafx.scene.control.TableView
import kotlinx.nosql.mongodb.DocumentSchema
import ktfx.application.later
import ktfx.beans.binding.or
import ktfx.beans.property.toProperty
import ktfx.collections.toMutableObservableList
import java.net.URL
import java.util.ResourceBundle

/**
 * Base controller for editing DAO in simple table with refresh, add, and delete button.
 *
 * @see [com.hendraanggrian.openpss.ui.receipt.PriceController]
 * @see [com.hendraanggrian.openpss.ui.receipt.PlatePriceController]
 * @see [com.hendraanggrian.openpss.ui.receipt.OffsetPriceController]
 * @see [com.hendraanggrian.openpss.ui.wage.WageRecessController]
 */
abstract class SimpleTableController<D : Document<S>, S : DocumentSchema<D>>(
    protected val schema: S
) : Controller(), Refreshable {

    @FXML lateinit var deleteButton: Button
    @FXML lateinit var table: TableView<D>

    override fun initialize(location: URL, resources: ResourceBundle) {
        super.initialize(location, resources)
        later {
            deleteButton.disableProperty().bind(table.selectionModel.selectedItemProperty().isNull or
                !isFullAccess.toProperty())
        }
    }

    override fun refresh() {
        table.items = transaction { schema.find().toMutableObservableList() }
    }

    @FXML abstract fun add()

    @FXML fun delete() = yesNoAlert(getString(R.string.are_you_sure)) {
        table.selectionModel.selectedItem.let {
            transaction { findByDoc(schema, it).remove() }
            table.items.remove(it)
        }
    }
}