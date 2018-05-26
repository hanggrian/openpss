package com.hendraanggrian.openpss.ui.wage

import com.hendraanggrian.openpss.R
import com.hendraanggrian.openpss.control.dialog.TableDialog
import com.hendraanggrian.openpss.db.schemas.Employee
import com.hendraanggrian.openpss.db.schemas.Recess
import com.hendraanggrian.openpss.db.schemas.Recesses
import com.hendraanggrian.openpss.db.transaction
import com.hendraanggrian.openpss.i18n.Resourced
import com.hendraanggrian.openpss.util.PATTERN_TIME
import com.hendraanggrian.openpss.util.stringCell

class EditRecessDialog(
    resourced: Resourced,
    employee: Employee
) : TableDialog<Recess, Recesses>(Recesses, resourced, employee, R.string.recess, R.image.header_recess) {

    init {
        getString(R.string.start)<String> {
            stringCell { start.toString(PATTERN_TIME) }
        }
        getString(R.string.end)<String> {
            stringCell { end.toString(PATTERN_TIME) }
        }
    }

    override fun add() = AddRecessPopover(this).showAt(addButton) { (start, end) ->
        val recess = Recess(start, end)
        recess.id = transaction { Recesses.insert(recess) }
        table.items.add(recess)
    }
}