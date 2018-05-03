package com.hendraanggrian.openpss.ui

import com.hendraanggrian.openpss.db.Document
import com.hendraanggrian.openpss.db.schemas.Employee.Role.MANAGER
import com.hendraanggrian.openpss.db.transaction
import com.hendraanggrian.openpss.util.yesNoAlert
import javafx.fxml.FXML
import javafx.scene.control.Button
import javafx.scene.control.TableView
import kotlinx.nosql.mongodb.DocumentSchema
import ktfx.application.later
import ktfx.beans.property.toProperty
import ktfx.beans.value.or
import ktfx.collections.toMutableObservableList
import java.net.URL
import java.util.ResourceBundle

/**
 * Base controller for editing DAO in simple table with refresh, add, and delete button.
 *
 * @see [com.hendraanggrian.openpss.ui.invoice.PriceController]
 * @see [com.hendraanggrian.openpss.ui.invoice.PlatePriceController]
 * @see [com.hendraanggrian.openpss.ui.invoice.OffsetPriceController]
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
            transaction {
                deleteButton.disableProperty().bind(table.selectionModel.selectedItemProperty().isNull or
                    !login.isAtLeast(MANAGER).toProperty())
            }
        }
    }

    override fun refresh() {
        table.items = transaction { schema().toMutableObservableList() }
    }

    @FXML abstract fun add()

    @FXML fun delete() = yesNoAlert {
        table.selectionModel.selectedItem.let {
            transaction { schema -= it }
            table.items.remove(it)
        }
    }
}