package com.hendraanggrian.openpss.ui.price

import com.hendraanggrian.openpss.R
import com.hendraanggrian.openpss.content.FxComponent
import com.hendraanggrian.openpss.db.Document
import com.hendraanggrian.openpss.db.Named
import com.hendraanggrian.openpss.popup.dialog.TableDialog
import com.hendraanggrian.openpss.popup.popover.InputPopover
import com.hendraanggrian.openpss.util.stringCell
import kotlinx.coroutines.CoroutineScope

abstract class EditPriceDialog<D>(
    component: FxComponent,
    headerId: String
) : TableDialog<D>(component, headerId)
    where D : Document<*>, D : Named {

    init {
        @Suppress("LeakingThis")
        getString(R.string.name)<String> {
            minWidth = 96.0
            stringCell { name }
        }
    }

    override fun add() = InputPopover(
        this, when (this) {
            is EditPlatePriceDialog -> R.string.add_plate_price
            is EditOffsetPriceDialog -> R.string.add_offset_price
            else -> R.string.add_digital_price
        }
    ).show(addButton) { name ->
        val add = add(name!!)
        if (add != null) {
            table.items.add(add)
        }
    }

    abstract suspend fun CoroutineScope.add(name: String): D?
}