package com.hendraanggrian.openpss.control

import javafx.beans.property.ObjectProperty
import javafx.scene.image.Image
import javafx.scene.image.ImageView
import ktfx.layouts.KtfxBorderPane
import ktfx.layouts.imageView

class MarginedImageView : KtfxBorderPane() {

    private val image: ImageView = imageView()

    fun imageProperty(): ObjectProperty<Image> = image.imageProperty()

    var topMargin: Double
        get() = (top as Space).height
        set(value) {
            top = Space(height = value)
        }

    var rightMargin: Double
        get() = (right as Space).width
        set(value) {
            right = Space(width = value)
        }

    var bottomMargin: Double
        get() = (bottom as Space).height
        set(value) {
            bottom = Space(height = value)
        }

    var leftMargin: Double
        get() = (left as Space).width
        set(value) {
            left = Space(width = value)
        }
}
