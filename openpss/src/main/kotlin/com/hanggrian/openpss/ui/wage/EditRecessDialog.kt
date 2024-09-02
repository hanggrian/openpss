package com.hanggrian.openpss.ui.wage

import com.hanggrian.openpss.Context
import com.hanggrian.openpss.PATTERN_TIME
import com.hanggrian.openpss.R
import com.hanggrian.openpss.db.schemas.Recess
import com.hanggrian.openpss.db.schemas.Recesses
import com.hanggrian.openpss.db.schemas.Recesses.end
import com.hanggrian.openpss.db.schemas.Recesses.start
import com.hanggrian.openpss.db.transaction
import com.hanggrian.openpss.popup.dialog.TableDialog
import com.hanggrian.openpss.util.stringCell
import ktfx.controls.TableColumnScope

class EditRecessDialog(context: Context) :
    TableDialog<Recess, Recesses>(context, R.string_recess, Recesses) {
    override fun onColumns(columns: TableColumnScope<Recess>) {
        super.onColumns(columns)

        columns.append(getString(R.string_start)) {
            stringCell { start.toString(PATTERN_TIME) }
        }
        columns.append(getString(R.string_end)) {
            stringCell { end.toString(PATTERN_TIME) }
        }
    }

    override fun add() =
        AddRecessPopover(this).show(addButton) { pair ->
            val recess = Recess(pair!!.first, pair.second)
            recess.id = transaction { Recesses.insert(recess) }
            table.items.add(recess)
        }
}
