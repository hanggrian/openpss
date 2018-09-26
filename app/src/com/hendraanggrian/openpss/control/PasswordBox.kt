package com.hendraanggrian.openpss.control

import com.hendraanggrian.openpss.R
import com.hendraanggrian.openpss.i18n.Resourced
import javafx.beans.property.StringProperty
import javafx.scene.control.PasswordField
import javafx.scene.control.TextField
import javafx.scene.image.Image
import ktfx.beans.binding.`when`
import ktfx.beans.binding.otherwise
import ktfx.beans.binding.then
import ktfx.beans.value.getValue
import ktfx.beans.value.setValue
import ktfx.layouts._HBox
import ktfx.layouts.pane
import ktfx.layouts.passwordField
import ktfx.layouts.textField
import ktfx.layouts.toggleButton
import ktfx.layouts.tooltip

class PasswordBox(resourced: Resourced) : _HBox(R.dimen.padding_medium.toDouble()), Resourced by resourced {

    private lateinit var field1: PasswordField
    private lateinit var field2: TextField

    init {
        pane {
            field1 = passwordField {
                promptText = getString(R.string.password)
            }
            field2 = textField {
                promptText = getString(R.string.password)
                isVisible = false
            }
            field1.textProperty().bindBidirectional(field2.textProperty())
        }
        toggleButton {
            tooltip(getString(R.string.view_password))
            graphic = ktfx.layouts.imageView {
                imageProperty().bind(
                    `when`(this@toggleButton.selectedProperty())
                        then Image(R.image.btn_visibility_on_light)
                        otherwise Image(R.image.btn_visibility_off_light)
                )
            }
            field1.visibleProperty().bind(!selectedProperty())
            field2.visibleProperty().bind(selectedProperty())
        }
    }

    fun textProperty(): StringProperty = field1.textProperty()
    var text: String by textProperty()

    override fun requestFocus() = when {
        field1.isVisible -> field1.requestFocus()
        else -> field2.requestFocus()
    }
}