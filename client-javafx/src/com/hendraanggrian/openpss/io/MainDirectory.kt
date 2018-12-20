package com.hendraanggrian.openpss.io

import com.hendraanggrian.openpss.BuildConfig
import org.apache.commons.lang3.SystemUtils

object MainDirectory : Directory(SystemUtils.USER_HOME, ".${BuildConfig.ARTIFACT}")