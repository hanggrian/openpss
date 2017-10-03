@file:JvmName("ToggleButtonsKt")
@file:Suppress("NOTHING_TO_INLINE")

package com.wijayaprinting.javafx.scene.utils

import javafx.scene.control.ToggleButton
import javafx.scene.image.Image
import javafx.scene.image.ImageView
import kotfx.bindings.bindingOf

inline fun ToggleButton.attachButtons(selected: String, unselected: String) = attachButtons(Image(selected), Image(unselected))

inline fun ToggleButton.attachButtons(selected: Image, unselected: Image) {
    graphic = ImageView()
    (graphic as ImageView).imageProperty().bind(bindingOf(selectedProperty()) {
        when {
            isSelected -> selected
            else -> unselected
        }
    })
}