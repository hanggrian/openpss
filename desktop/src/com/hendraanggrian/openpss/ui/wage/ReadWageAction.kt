package com.hendraanggrian.openpss.ui.wage

import com.hendraanggrian.openpss.R
import com.hendraanggrian.openpss.content.Action
import com.hendraanggrian.openpss.content.FxComponent
import com.hendraanggrian.openpss.db.SessionWrapper

class ReadWageAction(component: FxComponent) : Action<Unit>(component, true) {

    override val log: String = getString(R.string._log_wage_read)

    override fun SessionWrapper.handle() {}
}