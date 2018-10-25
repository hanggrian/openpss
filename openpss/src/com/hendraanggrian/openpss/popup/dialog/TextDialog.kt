package com.hendraanggrian.openpss.popup.dialog

import com.hendraanggrian.openpss.i18n.Resourced
import ktfx.layouts.label

class TextDialog(
    resourced: Resourced,
    titleId: String,
    content: String = ""
) : Dialog(resourced, titleId) {

    init {
        label {
            isWrapText = true
            text = content
        }
    }
}