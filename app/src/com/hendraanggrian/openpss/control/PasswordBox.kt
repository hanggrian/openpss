package com.hendraanggrian.openpss.control

import com.hendraanggrian.openpss.R
import com.hendraanggrian.openpss.i18n.Resourced
import javafx.beans.property.StringProperty
import javafx.scene.control.PasswordField
import javafx.scene.control.TextField
import javafx.scene.image.Image
import javafxx.beans.binding.`when`
import javafxx.beans.binding.otherwise
import javafxx.beans.binding.then
import javafxx.beans.value.getValue
import javafxx.beans.value.setValue
import javafxx.coroutines.listener
import javafxx.layouts._HBox
import javafxx.layouts.pane
import javafxx.layouts.passwordField
import javafxx.layouts.textField
import javafxx.layouts.toggleButton
import javafxx.layouts.tooltip

class PasswordBox(resourced: Resourced) : _HBox(R.dimen.padding_small.toDouble()), Resourced by resourced {

    private lateinit var field1: PasswordField
    private lateinit var field2: TextField

    init {
        pane {
            field1 = passwordField {
                promptText = getString(R.string.password)
            }
            field2 = textField {
                isVisible = false
                promptText = getString(R.string.password)
            }
        }
        toggleButton {
            tooltip(getString(R.string.view_password))
            graphic = javafxx.layouts.imageView {
                imageProperty().bind(`when`(this@toggleButton.selectedProperty())
                    then Image(R.image.btn_visibility_on_light)
                    otherwise Image(R.image.btn_visibility_off_light))
            }
            selectedProperty().listener { _, _, selected ->
                field1.isVisible = !selected
                field2.isVisible = selected
            }
        }
        field1.textProperty().bindBidirectional(field2.textProperty())
    }

    fun textProperty(): StringProperty = field1.textProperty()
    var text: String by textProperty()

    override fun requestFocus() = when {
        field1.isVisible -> field1.requestFocus()
        else -> field2.requestFocus()
    }
}