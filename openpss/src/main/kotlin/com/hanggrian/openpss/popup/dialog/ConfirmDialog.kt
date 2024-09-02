package com.hanggrian.openpss.popup.dialog

import com.hanggrian.openpss.Context
import com.hanggrian.openpss.R
import ktfx.layouts.label

class ConfirmDialog(context: Context, text: String? = null) :
    ResultableDialog<Unit>(context, R.string__confirm) {
    init {
        text?.let { label(it) }
        cancelButton.text = getString(R.string_no)
        defaultButton.text = getString(R.string_yes)
    }
}
