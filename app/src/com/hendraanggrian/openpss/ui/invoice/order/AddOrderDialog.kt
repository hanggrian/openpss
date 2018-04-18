package com.hendraanggrian.openpss.ui.invoice.order

import com.hendraanggrian.openpss.R
import com.hendraanggrian.openpss.controls.IntField
import com.hendraanggrian.openpss.controls.intField
import com.hendraanggrian.openpss.db.Order
import com.hendraanggrian.openpss.ui.Resourced
import com.hendraanggrian.openpss.utils.currencyConverter
import com.hendraanggrian.openpss.utils.getColor
import com.hendraanggrian.openpss.utils.getFont
import com.hendraanggrian.openpss.utils.style
import javafx.beans.Observable
import javafx.beans.value.ObservableBooleanValue
import javafx.scene.control.ButtonType
import javafx.scene.control.Dialog
import javafx.scene.control.TextField
import javafx.scene.image.ImageView
import ktfx.beans.binding.bindingOf
import ktfx.beans.binding.stringBindingOf
import ktfx.layouts._GridPane
import ktfx.layouts.gridPane
import ktfx.layouts.label
import ktfx.layouts.textField
import ktfx.scene.control.cancelButton
import ktfx.scene.control.graphicIcon
import ktfx.scene.control.headerTitle
import ktfx.scene.control.okButton
import ktfx.scene.layout.gap

@Suppress("LeakingThis")
abstract class AddOrderDialog<T : Order>(
    resourced: Resourced,
    titleId: String,
    graphicId: String? = null
) : Dialog<T>(), Resourced by resourced {

    abstract fun _GridPane.onLayout()

    abstract val titleBindingDependencies: Array<Observable>

    abstract val disableBinding: ObservableBooleanValue

    abstract fun newInstance(): T

    protected lateinit var titleField: TextField
    protected lateinit var qtyField: IntField

    init {
        style()
        headerTitle = getString(titleId)
        graphicId?.let { graphicIcon = ImageView(it) }
        dialogPane.content = gridPane {
            gap = 8.0
            label(getString(R.string.title)) col 0 row 0
            titleField = textField { promptText = getString(R.string.title) } col 1 row 0
            label(getString(R.string.qty)) col 0 row 1
            qtyField = intField { promptText = getString(R.string.qty) } col 1 row 1
            onLayout()
            (children.size / 2).let { totalRow ->
                label(getString(R.string.total)) col 0 row totalRow
                label {
                    font = getFont(R.font.sf_pro_text_bold)
                    textProperty().bind(stringBindingOf(*titleBindingDependencies) {
                        currencyConverter.toString(newInstance().total)
                    })
                    textFillProperty().bind(bindingOf(textProperty()) {
                        getColor(when {
                            newInstance().total > 0 -> R.color.teal
                            else -> R.color.red
                        })
                    })
                } col 1 row totalRow
            }
        }
        titleField.requestFocus()
        cancelButton()
        okButton().disableProperty().bind(disableBinding)
        setResultConverter { if (it == ButtonType.CANCEL) null else newInstance() }
    }
}