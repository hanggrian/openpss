@file:Suppress("NOTHING_TO_INLINE")

package com.hendraanggrian.openpss.control

import javafx.scene.Node
import javafx.scene.layout.Region
import ktfx.layouts.LayoutDsl
import ktfx.layouts.LayoutManager

open class Space @JvmOverloads constructor(width: Double = 0.0, height: Double = 0.0) : Region() {

    init {
        minWidth = width
        minHeight = height
    }
}

/** Creates a [Space]. */
fun space(
    width: Double = 0.0,
    height: Double = 0.0,
    init: ((@LayoutDsl Space).() -> Unit)? = null
): Space = Space(width, height).also {
    init?.invoke(it)
}

/** Creates a [Space] and add it to this [LayoutManager]. */
inline fun LayoutManager<Node>.space(
    width: Double = 0.0,
    height: Double = 0.0,
    noinline init: ((@LayoutDsl Space).() -> Unit)? = null
): Space = (com.hendraanggrian.openpss.control.space(
    width,
    height,
    init
))()