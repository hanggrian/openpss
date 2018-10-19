package com.hendraanggrian.openpss.ui.wage

import com.hendraanggrian.openpss.PATTERN_TIME
import com.hendraanggrian.openpss.R
import com.hendraanggrian.openpss.control.stringCell
import com.hendraanggrian.openpss.db.schemas.Employee
import com.hendraanggrian.openpss.db.schemas.Recess
import com.hendraanggrian.openpss.db.schemas.Recesses
import com.hendraanggrian.openpss.db.transaction
import com.hendraanggrian.openpss.i18n.Resourced
import com.hendraanggrian.openpss.popup.dialog.TableDialog

class EditRecessDialog(
    resourced: Resourced,
    employee: Employee
) : TableDialog<Recess, Recesses>(resourced, R.string.recess, Recesses, employee) {

    init {
        getString(R.string.start)<String> {
            stringCell { start.toString(PATTERN_TIME) }
        }
        getString(R.string.end)<String> {
            stringCell { end.toString(PATTERN_TIME) }
        }
    }

    override fun add() = AddRecessPopover(this).show(addButton) { (start, end) ->
        val recess = Recess(start, end)
        recess.id = transaction { Recesses.insert(recess) }
        table.items.add(recess)
    }
}