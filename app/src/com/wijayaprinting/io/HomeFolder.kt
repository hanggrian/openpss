package com.wijayaprinting.io

import com.wijayaprinting.BuildConfig.ARTIFACT
import org.apache.commons.lang3.SystemUtils.USER_HOME
import java.io.File

/** Auto-creating folder (if not already created) for every new instance. */
class HomeFolder : File("$USER_HOME$separator.$ARTIFACT") {

    init {
        mkdirs()
    }
}