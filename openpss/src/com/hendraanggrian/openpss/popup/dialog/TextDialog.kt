package com.hendraanggrian.openpss.popup.dialog

import com.hendraanggrian.openpss.i18n.Resourced
import ktfx.layouts.label

class TextDialog(
    resourced: Resourced,
    titleId: String,
    private val contentId: String
) : Dialog(resourced, titleId) {

    init {
        label {
            text = getString(contentId)
            isWrapText = true
        }
    }
}