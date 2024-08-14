package com.hanggrian.openpss.popup.dialog

import com.hanggrian.openpss.Context
import com.hanggrian.openpss.R
import ktfx.layouts.label

class ConfirmDialog(context: Context, textId: String? = null) :
    ResultableDialog<Unit>(context, R.string_are_you_sure) {
    init {

        textId?.let { label(getString(it)) }
        cancelButton.text = getString(R.string_no)
        defaultButton.text = getString(R.string_yes)
    }
}
