@file:Suppress("NOTHING_TO_INLINE")

package com.hendraanggrian.openpss.util

import com.hendraanggrian.openpss.R
import javafx.application.Platform
import javafxx.scene.control.styledErrorAlert
import java.awt.Desktop

/** Because sometimes [Platform.exit] is not enough. */
inline fun forceExit() {
    Platform.exit()
    System.exit(0)
}

/** Global [Desktop] instance, may be null if it is unsupported. */
val desktop: Desktop?
    get() {
        if (!Desktop.isDesktopSupported()) {
            styledErrorAlert(getStyle(R.style.openpss), "java.awt.Desktop is not supported.").show()
            return null
        }
        return Desktop.getDesktop()
    }