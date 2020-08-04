package com.hendraanggrian.openpss.popup

import com.hendraanggrian.openpss.R
import com.jfoenix.controls.JFXButton
import javafx.scene.control.Button
import ktfx.jfoenix.jfxButton

/** Defines a popup component that expects result to be returned. */
interface ResultablePopup<T> : Popup {

    var defaultButton: Button

    /**
     * @return result of the component.
     */
    val nullableResult: T? get() = null

    override fun initialize() {
        super.initialize()
        buttonInvokable.run {
            defaultButton = jfxButton(getString(R.string.ok)) {
                isDefaultButton = true
                styleClass += R.style.raised
                buttonType = JFXButton.ButtonType.RAISED
            }
        }
    }
}
