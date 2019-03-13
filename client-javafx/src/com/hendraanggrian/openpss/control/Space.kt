@file:Suppress("NOTHING_TO_INLINE")

package com.hendraanggrian.openpss.control

import javafx.scene.layout.Region
import ktfx.layouts.LayoutMarker
import ktfx.layouts.NodeManager

open class Space @JvmOverloads constructor(width: Double = 0.0, height: Double = 0.0) : Region() {

    init {
        minWidth = width
        minHeight = height
    }
}

fun space(
    width: Double = 0.0,
    height: Double = 0.0,
    init: ((@LayoutMarker Space).() -> Unit)? = null
): Space = Space(width, height).also { init?.invoke(it) }

inline fun NodeManager.space(
    width: Double = 0.0,
    height: Double = 0.0,
    noinline init: ((@LayoutMarker Space).() -> Unit)? = null
): Space = com.hendraanggrian.openpss.control.space(width, height, init).add()