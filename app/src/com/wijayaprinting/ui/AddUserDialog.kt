package com.wijayaprinting.ui

import com.wijayaprinting.R
import com.wijayaprinting.ui.Resourced
import javafx.scene.control.TextInputDialog
import javafx.scene.image.ImageView

class AddUserDialog(resourced: Resourced, header: String) : TextInputDialog(), Resourced by resourced {

    init {
        title = header
        headerText = header
        graphic = ImageView(R.png.ic_user)
        contentText = getString(R.string.name)
    }
}