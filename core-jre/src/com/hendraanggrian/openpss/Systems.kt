package com.hendraanggrian.openpss

import org.apache.commons.lang3.SystemUtils

inline fun ifMacOS(action: () -> Unit) {
    if (SystemUtils.IS_OS_MAC_OSX) action()
}

inline fun ifNotMacOS(action: () -> Unit) {
    if (!SystemUtils.IS_OS_MAC_OSX) action()
}