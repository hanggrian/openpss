package com.hendraanggrian.openpss.popup.dialog

import com.hendraanggrian.openpss.R
import com.hendraanggrian.openpss.content.Context
import ktfx.layouts.label

class ConfirmDialog(
    context: Context,
    textId: String? = null
) : ResultableDialog<Unit>(context, R.string.are_you_sure) {

    init {
        textId?.let { label(getString(it)) }
        cancelButton.text = getString(R.string.no)
        defaultButton.text = getString(R.string.yes)
    }
}
