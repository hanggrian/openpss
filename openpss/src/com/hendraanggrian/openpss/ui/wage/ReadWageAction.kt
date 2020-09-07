package com.hendraanggrian.openpss.ui.wage

import com.hendraanggrian.openpss.content.Action
import com.hendraanggrian.openpss.content.Context
import com.hendraanggrian.openpss.db.SessionWrapper

class ReadWageAction(context: Context) : Action<Unit>(context, true) {

    override fun SessionWrapper.handle() {}
}
