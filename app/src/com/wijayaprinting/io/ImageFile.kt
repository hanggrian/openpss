package com.wijayaprinting.io

import javafx.embed.swing.SwingFXUtils.fromFXImage
import javafx.scene.image.WritableImage
import java.io.File
import java.io.IOException
import javax.imageio.ImageIO.write

class ImageFile(suffix: Int) : File(HomeFolder(), ".img-$suffix.png") {

    @Throws(IOException::class)
    fun write(image: WritableImage): Boolean = write(fromFXImage(image, null), "png", this)
}