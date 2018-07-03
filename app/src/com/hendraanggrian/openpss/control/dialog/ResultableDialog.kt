package com.hendraanggrian.openpss.control.dialog

import com.hendraanggrian.openpss.control.Resultable
import com.hendraanggrian.openpss.i18n.Resourced
import javafx.scene.control.ButtonBar.ButtonData.OK_DONE

abstract class ResultableDialog<T>(
    resourced: Resourced,
    headerId: String? = null,
    graphicId: String? = null
) : Dialog<T>(resourced, headerId, graphicId), Resultable<T> {

    init {
        setResultConverter {
            when (it.buttonData) {
                OK_DONE -> optionalResult
                else -> null
            }
        }
    }
}