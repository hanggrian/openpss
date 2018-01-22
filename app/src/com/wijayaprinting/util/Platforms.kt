@file:Suppress("NOTHING_TO_INLINE")

package com.wijayaprinting.util

import javafx.application.Platform

inline fun forceExit() {
    Platform.exit()
    System.exit(0)
}