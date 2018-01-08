package com.wijayaprinting.io

import com.wijayaprinting.utils.hideOnWindows
import java.io.File

/** Auto-creating folder (if not already created) for every new instance. */
class PropertiesFolder : File("${System.getProperty("user.home")}$separator.wijayaprinting") {
    init {
        mkdirs()
        hideOnWindows()
    }
}