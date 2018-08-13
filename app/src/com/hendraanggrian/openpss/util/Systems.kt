@file:Suppress("NOTHING_TO_INLINE")

package com.hendraanggrian.openpss.util

import javafx.application.Platform

/** Because sometimes [Platform.exit] is not enough. */
inline fun quit() {
    Platform.exit()
    System.exit(0)
}