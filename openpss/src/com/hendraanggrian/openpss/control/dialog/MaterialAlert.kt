package com.hendraanggrian.openpss.control.dialog

import com.hendraanggrian.openpss.i18n.Resourced
import ktfx.NodeManager
import ktfx.application.later
import ktfx.layouts.label

class MaterialAlert(
    resourced: Resourced,
    titleId: String,
    private val contentId: String
) : MaterialDialog(resourced, titleId) {

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