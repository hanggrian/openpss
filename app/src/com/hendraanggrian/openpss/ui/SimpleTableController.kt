package com.hendraanggrian.openpss.ui

import com.hendraanggrian.openpss.R
import com.hendraanggrian.openpss.db.Ided
import com.hendraanggrian.openpss.db.transaction
import javafx.fxml.FXML
import javafx.scene.control.Button
import javafx.scene.control.ButtonType.NO
import javafx.scene.control.ButtonType.YES
import javafx.scene.control.TableView
import kotlinfx.application.later
import kotlinfx.beans.binding.or
import kotlinfx.beans.property.toProperty
import kotlinfx.collections.toMutableObservableList
import kotlinfx.scene.control.confirmAlert
import kotlinx.nosql.equal
import kotlinx.nosql.id
import kotlinx.nosql.mongodb.DocumentSchema

/**
 * Base controller for editing DAO in simple table with refresh, add, and delete button.
 *
 * @see [com.hendraanggrian.openpss.ui.order.PriceController]
 * @see [com.hendraanggrian.openpss.ui.order.PlatePriceController]
 * @see [com.hendraanggrian.openpss.ui.order.OffsetPriceController]
 * @see [com.hendraanggrian.openpss.ui.wage.WageRecessController]
 */
abstract class SimpleTableController<D : Ided<S>, S : DocumentSchema<D>>(protected val schema: S) : Controller(), Refreshable {

    @FXML lateinit var deleteButton: Button
    @FXML lateinit var table: TableView<D>

    override fun initialize() {
        refresh()
        later { deleteButton.disableProperty().bind(table.selectionModel.selectedItemProperty().isNull or !isFullAccess.toProperty()) }
    }

    override fun refresh() {
        table.items = transaction { schema.find().toMutableObservableList() }
    }

    @FXML
    abstract fun add()

    @FXML
    fun delete() = confirmAlert(getString(R.string.are_you_sure), YES, NO)
        .showAndWait()
        .filter { it == YES }
        .ifPresent {
            table.selectionModel.selectedItem.let {
                transaction { schema.find { id.equal(it.id.value) }.remove() }
                table.items.remove(it)
            }
        }
}