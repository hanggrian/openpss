package com.hanggrian.openpss.ui.price

import com.hanggrian.openpss.Context
import com.hanggrian.openpss.OpenPssApp
import com.hanggrian.openpss.R
import com.hanggrian.openpss.db.Document
import com.hanggrian.openpss.db.Named
import com.hanggrian.openpss.db.NamedSchema
import com.hanggrian.openpss.db.transaction
import com.hanggrian.openpss.popup.dialog.TableDialog
import com.hanggrian.openpss.popup.popover.InputPopover
import com.hanggrian.openpss.util.isNotEmpty
import com.hanggrian.openpss.util.stringCell
import kotlinx.nosql.equal
import kotlinx.nosql.mongodb.DocumentSchema
import ktfx.controls.TableColumnScope
import ktfx.jfoenix.controls.jfxSnackbar
import ktfx.jfoenix.controls.show

abstract class EditPriceDialog<D, S>(context: Context, headerId: String, schema: S) :
    TableDialog<D, S>(context, headerId, schema)
    where D : Document<S>, D : Named, S : DocumentSchema<D>, S : NamedSchema {
    override fun onColumns(columns: TableColumnScope<D>) {
        columns.append(getString(R.string_name)) {
            minWidth = 96.0
            stringCell { name }
        }
    }

    abstract fun newPrice(name: String): D

    override fun add() =
        InputPopover(
            this,
            when (this) {
                is EditPlatePriceDialog -> R.string_add_plate_price
                is EditOffsetPrintPriceDialog -> R.string_add_offset_price
                else -> R.string_add_digital_price
            },
        ).show(addButton) { name ->
            transaction {
                when {
                    schema { it.name.equal(name) }.isNotEmpty() ->
                        stack.jfxSnackbar
                            .show(getString(R.string_name_taken), OpenPssApp.DURATION_SHORT)
                    else -> {
                        val price = newPrice(name!!)
                        price.id = schema.insert(price)
                        table.items.add(price)
                    }
                }
            }
        }
}
