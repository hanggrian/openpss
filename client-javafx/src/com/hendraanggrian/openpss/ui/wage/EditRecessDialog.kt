package com.hendraanggrian.openpss.ui.wage

import com.hendraanggrian.openpss.App
import com.hendraanggrian.openpss.PATTERN_TIME
import com.hendraanggrian.openpss.R
import com.hendraanggrian.openpss.content.FxComponent
import com.hendraanggrian.openpss.db.schemas.Recess
import com.hendraanggrian.openpss.popup.dialog.TableDialog
import com.hendraanggrian.openpss.util.stringCell
import kotlinx.coroutines.CoroutineScope
import ktfx.collections.toMutableObservableList

class EditRecessDialog(component: FxComponent) : TableDialog<Recess>(component, R.string.recess) {

    init {
        getString(R.string.start)<String> {
            stringCell { start.toString(PATTERN_TIME) }
        }
        getString(R.string.end)<String> {
            stringCell { end.toString(PATTERN_TIME) }
        }
    }

    override suspend fun CoroutineScope.refresh(): List<Recess> = App.API.getRecesses().toMutableObservableList()

    override fun add() = AddRecessPopover(this).show(addButton) { pair ->
        table.items.add(App.API.addRecess(pair!!.first, pair.second))
    }

    override suspend fun CoroutineScope.delete(selected: Recess): Boolean =
        App.API.deleteRecess(selected.start, selected.end)
}