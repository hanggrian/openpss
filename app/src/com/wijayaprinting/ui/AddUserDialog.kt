package com.wijayaprinting.ui

import com.wijayaprinting.R
import javafx.scene.control.TextInputDialog
import javafx.scene.image.ImageView
import kotfx.dialogs.graphicIcon
import kotfx.dialogs.headerTitle

class AddUserDialog(resourced: Resourced, header: String) : TextInputDialog(), Resourced by resourced {

    init {
        headerTitle = header
        graphicIcon = ImageView(R.image.ic_user)
        contentText = getString(R.string.name)
    }
}