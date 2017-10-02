package com.wijayaprinting.javafx.control.button

import javafx.scene.control.Button
import javafx.scene.image.Image
import javafx.scene.image.ImageView

/**
 * @author Hendra Anggrian (hendraanggrian@gmail.com)
 */
class ImageButton(image: Image) : Button("", ImageView(image)) {

    constructor(image: String) : this(Image(image))
}