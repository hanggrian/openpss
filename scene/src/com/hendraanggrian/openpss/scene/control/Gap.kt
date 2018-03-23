@file:Suppress("NOTHING_TO_INLINE", "UNUSED")

package com.hendraanggrian.openpss.scene.control

import javafx.scene.Node
import javafx.scene.layout.Region
import ktfx.layouts.LayoutDsl
import ktfx.layouts.LayoutManager
import ktfx.scene.layout.widthMin

class Gap(width: Int = 48) : Region() {

    init {
        widthMin = width
    }
}

inline fun gap(
    noinline init: ((@LayoutDsl Gap).() -> Unit)? = null
): Gap = Gap().apply { init?.invoke(this) }

inline fun LayoutManager<Node>.gap(
    noinline init: ((@LayoutDsl Gap).() -> Unit)? = null
): Gap = com.hendraanggrian.openpss.scene.control.gap(init).add()