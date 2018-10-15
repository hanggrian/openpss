package com.hendraanggrian.openpss.popup.dialog

import com.hendraanggrian.openpss.i18n.Resourced
import ktfx.NodeManager
import ktfx.application.later
import ktfx.layouts.label

class TextDialog(
    resourced: Resourced,
    titleId: String,
    private val contentId: String
) : Dialog(resourced, titleId) {

    override fun onCreate(manager: NodeManager) {
        super.onCreate(manager)
        manager.run {
            label {
                later {
                    text = getString(contentId)
                }
                isWrapText = true
            }
        }
    }
}