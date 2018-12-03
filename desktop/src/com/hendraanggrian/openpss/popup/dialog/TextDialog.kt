package com.hendraanggrian.openpss.popup.dialog

import com.hendraanggrian.openpss.content.FxComponent
import ktfx.layouts.label

class TextDialog(
    component: FxComponent,
    titleId: String,
    content: String = ""
) : Dialog(component, titleId) {

    init {
        label {
            isWrapText = true
            text = content
        }
    }
}