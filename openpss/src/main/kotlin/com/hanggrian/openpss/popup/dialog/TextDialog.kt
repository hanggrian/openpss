package com.hanggrian.openpss.popup.dialog

import com.hanggrian.openpss.Context
import ktfx.layouts.label

class TextDialog(context: Context, titleId: String, content: String = "") :
    Dialog(context, titleId) {
    init {
        label {
            isWrapText = true
            text = content
        }
    }
}
