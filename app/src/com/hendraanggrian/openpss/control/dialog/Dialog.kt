package com.hendraanggrian.openpss.control.dialog

import com.hendraanggrian.openpss.i18n.Resourced
import com.hendraanggrian.openpss.util.getStyle
import javafx.scene.Node
import javafx.scene.control.Dialog
import javafx.scene.image.ImageView
import javafxx.layouts.LayoutManager
import javafxx.scene.control.graphicIcon
import javafxx.scene.control.headerTitle

open class Dialog<R>(
    resourced: Resourced,
    headerId: String? = null,
    graphicId: String? = null
) : Dialog<R>(), LayoutManager<Node>, Resourced by resourced {

    override fun <T : Node> T.invoke(): T = also { dialogPane.content = it }

    init {
        if (headerId != null) headerTitle = getString(headerId)
        if (graphicId != null) graphicIcon = ImageView(graphicId)
        dialogPane.stylesheets += getStyle(com.hendraanggrian.openpss.R.style.openpss)
    }

    final override fun getString(id: String): String = super.getString(id)
}