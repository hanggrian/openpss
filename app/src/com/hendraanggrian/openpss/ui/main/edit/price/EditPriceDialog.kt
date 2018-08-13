package com.hendraanggrian.openpss.ui.main.edit.price

import com.hendraanggrian.openpss.R
import com.hendraanggrian.openpss.control.dialog.TableDialog
import com.hendraanggrian.openpss.control.popover.InputPopover
import com.hendraanggrian.openpss.db.Document
import com.hendraanggrian.openpss.db.Named
import com.hendraanggrian.openpss.db.NamedSchema
import com.hendraanggrian.openpss.db.schemas.Employee
import com.hendraanggrian.openpss.db.transaction
import com.hendraanggrian.openpss.i18n.Resourced
import com.hendraanggrian.openpss.util.getStyle
import com.hendraanggrian.openpss.util.isNotEmpty
import com.hendraanggrian.openpss.util.stringCell
import javafxx.scene.control.styledErrorAlert
import kotlinx.nosql.equal
import kotlinx.nosql.mongodb.DocumentSchema

abstract class EditPriceDialog<D, S>(
    schema: S,
    resourced: Resourced,
    employee: Employee,
    headerId: String? = null,
    graphicId: String? = null
) : TableDialog<D, S>(schema, resourced, employee, headerId, graphicId)
    where D : Document<S>, D : Named, S : DocumentSchema<D>, S : NamedSchema {

    abstract fun newPrice(name: String): D

    init {
        getString(R.string.name)<String> {
            minWidth = 96.0
            stringCell { name }
        }
    }

    override fun add() = InputPopover(this, when {
        this is EditPlatePriceDialog -> R.string.add_plate
        else -> R.string.add_offset
    }).showAt(addButton) { name ->
        transaction @Suppress("IMPLICIT_CAST_TO_ANY") {
            when {
                schema { it.name.equal(name) }.isNotEmpty() ->
                    styledErrorAlert(getStyle(R.style.openpss), getString(R.string.name_taken)).show()
                else -> {
                    val price = newPrice(name)
                    price.id = schema.insert(price)
                    table.items.add(price)
                }
            }
        }
    }
}