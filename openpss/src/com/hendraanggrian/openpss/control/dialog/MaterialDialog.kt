package com.hendraanggrian.openpss.control.dialog

import com.hendraanggrian.openpss.App
import com.hendraanggrian.openpss.R
import com.hendraanggrian.openpss.i18n.Resourced
import com.hendraanggrian.openpss.lifecycle.Lifecylce
import com.hendraanggrian.openpss.util.getColor
import com.jfoenix.controls.JFXDialog
import ktfx.coroutines.onAction
import ktfx.jfoenix.jfxButton
import ktfx.layouts.buttonBar
import ktfx.layouts.label
import ktfx.scene.layout.paddingAll
import ktfx.scene.text.fontSize

open class MaterialDialog(
    resourced: Resourced,
    titleId: String
) : JFXDialog(), Lifecylce, Resourced by resourced {

    init {
        content = ktfx.layouts.vbox(R.dimen.padding_medium.toDouble()) {
            paddingAll = R.dimen.padding_large.toDouble()
            label(getString(titleId)) {
                fontSize = 18.0
                textFill = getColor(R.color.blue)
            }
            onCreate(this)
            buttonBar {
                jfxButton(getString(R.string.close)) {
                    styleClass += App.STYLE_BUTTON_FLAT
                    isCancelButton = true
                    onAction { close() }
                }
                onCreateActions(this)
            } marginTop R.dimen.padding_medium.toDouble()
        }
    }
}