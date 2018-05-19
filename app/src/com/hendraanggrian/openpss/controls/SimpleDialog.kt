package com.hendraanggrian.openpss.controls

import com.hendraanggrian.openpss.localization.Resourced
import com.hendraanggrian.openpss.util.getStyle
import javafx.scene.Node
import javafx.scene.control.Dialog
import javafx.scene.image.ImageView
import ktfx.layouts.LayoutManager
import ktfx.scene.control.graphicIcon
import ktfx.scene.control.headerTitle

open class SimpleDialog<R>(
    resourced: Resourced,
    headerId: String? = null,
    graphicId: String? = null
) : Dialog<R>(), LayoutManager<Node>, Resourced by resourced {

    override fun <T : Node> T.add(): T = also { dialogPane.content = it }

    init {
        if (headerId != null) headerTitle = @Suppress("LeakingThis") getString(headerId)
        if (graphicId != null) graphicIcon = ImageView(graphicId)
        dialogPane.stylesheets += getStyle(com.hendraanggrian.openpss.R.style.openpss)
    }
}