package com.wijayaprinting.io

import javafx.embed.swing.SwingFXUtils.fromFXImage
import javafx.scene.image.WritableImage
import java.io.File
import java.io.IOException
import javax.imageio.ImageIO.write

class ImageFile @JvmOverloads constructor(
        prefix: String,
        suffix: Int,
        private val format: String = FORMAT_PNG
) : File(DesktopFolder, "$prefix-$suffix.$format") {

    @Throws(IOException::class)
    fun write(image: WritableImage): Boolean = write(fromFXImage(image, null), format, this)

    companion object {
        const val FORMAT_PNG = "png"
    }
}