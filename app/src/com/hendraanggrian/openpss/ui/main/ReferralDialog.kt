package com.hendraanggrian.openpss.ui.main

import com.hendraanggrian.openpss.R
import com.hendraanggrian.openpss.i18n.Resourced
import javafx.scene.control.Dialog
import javafx.scene.image.ImageView
import ktfx.scene.control.graphicIcon
import ktfx.scene.control.headerTitle

class ReferralDialog(resourced: Resourced) : Dialog<Nothing>(), Resourced by resourced {

    init {
        headerTitle = "Referral required"
        graphicIcon = ImageView(R.image.header_change_password)
    }
}