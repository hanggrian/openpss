@file:Suppress("NOTHING_TO_INLINE")

package com.hendraanggrian.openpss.control

import javafx.beans.property.ObjectProperty
import javafx.scene.image.Image
import javafx.scene.image.ImageView
import ktfx.layouts.LayoutMarker
import ktfx.layouts.NodeManager
import ktfx.layouts._BorderPane
import ktfx.layouts.imageView

class MarginedImageView : _BorderPane() {

    private val image: ImageView = imageView()

    fun imageProperty(): ObjectProperty<Image> = image.imageProperty()

    var topMargin: Double
        get() = (top as Space).height
        set(value) {
            top = com.hendraanggrian.openpss.control.space(height = value)
        }

    var rightMargin: Double
        get() = (right as Space).width
        set(value) {
            right = com.hendraanggrian.openpss.control.space(width = value)
        }

    var bottomMargin: Double
        get() = (bottom as Space).height
        set(value) {
            bottom = com.hendraanggrian.openpss.control.space(height = value)
        }

    var leftMargin: Double
        get() = (left as Space).width
        set(value) {
            left = com.hendraanggrian.openpss.control.space(width = value)
        }
}

fun marginedImageView(
    init: ((@LayoutMarker MarginedImageView).() -> Unit)? = null
): MarginedImageView = MarginedImageView().also { init?.invoke(it) }

inline fun NodeManager.marginedImageView(
    noinline init: ((@LayoutMarker MarginedImageView).() -> Unit)? = null
): MarginedImageView = com.hendraanggrian.openpss.control.marginedImageView(init).add()