package com.hendraanggrian.openpss.ui

import com.hendraanggrian.openpss.R
import javafx.scene.control.TextInputDialog
import javafx.scene.image.ImageView
import kotfx.scene.control.graphicIcon
import kotfx.scene.control.headerTitle

class AddUserDialog(resourced: Resourced, header: String) : TextInputDialog(), Resourced by resourced {

    init {
        headerTitle = header
        graphicIcon = ImageView(R.image.ic_user)
        contentText = getString(R.string.name)
    }
}