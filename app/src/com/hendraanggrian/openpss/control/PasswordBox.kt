package com.hendraanggrian.openpss.control

import com.hendraanggrian.openpss.R
import com.hendraanggrian.openpss.localization.Resourced
import javafx.beans.property.StringProperty
import javafx.scene.control.PasswordField
import javafx.scene.control.TextField
import javafx.scene.image.Image
import ktfx.beans.binding.`when`
import ktfx.beans.binding.otherwise
import ktfx.beans.binding.then
import ktfx.beans.value.getValue
import ktfx.beans.value.setValue
import ktfx.coroutines.listener
import ktfx.layouts._HBox
import ktfx.layouts.anchorPane
import ktfx.layouts.passwordField
import ktfx.layouts.textField
import ktfx.layouts.toggleButton
import ktfx.layouts.tooltip

class PasswordBox(resourced: Resourced) : _HBox(8.0), Resourced by resourced {

    private lateinit var field1: PasswordField
    private lateinit var field2: TextField

    init {
        anchorPane {
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
            graphic = ktfx.layouts.imageView {
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

    override fun requestFocus() = field1.requestFocus()
}