package com.hendraanggrian.openpss.popup.dialog

import com.hendraanggrian.openpss.R
import com.hendraanggrian.openpss.i18n.Resourced
import ktfx.layouts.label

class ConfirmDialog(resourced: Resourced) : ResultableDialog<Unit>(resourced, R.string.are_you_sure) {

    init {
        label(resourced.getString(R.string.are_you_sure))
    }
}