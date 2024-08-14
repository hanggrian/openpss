package com.hanggrian.openpss.popup

import com.hanggrian.openpss.R
import com.jfoenix.controls.JFXButton
import javafx.scene.control.Button
import ktfx.jfoenix.layouts.styledJfxButton

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
            defaultButton =
                styledJfxButton(getString(R.string_ok), null, R.style_raised) {
                    isDefaultButton = true
                    buttonType = JFXButton.ButtonType.RAISED
                }
        }
    }
}
