package com.hendraanggrian.openpss.ui.wage

import com.hendraanggrian.openpss.Context
import com.hendraanggrian.openpss.PATTERN_TIME
import com.hendraanggrian.openpss.R
import com.hendraanggrian.openpss.control.stringCell
import com.hendraanggrian.openpss.db.schemas.Recess
import com.hendraanggrian.openpss.db.schemas.Recesses
import com.hendraanggrian.openpss.db.transaction
import com.hendraanggrian.openpss.popup.dialog.TableDialog

class EditRecessDialog(context: Context) : TableDialog<Recess, Recesses>(context, R.string.recess, Recesses) {

    init {
        getString(R.string.start)<String> {
            stringCell { start.toString(PATTERN_TIME) }
        }
        getString(R.string.end)<String> {
            stringCell { end.toString(PATTERN_TIME) }
        }
    }

    override fun add() = AddRecessPopover(this).show(addButton) { pair ->
        val recess = Recess(pair!!.first, pair.second)
        recess.id = transaction { Recesses.insert(recess) }
        table.items.add(recess)
    }
}