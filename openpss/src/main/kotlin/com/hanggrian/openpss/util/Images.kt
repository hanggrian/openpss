package com.hanggrian.openpss.util

import java.awt.image.BufferedImage
import java.awt.image.BufferedImage.TYPE_INT_RGB

fun Iterable<BufferedImage>.concatenate(imageType: Int = TYPE_INT_RGB): BufferedImage {
    val image = BufferedImage(firstOrNull()?.width ?: 0, sumBy { it.height }, imageType)
    var y = 0
    forEach {
        image.graphics.drawImage(it, 0, y, null)
        y += it.height
    }
    return image
}
