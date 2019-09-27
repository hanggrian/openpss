package com.hendraanggrian.openpss.ui.price

import com.hendraanggrian.openpss.FxComponent
import com.hendraanggrian.openpss.R2
import com.hendraanggrian.openpss.nosql.NamedDocument
import com.hendraanggrian.openpss.ui.InputPopOver
import com.hendraanggrian.openpss.ui.TableDialog
import com.hendraanggrian.openpss.util.stringCell
import kotlinx.coroutines.CoroutineScope

abstract class EditPriceDialog<D : NamedDocument<*>>(component: FxComponent, headerId: String) :
    TableDialog<D>(component, headerId) {

    init {
        @Suppress("LeakingThis")
        getString(R2.string.name)<String> {
            minWidth = 96.0
            stringCell { name }
        }
    }

    override fun add() = InputPopOver(
        this, when (this) {
            is EditPlatePriceDialog -> R2.string.add_plate_price
            is EditOffsetPriceDialog -> R2.string.add_offset_price
            else -> R2.string.add_digital_price
        }
    ).show(addButton) { name ->
        val add = add(name!!)
        if (add != null) {
            table.items.add(add)
        }
    }

    abstract suspend fun CoroutineScope.add(name: String): D?
}
