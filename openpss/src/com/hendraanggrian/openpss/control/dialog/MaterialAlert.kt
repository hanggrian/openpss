package com.hendraanggrian.openpss.control.dialog

import com.hendraanggrian.openpss.i18n.Resourced
import javafx.scene.Node
import ktfx.application.later
import ktfx.layouts.LayoutManager
import ktfx.layouts.label

class MaterialAlert(
    resourced: Resourced,
    titleId: String,
    private val contentId: String
) : MaterialDialog(resourced, titleId) {

    override fun LayoutManager<Node>.onCreate() {
        label {
            later {
                text = getString(contentId)
            }
            isWrapText = true
        }
    }
}