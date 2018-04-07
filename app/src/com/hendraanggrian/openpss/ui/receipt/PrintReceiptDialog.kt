package com.hendraanggrian.openpss.ui.receipt

import com.hendraanggrian.openpss.R
import com.hendraanggrian.openpss.db.schema.Receipt
import com.hendraanggrian.openpss.ui.Resourced
import javafx.scene.control.Dialog
import javafx.scene.image.ImageView
import ktfx.layouts.gridPane
import ktfx.scene.control.graphicIcon
import ktfx.scene.control.headerTitle

class PrintReceiptDialog(resourced: Resourced, receipt: Receipt) : Dialog<Boolean>(), Resourced by resourced {

    init {
        headerTitle = getString(R.string.print_receipt)
        graphicIcon = ImageView(R.image.ic_print)
        dialogPane.content = gridPane {

        }
    }
}