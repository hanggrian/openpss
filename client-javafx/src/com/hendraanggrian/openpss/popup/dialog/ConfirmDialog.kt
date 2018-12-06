package com.hendraanggrian.openpss.popup.dialog

import com.hendraanggrian.openpss.R
import com.hendraanggrian.openpss.content.FxComponent
import ktfx.layouts.label

class ConfirmDialog(
    component: FxComponent,
    textId: String? = null
) : ResultableDialog<Unit>(component, R.string.are_you_sure) {

    init {
        textId?.let { label(getString(it)) }
        cancelButton.text = getString(R.string.no)
        defaultButton.text = getString(R.string.yes)
    }
}