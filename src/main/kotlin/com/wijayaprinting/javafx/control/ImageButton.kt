package com.wijayaprinting.javafx.control

import javafx.scene.control.Button
import javafx.scene.image.Image
import javafx.scene.image.ImageView

/**
 * @author Hendra Anggrian (hendraanggrian@gmail.com)
 */
open class ImageButton(image: Image) : Button("", ImageView(image)) {

    constructor(image: String) : this(Image(image))
}