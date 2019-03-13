@file:Suppress("NOTHING_TO_INLINE")

package com.hendraanggrian.openpss.control

import com.jfoenix.effects.JFXDepthManager
import ktfx.jfoenix._JFXToolbar
import ktfx.layouts.LayoutMarker
import ktfx.layouts.NodeManager

class Toolbar : _JFXToolbar() {

    init {
        JFXDepthManager.setDepth(this, 0)
    }
}

fun toolbar(
    init: ((@LayoutMarker Toolbar).() -> Unit)? = null
): Toolbar = Toolbar().also { init?.invoke(it) }

inline fun NodeManager.toolbar(
    noinline init: ((@LayoutMarker Toolbar).() -> Unit)? = null
): Toolbar = com.hendraanggrian.openpss.control.toolbar(init).add()