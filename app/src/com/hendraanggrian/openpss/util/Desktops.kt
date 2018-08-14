package com.hendraanggrian.openpss.util

import com.hendraanggrian.openpss.R
import javafxx.scene.control.styledErrorAlert
import java.awt.Desktop

/** Global [Desktop] instance, may be null if it is unsupported. */
val desktop: Desktop?
    get() {
        if (!Desktop.isDesktopSupported()) {
            styledErrorAlert(getStyle(R.style.openpss), "Desktop is not supported.").show()
            return null
        }
        return Desktop.getDesktop()
    }