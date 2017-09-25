package com.wijayaprinting.javafx.control

import javafx.scene.control.ToggleButton
import javafx.scene.image.Image
import javafx.scene.image.ImageView
import kotfx.bindings.bindingOf

/**
 * @author Hendra Anggrian (hendraanggrian@gmail.com)
 */
class ImageToggleButton(selected: Image, unselected: Image) : ToggleButton() {

    constructor(selected: String, unselected: String) : this(Image(selected), Image(unselected))

    init {
        graphic = ImageView()
        (graphic as ImageView).imageProperty().bind(bindingOf(selectedProperty()) {
            when {
                isSelected -> selected
                else -> unselected
            }
        })
    }
}