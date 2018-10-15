package com.hendraanggrian.openpss.ui.main.edit.price

import com.hendraanggrian.openpss.R
import com.hendraanggrian.openpss.popup.dialog.TableDialog
import com.hendraanggrian.openpss.popup.popover.InputPopover
import com.hendraanggrian.openpss.control.stringCell
import com.hendraanggrian.openpss.db.Document
import com.hendraanggrian.openpss.db.Named
import com.hendraanggrian.openpss.db.NamedSchema
import com.hendraanggrian.openpss.db.schemas.Employee
import com.hendraanggrian.openpss.db.transaction
import com.hendraanggrian.openpss.i18n.Resourced
import com.hendraanggrian.openpss.util.getStyle
import com.hendraanggrian.openpss.util.isNotEmpty
import kotlinx.nosql.equal
import kotlinx.nosql.mongodb.DocumentSchema
import ktfx.scene.control.errorAlert

abstract class EditPriceDialog<D, S>(
    resourced: Resourced,
    headerId: String,
    schema: S,
    employee: Employee
) : TableDialog<D, S>(resourced, headerId, schema, employee)
    where D : Document<S>, D : Named, S : DocumentSchema<D>, S : NamedSchema {

    abstract fun newPrice(name: String): D

    init {
        @Suppress("LeakingThis")
        getString(R.string.name)<String> {
            minWidth = 96.0
            stringCell { name }
        }
    }

    override fun add() = InputPopover(
        this, when {
            this is EditPlatePriceDialog -> R.string.add_plate
            else -> R.string.add_offset
        }
    ).showAt(addButton) { name ->
        transaction @Suppress("IMPLICIT_CAST_TO_ANY") {
            when {
                schema { it.name.equal(name) }.isNotEmpty() -> errorAlert(getString(R.string.name_taken)) {
                    dialogPane.stylesheets += getStyle(R.style.openpss)
                }.show()
                else -> {
                    val price = newPrice(name)
                    price.id = schema.insert(price)
                    table.items.add(price)
                }
            }
        }
    }
}