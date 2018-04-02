@file:Suppress("NOTHING_TO_INLINE", "UNUSED")

package com.hendraanggrian.openpss.scene.control

import javafx.scene.Node
import javafx.scene.layout.Region
import ktfx.layouts.LayoutDsl
import ktfx.layouts.LayoutManager

class Gap(width: Int = 48) : Region() {

    init {
        minWidth = width.toDouble()
    }
}

inline fun gap(): Gap = gap { }

inline fun gap(
    init: (@LayoutDsl Gap).() -> Unit
): Gap = Gap().apply(init)

inline fun LayoutManager<Node>.gap(): Gap = gap { }

inline fun LayoutManager<Node>.gap(
    init: (@LayoutDsl Gap).() -> Unit
): Gap = com.hendraanggrian.openpss.scene.control.gap(init).add()