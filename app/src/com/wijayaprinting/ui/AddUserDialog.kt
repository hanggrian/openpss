package com.wijayaprinting.ui

import com.wijayaprinting.R
import javafx.scene.control.TextInputDialog
import javafx.scene.image.Image
import javafx.scene.image.ImageView
import kotfx.dialogs.icon

class AddUserDialog(resourced: Resourced, header: String) : TextInputDialog(), Resourced by resourced {

    init {
        icon = Image(R.image.ic_user)
        title = header
        headerText = header
        graphic = ImageView(R.image.ic_user)
        contentText = getString(R.string.name)
    }
}