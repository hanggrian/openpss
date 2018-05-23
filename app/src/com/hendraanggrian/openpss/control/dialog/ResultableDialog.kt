package com.hendraanggrian.openpss.control.dialog

import com.hendraanggrian.openpss.control.Resultable
import com.hendraanggrian.openpss.localization.Resourced
import javafx.scene.control.ButtonBar

abstract class ResultableDialog<T>(
    resourced: Resourced,
    headerId: String? = null,
    graphicId: String? = null
) : Dialog<T>(resourced, headerId, graphicId), Resultable<T> {

    init {
        setResultConverter {
            if (it.buttonData == ButtonBar.ButtonData.OK_DONE) null
            else optionalResult
        }
    }
}