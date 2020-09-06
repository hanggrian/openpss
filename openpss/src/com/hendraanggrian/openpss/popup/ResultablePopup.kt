package com.hendraanggrian.openpss.popup

import com.hendraanggrian.openpss.R
import com.jfoenix.controls.JFXButton
import javafx.scene.control.Button
import ktfx.jfoenix.layouts.styledJFXButton

/** Defines a popup component that expects result to be returned. */
interface ResultablePopup<T> : Popup {

    var defaultButton: Button

    /**
     * @return result of the component.
     */
    val nullableResult: T? get() = null

    override fun initialize() {
        super.initialize()
        buttonManager.run {
            defaultButton = styledJFXButton(getString(R.string.ok), null, R.style.raised) {
                isDefaultButton = true
                buttonType = JFXButton.ButtonType.RAISED
            }
        }
    }
}
