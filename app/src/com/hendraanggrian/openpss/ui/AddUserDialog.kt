package com.hendraanggrian.openpss.ui

import com.hendraanggrian.openpss.R
import javafx.scene.control.TextInputDialog
import javafx.scene.image.Image
import javafx.scene.image.ImageView
import kotlinfx.scene.control.headerTitle
import kotlinfx.scene.control.icon

class AddUserDialog(resourced: Resourced, header: String) : TextInputDialog(), Resourced by resourced {

    init {
        headerTitle = header
        icon = Image(R.image.menu_user)
        graphic = ImageView(R.image.ic_user)
        contentText = getString(R.string.name)
    }
}