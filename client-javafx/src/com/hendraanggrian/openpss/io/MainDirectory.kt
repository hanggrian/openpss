package com.hendraanggrian.openpss.io

import com.hendraanggrian.openpss.internal.CommonJvmBuildConfig
import org.apache.commons.lang3.SystemUtils

object MainDirectory : Directory(SystemUtils.USER_HOME, ".${CommonJvmBuildConfig.ARTIFACT}")