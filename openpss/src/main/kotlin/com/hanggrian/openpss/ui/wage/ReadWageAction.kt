package com.hanggrian.openpss.ui.wage

import com.hanggrian.openpss.Action
import com.hanggrian.openpss.Context
import com.hanggrian.openpss.db.ExtendedSession

class ReadWageAction(context: Context) : Action<Unit>(context, true) {
    override fun ExtendedSession.handle() {}
}
