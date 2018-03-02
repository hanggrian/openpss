package com.hendraanggrian.openpss.io

import javafx.embed.swing.SwingFXUtils.fromFXImage
import javafx.scene.image.WritableImage
import java.io.File
import java.io.IOException
import javax.imageio.ImageIO.write

class WageFile(count: Int) : File(WageContentFolder, "$count.png") {

    @Throws(IOException::class)
    fun write(image: WritableImage): Boolean = write(fromFXImage(image, null), "png", this)
}