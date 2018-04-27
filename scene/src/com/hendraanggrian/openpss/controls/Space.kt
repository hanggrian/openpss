@file:Suppress("NOTHING_TO_INLINE", "UNUSED")

package com.hendraanggrian.openpss.controls

import javafx.scene.Node
import javafx.scene.layout.Region
import ktfx.layouts.LayoutDsl
import ktfx.layouts.LayoutManager

class Space @JvmOverloads constructor(width: Int = 32) : Region() {

    init {
        minWidth = width.toDouble()
    }
}

inline fun space(): Space = space { }

inline fun space(
    init: (@LayoutDsl Space).() -> Unit
): Space = Space().apply(init)

inline fun LayoutManager<Node>.space(): Space = space { }

inline fun LayoutManager<Node>.space(
    init: (@LayoutDsl Space).() -> Unit
): Space = com.hendraanggrian.openpss.controls.space(init).add()